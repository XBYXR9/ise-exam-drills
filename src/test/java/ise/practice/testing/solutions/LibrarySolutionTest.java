package ise.practice.testing.solutions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import ise.testing.exam_library.Book;
import ise.testing.exam_library.Library;
import ise.testing.exam_library.Member;

/*
 * SOLUTION to the matching *PracticeTest in the parent package.
 *
 * Generated from the canonical version in the scenario package, which is the one
 * covered by the mutation drill -- so the two can never drift apart. Attempt the
 * practice class cold, then diff against this file.
 */
/**
 * RETAKE EXAM, EXERCISE 4 -- all six tests, with the exact method names from the
 * task description. Renaming any of them scores zero.
 *
 * What cost points last time:
 *   "Your test for registering members passed, but the registerMember() method was
 *    broken and did not register any members."
 *        -> testAddBooksAndRegisterMembers now asserts getMemberCount() explicitly.
 *   "Your test for a new library passed, but it did not correctly check for the
 *    number of members."
 *        -> testInitialLibraryIsEmptyAndHasOneMember now asserts the number 1, not
 *           merely that the list is non-null.
 */
class LibrarySolutionTest {

    private Library library;
    private Book dune;
    private Book solaris;
    private Member ann;
    private Member bob;

    protected Library newLibrary() {
        return new Library();
    }

    @BeforeEach
    void setUp() {
        library = newLibrary();
        dune = new Book("Dune", "Herbert");
        solaris = new Book("Solaris", "Lem");
        ann = new Member("Ann", "M-1");
        bob = new Member("Bob", "M-2");
    }

    @Test
    @DisplayName("1. a new library holds zero books and exactly one pre-registered member")
    void testInitialLibraryIsEmptyAndHasOneMember() {
        // Both numbers are asserted as numbers. "the list is not null" and "the list
        // is empty" are not enough: the task says zero books AND one member, so both
        // counts have to appear literally.
        assertEquals(0, library.getBookCount());
        assertEquals(1, library.getMemberCount());
    }

    @Test
    @DisplayName("2. adding books and registering members updates both counts by the right amount")
    void testAddBooksAndRegisterMembers() {
        library.addBook(dune);
        library.addBook(solaris);
        library.registerMember(ann);
        library.registerMember(bob);

        // 2 books, and 1 pre-existing + 2 new = 3 members. Exact numbers, so an
        // implementation whose registerMember body is empty goes red here instead of
        // sliding through on "the call did not throw".
        assertEquals(2, library.getBookCount());
        assertEquals(3, library.getMemberCount());
    }

    @Test
    @DisplayName("3. borrowing marks the book as lent and puts it on the member, returning undoes both")
    void testBorrowAndReturnBook() {
        library.addBook(dune);
        library.registerMember(ann);

        library.borrowBook(dune, ann);

        assertTrue(dune.isBorrowed());
        assertEquals(1, ann.getBorrowedBooks().size());
        assertTrue(ann.getBorrowedBooks().contains(dune));

        library.returnBook(dune, ann);

        // The inverse operation is checked just as hard as the forward one: an
        // implementation of returnBook that only flips the flag and forgets the list
        // fails on the size assertion.
        assertFalse(dune.isBorrowed());
        assertEquals(0, ann.getBorrowedBooks().size());
        assertFalse(ann.getBorrowedBooks().contains(dune));
    }

    @Test
    @DisplayName("4. addBook(null) throws IllegalArgumentException and adds nothing")
    void testAddNullBookThrowsException() {
        // The call MUST sit inside the lambda. Written outside, the exception escapes
        // the test method and the run is reported as an error, which is exactly what
        // happened on the final exam ("IllegalArgumentException: Product cannot be null").
        assertThrows(IllegalArgumentException.class, () -> library.addBook(null));

        assertEquals(0, library.getBookCount());
    }

    @Test
    @DisplayName("5. removing a book and a member brings both counts back down")
    void testRemoveBookAndMember() {
        library.addBook(dune);
        library.registerMember(ann);
        assertEquals(1, library.getBookCount());
        assertEquals(2, library.getMemberCount());

        library.removeBook(dune);
        library.removeMember(ann);

        // Back to the initial state: 0 books, 1 house member. Asserting the before
        // values above is what makes these two numbers meaningful.
        assertEquals(0, library.getBookCount());
        assertEquals(1, library.getMemberCount());
    }

    @Test
    @DisplayName("6. a second member borrowing an already-borrowed book gets IllegalStateException")
    void testBorrowAlreadyBorrowedBookThrowsException() {
        library.addBook(dune);
        library.registerMember(ann);
        library.registerMember(bob);
        library.borrowBook(dune, ann);

        assertThrows(IllegalStateException.class, () -> library.borrowBook(dune, bob));

        // And the failed attempt changed nothing: the book still belongs to Ann only.
        assertEquals(1, ann.getBorrowedBooks().size());
        assertEquals(0, bob.getBorrowedBooks().size());
        assertTrue(dune.isBorrowed());
    }
}
