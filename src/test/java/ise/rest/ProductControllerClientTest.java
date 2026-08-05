package ise.rest;

import ise.rest.client.ProductController;
import ise.rest.model.Product;
import ise.rest.server.ServerApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The WebFlux client against a REAL server on a random port.
 *
 * Because every call is non-blocking, the test cannot assert immediately after
 * calling the method -- the response has not arrived yet. A CountDownLatch is
 * released inside the callback and the test waits on it. That wait is also the proof
 * that the callback really was invoked: if the controller forgot to call it, await()
 * times out and the test fails.
 */
@SpringBootTest(classes = ServerApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductControllerClientTest {

    @LocalServerPort
    private int port;

    private ProductController controller;

    @BeforeEach
    void setUp() {
        controller = new ProductController("http://localhost:" + port);
    }

    /** Runs an async controller call and blocks the TEST (not the SUT) until it lands. */
    private List<Product> await(java.util.function.Consumer<Consumer<List<Product>>> call) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        List<Product>[] received = new List[1];

        call.accept(products -> {
            received[0] = products;
            latch.countDown();
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS),
                "the callback was never invoked -- the controller did not call it after the response");
        return received[0];
    }

    @Test
    @DisplayName("addProduct posts the product and hands the updated cache to the callback")
    void addProductUpdatesTheCache() throws Exception {
        List<Product> cache = await(cb -> controller.addProduct(new Product("Hoodie", 49.90, 5), cb));

        assertEquals(1, cache.size());
        // The id proves the object in the cache is the SERVER response, not the local
        // object that was posted -- that one still had a null id.
        assertEquals(1L, cache.get(0).getId());
        assertEquals("Hoodie", cache.get(0).getName());
        assertEquals(1, controller.getProducts().size());
    }

    @Test
    @DisplayName("getAllProducts replaces the cache instead of appending to it")
    void getAllProductsReplacesTheCache() throws Exception {
        await(cb -> controller.addProduct(new Product("Hoodie", 49.90, 5), cb));
        await(cb -> controller.addProduct(new Product("Scarf", 19.90, 3), cb));

        List<Product> cache = await(cb -> controller.getAllProducts(cb));

        // 2, not 4. A refresh that calls addAll without clearing first doubles the
        // cache on every call, and nothing but this exact number catches it.
        assertEquals(2, cache.size());
    }

    @Test
    @DisplayName("updateProduct replaces the cached copy in place")
    void updateProductUpdatesTheCache() throws Exception {
        await(cb -> controller.addProduct(new Product("Hoodie", 49.90, 5), cb));

        Product edited = controller.getProducts().get(0);
        edited.setName("Hoodie XL");
        edited.setPrice(59.90);

        List<Product> cache = await(cb -> controller.updateProduct(edited, cb));

        assertEquals(1, cache.size());   // replaced, not appended
        assertEquals("Hoodie XL", cache.get(0).getName());
        assertEquals(59.90, cache.get(0).getPrice(), 0.0001);
    }

    @Test
    @DisplayName("deleteProduct removes it from the cache even though 204 carries no body")
    void deleteProductRemovesFromTheCache() throws Exception {
        await(cb -> controller.addProduct(new Product("Hoodie", 49.90, 5), cb));
        await(cb -> controller.addProduct(new Product("Scarf", 19.90, 3), cb));

        Product doomed = controller.getProducts().get(0);
        List<Product> cache = await(cb -> controller.deleteProduct(doomed, cb));

        assertEquals(1, cache.size());
        assertFalse(cache.stream().anyMatch(p -> p.getId().equals(doomed.getId())));
        assertEquals("Scarf", cache.get(0).getName());
    }

    @Test
    @DisplayName("the client really is non-blocking: the callback runs on a reactor thread, not the caller")
    void theCallbackRunsOnAnotherThread() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        String callerThread = Thread.currentThread().getName();
        String[] callbackThread = new String[1];

        controller.addProduct(new Product("Hoodie", 49.90, 5), products -> {
            callbackThread[0] = Thread.currentThread().getName();
            latch.countDown();
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));

        // With .block() the response would be assembled ON the calling thread and both
        // names would be identical. A different thread is the observable signature of
        // .subscribe(), and "all HTTP requests must be asynchronous" is graded.
        assertNotEquals(callerThread, callbackThread[0],
                "the callback ran on the calling thread, which means the client blocked");
        assertEquals(1, controller.getProducts().size());
    }
}
