package ise.rest.server;

import ise.rest.model.Product;
import ise.rest.model.Review;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * PERSISTENCE LAYER, in memory so the project runs with zero setup.
 *
 * In a closed layered architecture only the Service may talk to this class. The
 * Resource must never inject it -- that shortcut is the single most common way to
 * lose the architecture marks.
 */
@Repository
public class ProductRepository {

    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final Map<Long, Review> reviews = new ConcurrentHashMap<>();
    private final AtomicLong productIds = new AtomicLong(0);
    private final AtomicLong reviewIds = new AtomicLong(0);

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(productIds.incrementAndGet());
        }
        products.put(product.getId(), product);
        return product;
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    public boolean existsById(Long id) {
        return products.containsKey(id);
    }

    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    /**
     * The hand-written equivalent of the Spring Data derived query
     * findByQuantityGreaterThan(int) -- see ProductJpaRepositoryExample.
     */
    public List<Product> findByQuantityGreaterThan(int threshold) {
        List<Product> matches = new ArrayList<>();
        for (Product product : products.values()) {
            if (product.getQuantity() > threshold) {
                matches.add(product);
            }
        }
        return matches;
    }

    public void deleteById(Long id) {
        products.remove(id);
    }

    public Review saveReview(Review review) {
        if (review.getId() == null) {
            review.setId(reviewIds.incrementAndGet());
        }
        reviews.put(review.getId(), review);
        return review;
    }

    public List<Review> findReviewsByProductId(Long productId) {
        List<Review> matches = new ArrayList<>();
        for (Review review : reviews.values()) {
            if (productId.equals(review.getProductId())) {
                matches.add(review);
            }
        }
        return matches;
    }
}
