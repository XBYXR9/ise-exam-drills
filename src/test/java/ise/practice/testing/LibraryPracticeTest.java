package ise.practice.testing;

import ise.testing.exam_library.Book;
import ise.testing.exam_library.Library;
import ise.testing.exam_library.Member;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * PRACTICE DRILL -- Retake exam, exercise 4 (Testing, 15 points).
 *
 * Delete @Disabled, fill in the TODOs, then diff against
 *     ise.practice.testing.solutions.LibrarySolutionTest
 *
 * =====================================================================
 * PROBLEM STATEMENT (exam wording)
 *
 *   Book    -title -author -isBorrowed
 *   Member  -name -id -borrowedBooks: List<Book>
 *   Library -books: List<Book>  -members: List<Member>
 *           +addBook(Book) +removeBook(Book) +registerMember(Member)
 *           +removeMember(Member) +borrowBook(Book, Member) +returnBook(Book, Member)
 *           +getBookCount(): int  +getMemberCount(): int
 *
 * Do not change the names of the test methods. Otherwise your solution will fail
 * and not get points.
 * =====================================================================
 */
@Disabled("PRACTICE DRILL: delete this line, then fill in the TODOs below")
class LibraryPracticeTest {

    // TODO: a @BeforeEach with a fresh Library, two Books and two Members.

    @Test
    void testInitialLibraryIsEmptyAndHasOneMember() {
        // TODO Task 1 -- "The Library should have no books and have one member upon
        //   creation. It should check that a newly created Library has zero books and
        //   has one registered member."
        //
        //   Graded feedback last time: "Your test for a new library passed, but it did
        //   not correctly check for the number of members."
        //   Both counts, as numbers.
    }

    @Test
    void testAddBooksAndRegisterMembers() {
        // TODO Task 2 -- "It should add multiple books and register multiple members.
        //   Verify that getBookCount() and getMemberCount() return the correct numbers."
        //
        //   Graded feedback: "Your test for registering members passed, but the
        //   registerMember() method was broken and did not register any members."
        //   Careful with the member count: the library already had one.
    }

    @Test
    void testBorrowAndReturnBook() {
        // TODO Task 3 -- "Add a book and a member. Have the member borrow the book and
        //   verify that the book is no longer available and that the member's borrowed
        //   book list is updated. Then, have the member return the book and verify that
        //   the book is available again."
    }

    @Test
    void testAddNullBookThrowsException() {
        // TODO Task 4 -- "It should verify that calling addBook(null) throws an
        //   IllegalArgumentException."
    }

    @Test
    void testRemoveBookAndMember() {
        // TODO Task 5 -- "Add a book and a member, then remove them. Verify that the
        //   book count and member count are updated correctly after the removal."
    }

    @Test
    void testBorrowAlreadyBorrowedBookThrowsException() {
        // TODO Task 6 -- "It should add one book and two members. Have the first member
        //   borrow the book. Then, verify that a subsequent attempt by the second member
        //   to borrow the same book throws an IllegalStateException."
    }
}
