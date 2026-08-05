package ise.testing.s14_query;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Search, filter and sort -- all three need a found AND a not-found test. */
public class BookShelf {

    private final List<Book> books = new ArrayList<>();

    public void add(Book book) {
        books.add(book);
    }

    /** Null-returning search, the shape the playbook uses. */
    public Book findByTitle(String title) {
        for (Book book : books) {
            if (book.getTitle().equals(title)) {
                return book;
            }
        }
        return null;
    }

    /** Optional-returning search, the shape modern code uses. */
    public Optional<Book> find(String title) {
        return books.stream().filter(b -> b.getTitle().equals(title)).findFirst();
    }

    public List<Book> getAvailableBooks() {
        List<Book> available = new ArrayList<>();
        for (Book book : books) {
            if (!book.isBorrowed()) {
                available.add(book);
            }
        }
        return available;
    }

    public List<Book> sortedByPrice() {
        List<Book> sorted = new ArrayList<>(books);
        sorted.sort(Comparator.comparingDouble(Book::getPrice));
        return sorted;
    }
}
