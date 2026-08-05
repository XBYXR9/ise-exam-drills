package ise.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import ise.rest.model.Product;
import ise.rest.model.Review;
import ise.rest.server.ServerApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Endpoint tests for every shape in ProductResource: the status code AND the JSON.
 *
 * The repository is an in-memory singleton, so @DirtiesContext rebuilds the whole
 * application context before each test. That is what keeps the tests independent --
 * exactly the same reason you build the SUT in @BeforeEach in a plain unit test.
 */
@SpringBootTest(classes = ServerApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductResourceTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper json = new ObjectMapper();

    /** Helper: creates a product through the API and returns its server-assigned id. */
    private long createProduct(String name, double price, int quantity) throws Exception {
        MvcResult result = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new Product(name, price, quantity))))
                .andExpect(status().isOk())
                .andReturn();
        return json.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    @DisplayName("POST /products creates the product and gives it a server-assigned id")
    void createProductAssignsAnId() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new Product("Hoodie", 49.90, 5))))
                .andExpect(status().isOk())
                // The id must come back non-null: a resource that forgets to return the
                // saved object gives the client nothing to work with afterwards.
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Hoodie"))
                .andExpect(jsonPath("$.price").value(49.90))
                .andExpect(jsonPath("$.quantity").value(5));
    }

    @Test
    @DisplayName("POST /products with an id already set is rejected with 400 Bad Request")
    void createProductWithIdIsRejected() throws Exception {
        Product withId = new Product("Hoodie", 49.90, 5);
        withId.setId(99L);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(withId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /products returns every product")
    void getAllProducts() throws Exception {
        createProduct("Hoodie", 49.90, 5);
        createProduct("Scarf", 19.90, 0);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /products?onlyAvailable=true hides the out-of-stock product")
    void getOnlyAvailableProducts() throws Exception {
        createProduct("Hoodie", 49.90, 5);
        createProduct("Scarf", 19.90, 0);

        mockMvc.perform(get("/products").param("onlyAvailable", "true"))
                .andExpect(status().isOk())
                // Size AND identity: a filter that returns the whole list has size 2,
                // and one that returns an empty list has size 0. Only 1 + "Hoodie" is right.
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Hoodie"));
    }

    @Test
    @DisplayName("GET /products without the parameter defaults to onlyAvailable=false")
    void onlyAvailableDefaultsToFalse() throws Exception {
        createProduct("Scarf", 19.90, 0);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /products?sortField=PRICE&sortingOrder=DESCENDING sorts on the server")
    void sortingIsAppliedByTheServer() throws Exception {
        createProduct("Scarf", 19.90, 3);
        createProduct("Hoodie", 49.90, 5);

        mockMvc.perform(get("/products")
                        .param("sortField", "PRICE")
                        .param("sortingOrder", "DESCENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Hoodie"))
                .andExpect(jsonPath("$[1].name").value("Scarf"));
    }

    @Test
    @DisplayName("GET /products/{id} returns the single product, 404 when the id is unknown")
    void getSingleProduct() throws Exception {
        long id = createProduct("Hoodie", 49.90, 5);

        mockMvc.perform(get("/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hoodie"));

        mockMvc.perform(get("/products/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /products/{id} updates the product when path id and body id agree")
    void updateProduct() throws Exception {
        long id = createProduct("Hoodie", 49.90, 5);

        Product updated = new Product("Hoodie XL", 59.90, 3);
        updated.setId(id);

        mockMvc.perform(put("/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hoodie XL"))
                .andExpect(jsonPath("$.price").value(59.90));

        // And the change actually persisted, rather than only being echoed back.
        mockMvc.perform(get("/products/{id}", id))
                .andExpect(jsonPath("$.name").value("Hoodie XL"));
    }

    @Test
    @DisplayName("PUT /products/{id} is 400 when the two ids disagree and 404 when the id is unknown")
    void updateProductFailureModes() throws Exception {
        long id = createProduct("Hoodie", 49.90, 5);

        Product mismatched = new Product("Hoodie", 49.90, 5);
        mismatched.setId(id + 1);
        mockMvc.perform(put("/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(mismatched)))
                .andExpect(status().isBadRequest());

        Product missing = new Product("Ghost", 1.0, 1);
        missing.setId(9999L);
        mockMvc.perform(put("/products/{id}", 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(missing)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /products/{id} answers 204 No Content and the product is really gone")
    void deleteProduct() throws Exception {
        long id = createProduct("Hoodie", 49.90, 5);

        mockMvc.perform(delete("/products/{id}", id))
                .andExpect(status().isNoContent());

        // The follow-up GET is the assertion with teeth: a delete that returns 204
        // without removing anything passes the status check on its own.
        mockMvc.perform(get("/products/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /products/{id}/buy decrements the quantity by exactly one")
    void buyProductDecrementsQuantity() throws Exception {
        long id = createProduct("Hoodie", 49.90, 5);

        mockMvc.perform(post("/products/{id}/buy", id))
                .andExpect(status().isOk())
                // 4, not "less than 5": a buy that empties the stock also reduces it.
                .andExpect(jsonPath("$.quantity").value(4));

        mockMvc.perform(get("/products/{id}", id))
                .andExpect(jsonPath("$.quantity").value(4));
    }

    @Test
    @DisplayName("POST /products/{id}/buy is 400 when out of stock and 404 when the id is unknown")
    void buyProductFailureModes() throws Exception {
        long soldOut = createProduct("Scarf", 19.90, 0);

        mockMvc.perform(post("/products/{id}/buy", soldOut))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/products/{id}/buy", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST and GET /products/{id}/reviews handle the nested resource")
    void nestedReviewResource() throws Exception {
        long id = createProduct("Hoodie", 49.90, 5);

        mockMvc.perform(get("/products/{id}/reviews", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(post("/products/{id}/reviews", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new Review("ann", 5))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                // The parent id comes from the PATH, and the server writes it into the
                // child. A client is never asked to repeat it in the body.
                .andExpect(jsonPath("$.productId").value(id))
                .andExpect(jsonPath("$.author").value("ann"));

        mockMvc.perform(get("/products/{id}/reviews", id))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].stars").value(5));
    }

    @Test
    @DisplayName("the nested resource is 404 when the parent product does not exist")
    void nestedResourceRequiresAnExistingParent() throws Exception {
        mockMvc.perform(get("/products/{id}/reviews", 9999))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/products/{id}/reviews", 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new Review("ann", 5))))
                .andExpect(status().isNotFound());
    }
}
