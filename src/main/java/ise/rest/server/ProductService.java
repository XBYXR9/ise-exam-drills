package ise.rest.server;

import ise.rest.model.Product;
import ise.rest.model.Review;
import ise.rest.model.SortField;
import ise.rest.model.SortingOrder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * BUSINESS LAYER. Everything that is a RULE rather than an HTTP concern lives here:
 * what "available" means, how a purchase changes the stock, how the list is sorted.
 *
 * The Resource above translates HTTP to method calls; the Repository below stores
 * things. Neither of them decides anything.
 */
@Service
public class ProductService {

    private final ProductRepository repository;

    /** Constructor injection: Spring supplies the repository, and tests can too. */
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product create(Product product) {
        return repository.save(product);
    }

    public Optional<Product> findById(Long id) {
        return repository.findById(id);
    }

    public boolean exists(Long id) {
        return repository.existsById(id);
    }

    public List<Product> findAll(boolean onlyAvailable) {
        return onlyAvailable ? repository.findByQuantityGreaterThan(0) : repository.findAll();
    }

    public List<Product> findAll(boolean onlyAvailable, SortField sortField, SortingOrder order) {
        List<Product> products = findAll(onlyAvailable);
        Comparator<Product> comparator = comparatorFor(sortField);
        if (order == SortingOrder.DESCENDING) {
            comparator = comparator.reversed();
        }
        products.sort(comparator);
        return products;
    }

    private Comparator<Product> comparatorFor(SortField sortField) {
        switch (sortField) {
            case NAME:
                return Comparator.comparing(Product::getName, Comparator.nullsLast(Comparator.naturalOrder()));
            case PRICE:
                return Comparator.comparingDouble(Product::getPrice);
            case QUANTITY:
                return Comparator.comparingInt(Product::getQuantity);
            case ID:
            default:
                return Comparator.comparing(Product::getId);
        }
    }

    public Product update(Product product) {
        return repository.save(product);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    /** The business rule for a purchase: one unit leaves the stock. */
    public Product buyOne(Product product) {
        product.setQuantity(product.getQuantity() - 1);
        return repository.save(product);
    }

    public Review addReview(Long productId, Review review) {
        review.setProductId(productId);
        return repository.saveReview(review);
    }

    public List<Review> findReviews(Long productId) {
        return repository.findReviewsByProductId(productId);
    }
}
