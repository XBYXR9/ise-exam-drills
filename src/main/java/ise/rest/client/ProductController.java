package ise.rest.client;

import ise.rest.model.Product;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * CLIENT, application layer of the MVC client -- the exam's ProductController.
 *
 * The three requirements from the task, and what each one means in code:
 *
 *   "It must have a WebClient instance and a List<Product> to cache the data locally."
 *        -> the two fields below. The cache is what the View renders; the server is
 *           only consulted to refresh it.
 *
 *   "All HTTP requests must be asynchronous (non-blocking)."
 *        -> every call ends in .subscribe(...), never .block().
 *           .block() parks the calling thread until the response arrives. On the exam
 *           it costs the marks for this requirement, and in a real GUI it freezes the
 *           window. .subscribe() registers a callback and returns immediately; the
 *           reactive thread runs the lambda when the response lands.
 *
 *   "Each method takes a Consumer<List<Product>> which must be called with the
 *    updated local list after a response is received from the server."
 *        -> the callback is invoked INSIDE the subscribe lambda, after the cache has
 *           been updated. Calling it outside subscribe would hand the View the old
 *           cache, because the response has not arrived yet.
 */
public class ProductController {

    private final WebClient webClient;

    /** The local cache the View renders. */
    private final List<Product> products = new ArrayList<>();

    public ProductController(String baseUrl) {
        this.webClient = WebClient.create(baseUrl);
    }

    /** Constructor for tests, which supply a WebClient pointed at a stub server. */
    public ProductController(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * POST /products, then add the server-assigned product to the cache.
     *
     * bodyToMono(Product.class) because a single object comes back;
     * bodyToFlux(...) is for a list. Getting that wrong is a runtime ClassCastException,
     * not a compile error.
     */
    public void addProduct(Product product, Consumer<List<Product>> callback) {
        webClient.post()
                .uri("/products")
                .bodyValue(product)
                .retrieve()
                .bodyToMono(Product.class)
                .subscribe(created -> {                 // NOT .block()
                    products.add(created);              // 1. update the cache
                    callback.accept(products);          // 2. THEN notify the caller
                });
    }

    /** GET /products -- replace the whole cache with what the server has. */
    public void getAllProducts(Consumer<List<Product>> callback) {
        webClient.get()
                .uri("/products")
                .retrieve()
                .bodyToFlux(Product.class)              // a LIST comes back -> Flux
                .collectList()                          // Flux<Product> -> Mono<List<Product>>
                .subscribe(fetched -> {
                    products.clear();                   // clear before filling, or the
                    products.addAll(fetched);           // cache doubles on every refresh
                    callback.accept(products);
                });
    }

    /** PUT /products/{id} -- replace the cached copy in place, keeping the list order. */
    public void updateProduct(Product product, Consumer<List<Product>> callback) {
        webClient.put()
                .uri("/products/{id}", product.getId())  // let WebClient do the encoding
                .bodyValue(product)
                .retrieve()
                .bodyToMono(Product.class)
                .subscribe(updated -> {
                    for (int i = 0; i < products.size(); i++) {
                        if (products.get(i).getId().equals(updated.getId())) {
                            products.set(i, updated);
                            break;
                        }
                    }
                    callback.accept(products);
                });
    }

    /**
     * DELETE /products/{id} -- the server answers 204 No Content, so there is no body
     * to map. bodyToMono(Void.class) completes empty, and an empty Mono never fires
     * the onNext lambda of subscribe(). Use .then() or the completion callback instead,
     * otherwise the cache is never updated and the View never refreshes.
     */
    public void deleteProduct(Product product, Consumer<List<Product>> callback) {
        webClient.delete()
                .uri("/products/{id}", product.getId())
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe(
                        ignored -> { },                 // never called for an empty body
                        error -> { },                   // onError
                        () -> {                         // onComplete -- this one fires
                            products.removeIf(p -> p.getId().equals(product.getId()));
                            callback.accept(products);
                        });
    }

    /** Read-only view of the cache, for the View and for tests. */
    public List<Product> getProducts() {
        return products;
    }
}
