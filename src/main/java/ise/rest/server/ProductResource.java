package ise.rest.server;

import ise.rest.model.Product;
import ise.rest.model.Review;
import ise.rest.model.SortField;
import ise.rest.model.SortingOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * NETWORK LAYER (Resource). Every endpoint shape the exam has used, in one class.
 *
 * CLOSED LAYERED ARCHITECTURE -- the rule that carries marks:
 *   Resource -> Service -> Repository, each layer talking ONLY to the one below.
 *   This class has a ProductService field and no ProductRepository field. Injecting
 *   the repository here to "save a hop" is the mistake the task warns about.
 *
 * The Resource is allowed to do exactly two things: translate HTTP into a service
 * call, and turn a missing or contradictory input into the right status code.
 */
@RestController
public class ProductResource {

    private final ProductService productService;

    public ProductResource(ProductService productService) {
        this.productService = productService;
    }

    /**
     * POST /products -- create.
     * A client that already knows the id is guessing: ids are assigned by the server.
     */
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        if (product.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A new product must not carry an id");
        }
        Product created = productService.create(product);
        return ResponseEntity.ok(created);
    }

    /**
     * GET /products -- list all, with three OPTIONAL parameters.
     *
     * required = false plus defaultValue is what makes "if the parameter is not
     * defined, use false" work. Without defaultValue the parameter arrives as null
     * and the boolean unboxing throws a 500.
     */
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(value = "onlyAvailable", required = false, defaultValue = "false") boolean onlyAvailable,
            @RequestParam(value = "sortField", required = false, defaultValue = "ID") SortField sortField,
            @RequestParam(value = "sortingOrder", required = false, defaultValue = "ASCENDING") SortingOrder sortingOrder) {
        return ResponseEntity.ok(productService.findAll(onlyAvailable, sortField, sortingOrder));
    }

    /** GET /products/{id} -- a single product, 404 when there is none. */
    @GetMapping("/products/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable Long productId) {
        Product product = productService.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No product with id " + productId));
        return ResponseEntity.ok(product);
    }

    /**
     * PUT /products/{id} -- update.
     * Two different failures, two different codes: a body that disagrees with the URL
     * is the CLIENT being inconsistent (400); an id nobody has is missing (404).
     */
    @PutMapping("/products/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId,
                                                 @RequestBody Product product) {
        if (product.getId() == null || !product.getId().equals(productId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The id in the path and the id in the body must match");
        }
        if (!productService.exists(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No product with id " + productId);
        }
        return ResponseEntity.ok(productService.update(product));
    }

    /** DELETE /products/{id} -- 204 No Content, because there is nothing left to return. */
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        if (!productService.exists(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No product with id " + productId);
        }
        productService.delete(productId);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /products/{id}/buy -- an ACTION on a sub-resource.
     *
     * Buying is not a CRUD verb, so it becomes a POST to a named sub-path rather than
     * a PUT of the whole product. The quantity change itself belongs in the service;
     * the resource only decides which HTTP failure applies.
     */
    @PostMapping("/products/{productId}/buy")
    public ResponseEntity<Product> buyProduct(@PathVariable Long productId) {
        Product product = productService.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No product with id " + productId));
        if (product.getQuantity() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Product " + productId + " is out of stock");
        }
        return ResponseEntity.ok(productService.buyOne(product));
    }

    /**
     * GET /products/{id}/reviews -- a UML association rendered as a nested resource.
     * The parent id lives in the PATH, never in a query parameter.
     */
    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<List<Review>> getReviews(@PathVariable Long productId) {
        if (!productService.exists(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No product with id " + productId);
        }
        return ResponseEntity.ok(productService.findReviews(productId));
    }

    /** POST /products/{id}/reviews -- create a child of an existing parent. */
    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<Review> addReview(@PathVariable Long productId,
                                            @RequestBody Review review) {
        if (review.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A new review must not carry an id");
        }
        if (!productService.exists(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No product with id " + productId);
        }
        return ResponseEntity.ok(productService.addReview(productId, review));
    }
}
