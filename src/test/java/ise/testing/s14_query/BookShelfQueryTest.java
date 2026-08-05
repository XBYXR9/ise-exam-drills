package ise.testing.s14_query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Search, filter and sort. Each one needs BOTH directions: the hit and the miss,
 * the included and the excluded. A filter test that only checks the survivors passes
 * against a filter that returns everything.
 */
public class BookShelfQueryTest {

    private BookShelf shelf;
    private Book dune;
    private Book solaris;

    protected BookShelf newShelf() {
        return new BookShelf();
    }

    @BeforeEach
    void setUp() {
        shelf = newShelf();
        dune = new Book("Dune", "Herbert", 20.0);
        solaris = new Book("Solaris", "Lem", 10.0);
        shelf.add(dune);
        shelf.add(solaris);
    }

    @Test
    @DisplayName("findByTitle returns the very object that was added")
    void findReturnsTheMatch() {
        // assertSame, not assertEquals: Book has no equals(), so assertEquals would
        // silently degrade to reference comparison anyway -- say what you mean.
        assertSame(dune, shelf.findByTitle("Dune"));
    }

    @Test
    @DisplayName("findByTitle returns null for a title that is not on the shelf")
    void findReturnsNullWhenAbsent() {
        assertNull(shelf.findByTitle("Neuromancer"));
    }

    @Test
    @DisplayName("the Optional variant is present on a hit and empty on a miss")
    void optionalVariant() {
        Optional<Book> hit = shelf.find("Solaris");
        assertTrue(hit.isPresent());
        assertSame(solaris, hit.get());

        Optional<Book> miss = shelf.find("Neuromancer");
        assertTrue(miss.isEmpty());
        assertFalse(miss.isPresent());
    }

    @Test
    @DisplayName("getAvailableBooks includes the free book and excludes the lent one")
    void filterKeepsOnlyAvailableBooks() {
        dune.lend();

        List<Book> available = shelf.getAvailableBooks();

        assertEquals(1, available.size());              // exact size
        assertTrue(available.contains(solaris));        // positive
        assertFalse(available.contains(dune));          // NEGATIVE -- the one that
                                                        // catches a filter returning
                                                        // the whole list unchanged
    }

    @Test
    @DisplayName("an empty shelf filters down to an empty list rather than null")
    void filterOnEmptyShelf() {
        BookShelf empty = newShelf();
        assertEquals(0, empty.getAvailableBooks().size());
        assertTrue(empty.getAvailableBooks().isEmpty());
    }

    @Test
    @DisplayName("sortedByPrice puts the cheaper book first and leaves the shelf itself alone")
    void sortIsAscendingAndNonDestructive() {
        List<Book> sorted = shelf.sortedByPrice();

        assertEquals("Solaris", sorted.get(0).getTitle());
        assertEquals("Dune", sorted.get(1).getTitle());
        assertEquals(2, sorted.size());
        // The original order is untouched: sorting returned a copy.
        assertSame(dune, shelf.findByTitle("Dune"));
    }
}
