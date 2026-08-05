package ise.rest.server;

/**
 * REFERENCE ONLY -- the exam project usually hands you a Spring Data JPA repository
 * instead of an in-memory Map, and the task is to add a DERIVED QUERY to it: a method
 * whose NAME is the query. No body, no annotation, no SQL.
 *
 * Naming rules worth memorising:
 *   findBy<Field>                          -> WHERE field = ?
 *   findBy<Field>GreaterThan               -> WHERE field > ?
 *   findBy<Field>GreaterThanEqual          -> WHERE field >= ?
 *   findBy<Field>LessThan / LessThanEqual  -> WHERE field < / <= ?
 *   findBy<Field>Between                   -> WHERE field BETWEEN ? AND ?
 *   findBy<Field>Containing                -> WHERE field LIKE %?%
 *   findBy<Field>IgnoreCase                -> case-insensitive equality
 *   findBy<A>And<B> / findBy<A>Or<B>       -> combined conditions
 *   findBy<Field>OrderBy<Other>Desc        -> ORDER BY other DESC
 *   countBy... / existsBy... / deleteBy... -> the other verbs
 *
 * The field name in the method must match the ENTITY field exactly, capitalised.
 *
 * Left commented out because this project has no JPA on the classpath (no database,
 * so the app starts instantly). Uncomment only in a project that has
 * spring-boot-starter-data-jpa.
 *
 * import ise.rest.model.Product;
 * import org.springframework.data.jpa.repository.JpaRepository;
 * import org.springframework.stereotype.Repository;
 *
 * import java.util.List;
 *
 * @Repository
 * public interface ProductJpaRepository extends JpaRepository<Product, Long> {
 *
 *     // GET /products?onlyAvailable=true  ->  service calls this with 0
 *     List<Product> findByQuantityGreaterThan(int quantity);
 *
 *     List<Product> findByNameContainingIgnoreCase(String namePart);
 *
 *     List<Product> findByPriceBetween(double min, double max);
 *
 *     long countByQuantityGreaterThan(int quantity);
 * }
 *
 * JpaRepository already provides save, findById, findAll, existsById, deleteById and
 * count -- you never write those yourself.
 */
public final class ProductJpaRepositoryExample {

    private ProductJpaRepositoryExample() {
    }
}
