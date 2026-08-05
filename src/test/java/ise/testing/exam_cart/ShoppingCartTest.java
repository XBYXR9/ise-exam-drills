package ise.testing.exam_cart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * FINAL EXAM, EXERCISE 2 ("The Testers Strike Back") -- all five tests.
 *
 * The two that failed, and why:
 *
 *   testInitialCartIsEmpty()
 *     "expected: <[]> but was: <0>"
 *     -> an empty LIST was compared against getProductCount(), which is an INT.
 *        Compare like with like: a count against a number.
 *
 *   testAddNullProductThrowsException()
 *     "java.lang.IllegalArgumentException: Product cannot be null"
 *     -> addProduct(null) was called OUTSIDE the assertThrows lambda, so the
 *        exception escaped and the test was reported as an error rather than a pass.
 */
public class ShoppingCartTest {

    private ShoppingCart cart;
    private Product book;
    private Product pen;

    protected ShoppingCart newCart() {
        return new ShoppingCart();
    }

    @BeforeEach
    void setUp() {
        cart = newCart();
        book = new Product("Book", 20.0);
        pen = new Product("Pen", 2.5);
    }

    @Test
    @DisplayName("1. a new cart holds zero products and totals 0.00")
    void testInitialCartIsEmpty() {
        // int against int, double against double with a delta. No list in sight.
        assertEquals(0, cart.getProductCount());
        assertEquals(0.0, cart.calculateTotalPrice(), 0.0001);
    }

    @Test
    @DisplayName("2. adding products updates the count and sums their prices")
    void testAddProductsAndCalculatePrice() {
        cart.addProduct(book);
        cart.addProduct(pen);

        assertEquals(2, cart.getProductCount());
        // 20.00 + 2.50 = 22.50. Two DIFFERENT prices, so a total that returns only the
        // first or only the last price gives a different number and is caught.
        assertEquals(22.50, cart.calculateTotalPrice(), 0.0001);
    }

    @Test
    @DisplayName("3. removing a product updates the count and the total")
    void testRemoveProduct() {
        cart.addProduct(book);
        cart.addProduct(pen);

        cart.removeProduct(book);

        assertEquals(1, cart.getProductCount());
        // The remaining total says WHICH product was removed. A removeProduct that
        // deletes the wrong element leaves count 1 and total 20.0, not 2.5.
        assertEquals(2.50, cart.calculateTotalPrice(), 0.0001);
    }

    @Test
    @DisplayName("4. addProduct(null) throws IllegalArgumentException and leaves the cart empty")
    void testAddNullProductThrowsException() {
        // The call belongs INSIDE the lambda. This is the line that failed last time.
        assertThrows(IllegalArgumentException.class, () -> cart.addProduct(null));

        assertEquals(0, cart.getProductCount());
    }

    @Test
    @DisplayName("5. a 10% discount on a 22.50 cart gives 20.25")
    void testApplyDiscount() {
        cart.addProduct(book);
        cart.addProduct(pen);

        // 22.50 x 0.90 = 20.25. Note that neither 0% nor 100% is used here: those two
        // rates make several wrong formulas produce the right answer.
        assertEquals(20.25, cart.applyDiscount(0.10), 0.0001);

        // applyDiscount must not consume the cart -- the total is still what it was.
        assertEquals(22.50, cart.calculateTotalPrice(), 0.0001);
    }

    @Test
    @DisplayName("edge case worth adding: a discount on an empty cart is 0.00, not a crash")
    void testApplyDiscountOnEmptyCart() {
        assertEquals(0.0, cart.applyDiscount(0.10), 0.0001);
    }
}
