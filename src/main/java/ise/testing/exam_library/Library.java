package ise.testing.exam_library;

import java.util.ArrayList;
import java.util.List;

/**
 * Exam SUT.
 *
 * Note the constructor: the task states "The Library should have no books and have
 * ONE member upon creation", so a house account is registered up front. That single
 * pre-registered member is what testInitialLibraryIsEmptyAndHasOneMember has to
 * assert on -- and failing to assert the member COUNT is what cost points:
 *   "Your test for a new library passed, but it did not correctly check for the
 *    number of members."
 */
public class Library {

    private final List<Book> books = new ArrayList<>();
    private final List<Member> members = new ArrayList<>();

    public Library() {
        members.add(new Member("Library Account", "LIB-0"));
    }

    public void addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book must not be null");
        }
        books.add(book);
    }

    /** A borrowed book stays on the shelf list: it cannot be removed while lent out. */
    public void removeBook(Book book) {
        if (book != null && book.isBorrowed()) {
            throw new IllegalStateException("A borrowed book cannot be removed");
        }
        books.remove(book);
    }

    public void registerMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member must not be null");
        }
        members.add(member);
    }

    public void removeMember(Member member) {
        if (member != null && !member.getBorrowedBooks().isEmpty()) {
            throw new IllegalStateException("A member with borrowed books cannot be removed");
        }
        members.remove(member);
    }

    public void borrowBook(Book book, Member member) {
        if (book.isBorrowed()) {
            throw new IllegalStateException("Book is already borrowed");
        }
        book.setBorrowed(true);
        member.getBorrowedBooks().add(book);
    }

    public void returnBook(Book book, Member member) {
        book.setBorrowed(false);
        member.getBorrowedBooks().remove(book);
    }

    public int getBookCount() {
        return books.size();
    }

    public int getMemberCount() {
        return members.size();
    }
}
