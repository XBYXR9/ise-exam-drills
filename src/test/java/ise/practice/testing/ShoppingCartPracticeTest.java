package ise.practice.testing;

import ise.testing.exam_cart.Product;
import ise.testing.exam_cart.ShoppingCart;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * PRACTICE DRILL -- Final exam, exercise 2 ("The Testers Strike Back", 15 points).
 *
 * Delete @Disabled, fill in the TODOs, then diff against
 *     ise.practice.testing.solutions.ShoppingCartSolutionTest
 *
 * =====================================================================
 * PROBLEM STATEMENT (exam wording)
 *
 *   ShoppingCart  -items: List<Product>
 *                 +addProduct(Product): void      +removeProduct(Product): void
 *                 +getProductCount(): int         +calculateTotalPrice(): double
 *                 +applyDiscount(double): double
 *   Product       -name: String   -price: double
 *
 * Please name the test methods EXACTLY as specified. Otherwise the automated
 * correction of your solution will fail and you might not get full points.
 * =====================================================================
 */
@Disabled("PRACTICE DRILL: delete this line, then fill in the TODOs below")
class ShoppingCartPracticeTest {

    // TODO: a @BeforeEach that builds a fresh ShoppingCart and two Products with
    //   DIFFERENT prices. Equal prices hide a total that returns only one of them.

    @Test
    void testInitialCartIsEmpty() {
        // TODO Task 1 -- "It should check that a newly created ShoppingCart is empty
        //   (product count is 0) and that its total price is 0.0."
        //
        //   Last attempt failed with: expected: <[]> but was: <0>
        //   Read that message: what type is on each side, and what should be?
    }

    @Test
    void testAddProductsAndCalculatePrice() {
        // TODO Task 2 -- "It should add multiple products to the cart and verify that
        //   getProductCount() returns the correct number of items and
        //   calculateTotalPrice() returns the correct sum of their prices."
    }

    @Test
    void testRemoveProduct() {
        // TODO Task 3 -- "It should add multiple products, then remove one. Verify that
        //   the product count and total price are updated correctly after the removal."
        //   The remaining TOTAL is what proves the right product was removed.
    }

    @Test
    void testAddNullProductThrowsException() {
        // TODO Task 4 -- "It should verify that calling addProduct(null) throws an
        //   IllegalArgumentException."
        //
        //   Last attempt failed with the exception escaping the test:
        //     java.lang.IllegalArgumentException: Product cannot be null
        //   Where exactly does the addProduct(null) call have to sit?
    }

    @Test
    void testApplyDiscount() {
        // TODO Task 5 -- "It should add some products to the cart and then check if
        //   applyDiscount() returns the correct price for a given discount rate
        //   (e.g., 10% or 0.10)."
        //   Avoid rates of 0.0 and 1.0 here: several wrong formulas pass on those.
    }
}
