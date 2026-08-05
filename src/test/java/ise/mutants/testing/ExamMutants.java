package ise.mutants.testing;

import ise.testing.exam_cart.Product;
import ise.testing.exam_cart.ShoppingCart;
import ise.testing.exam_cart.ShoppingCartTest;
import ise.testing.exam_library.Book;
import ise.testing.exam_library.Library;
import ise.testing.exam_library.LibraryTest;
import ise.testing.exam_library.Member;
import org.junit.jupiter.api.Tag;

/**
 * MUTANTS for the two exam testing exercises.
 *
 * These are the ones that matter: each mutation below is a plausible thing a grader
 * would inject, and two of them are the mutations that actually cost points.
 */
final class ExamMutants {

    private ExamMutants() {
    }
}

// ---------------------------------------------------------------------------
// exam_library -- MUTATION: registerMember does nothing.
// The exact defect behind "your test for registering members passed, but the
// registerMember() method was broken and did not register any members".
// ---------------------------------------------------------------------------
class LibraryThatRegistersNobody extends Library {

    @Override
    public void registerMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member must not be null");
        }
        // the add is gone
    }
}

@Tag("mutant")
class LibraryTest_RegistersNobody extends LibraryTest {

    @Override
    protected Library newLibrary() {
        return new LibraryThatRegistersNobody();
    }
    // CAUGHT BY: testAddBooksAndRegisterMembers -- assertEquals(3, getMemberCount()).
}

// ---------------------------------------------------------------------------
// exam_library -- MUTATION: the pre-registered member is missing.
// The defect behind "your test for a new library ... did not correctly check for
// the number of members".
// ---------------------------------------------------------------------------
class LibraryWithNoHouseMember extends Library {

    @Override
    public int getMemberCount() {
        return super.getMemberCount() - 1;   // the house account was never registered
    }
}

@Tag("mutant")
class LibraryTest_NoHouseMember extends LibraryTest {

    @Override
    protected Library newLibrary() {
        return new LibraryWithNoHouseMember();
    }
    // CAUGHT BY: testInitialLibraryIsEmptyAndHasOneMember -- assertEquals(1, ...).
    // A test that only checked getBookCount() would have sailed straight past this.
}

// ---------------------------------------------------------------------------
// exam_library -- MUTATION: the already-borrowed guard is removed
// ---------------------------------------------------------------------------
class LibraryThatLendsTwice extends Library {

    @Override
    public void borrowBook(Book book, Member member) {
        book.setBorrowed(true);            // the IllegalStateException guard is gone
        member.getBorrowedBooks().add(book);
    }
}

@Tag("mutant")
class LibraryTest_LendsTwice extends LibraryTest {

    @Override
    protected Library newLibrary() {
        return new LibraryThatLendsTwice();
    }
    // CAUGHT BY: testBorrowAlreadyBorrowedBookThrowsException.
}

// ---------------------------------------------------------------------------
// exam_library -- MUTATION: removeBook removes nothing
// ---------------------------------------------------------------------------
class LibraryThatNeverRemoves extends Library {

    @Override
    public void removeBook(Book book) {
        // body deleted
    }
}

@Tag("mutant")
class LibraryTest_NeverRemoves extends LibraryTest {

    @Override
    protected Library newLibrary() {
        return new LibraryThatNeverRemoves();
    }
    // CAUGHT BY: testRemoveBookAndMember -- assertEquals(0, getBookCount()) AFTER
    // having asserted it was 1 before the removal.
}

// ---------------------------------------------------------------------------
// exam_cart -- MUTATION: addProduct does nothing
// ---------------------------------------------------------------------------
class CartThatAddsNothing extends ShoppingCart {

    @Override
    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        // the add is gone
    }
}

@Tag("mutant")
class ShoppingCartTest_AddsNothing extends ShoppingCartTest {

    @Override
    protected ShoppingCart newCart() {
        return new CartThatAddsNothing();
    }
    // CAUGHT BY: testAddProductsAndCalculatePrice, testRemoveProduct, testApplyDiscount.
}

// ---------------------------------------------------------------------------
// exam_cart -- MUTATION: the total returns a constant zero
// ---------------------------------------------------------------------------
class CartWithZeroTotal extends ShoppingCart {

    @Override
    public double calculateTotalPrice() {
        return 0.0;
    }
}

@Tag("mutant")
class ShoppingCartTest_ZeroTotal extends ShoppingCartTest {

    @Override
    protected ShoppingCart newCart() {
        return new CartWithZeroTotal();
    }
    // CAUGHT BY: testAddProductsAndCalculatePrice (22.50) and testApplyDiscount (20.25).
    // testInitialCartIsEmpty does NOT catch it -- 0.0 is the right answer there. An
    // empty-cart test alone can never prove a total is computed.
}

// ---------------------------------------------------------------------------
// exam_cart -- MUTATION: the discount rate is ignored
// ---------------------------------------------------------------------------
class CartThatIgnoresTheDiscount extends ShoppingCart {

    @Override
    public double applyDiscount(double discountRate) {
        return calculateTotalPrice();   // the rate is dropped
    }
}

@Tag("mutant")
class ShoppingCartTest_IgnoresDiscount extends ShoppingCartTest {

    @Override
    protected ShoppingCart newCart() {
        return new CartThatIgnoresTheDiscount();
    }
    // CAUGHT BY: testApplyDiscount -- 20.25 vs 22.50. Had the test used a rate of 0.0
    // (or an empty cart) both implementations would agree and the mutant would survive.
}

// ---------------------------------------------------------------------------
// exam_cart -- MUTATION: removeProduct clears the whole cart
// ---------------------------------------------------------------------------
class CartThatClearsOnRemove extends ShoppingCart {

    private final java.util.List<Product> items = new java.util.ArrayList<>();

    @Override
    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        items.add(product);
    }

    @Override
    public void removeProduct(Product product) {
        items.clear();   // mutated: takes every other product with it
    }

    @Override
    public int getProductCount() {
        return items.size();
    }

    @Override
    public double calculateTotalPrice() {
        double total = 0.0;
        for (Product item : items) {
            total += item.getPrice();
        }
        return total;
    }
}

@Tag("mutant")
class ShoppingCartTest_ClearsOnRemove extends ShoppingCartTest {

    @Override
    protected ShoppingCart newCart() {
        return new CartThatClearsOnRemove();
    }
    // CAUGHT BY: testRemoveProduct -- assertEquals(1, count) and the remaining
    // total of 2.50, which names WHICH product survived.
}
