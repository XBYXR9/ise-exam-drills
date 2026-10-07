package de.tum.ise;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/*
 * JUNIT EXTRAS TEMPLATE -- the things TestTemplate does not cover:
 *   exceptions with message, void methods (assert STATE), collections, object equality,
 *   assertAll, grouping with @Nested, lifecycle, skipping with assumptions.
 * Rename Library / Book / addBook / getBookCount to your classes. Delete what you do not need.
 */
public class JUnitExtrasTemplate {

    private Library library;

    @BeforeEach
    void setUp() {                                   // fresh object for EVERY test
        library = new Library();
    }

    // ---------- 1. EXCEPTION: the call goes INSIDE the lambda, never in a try/catch ----------
    @Test
    void testAddNullThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> library.addBook(null));
        assertEquals("Book must not be null", ex.getMessage());      // optional: check the message too
    }

    // ---------- 2. VOID method: nothing is returned, so assert the STATE it changed ----------
    @Test
    void testAddBookIncreasesCount() {
        assertEquals(0, library.getBookCount());                     // assert the BEFORE value too:
        library.addBook(new Book("Dune"));                           //   otherwise "does nothing" also passes
        assertEquals(1, library.getBookCount());                     // int vs int, never list vs int
    }

    // ---------- 3. COLLECTION contents, not just size ----------
    @Test
    void testBooksContent() {
        library.addBook(new Book("A"));
        library.addBook(new Book("B"));
        assertEquals(List.of("A", "B"), library.titles());           // order matters with List
        assertTrue(library.titles().contains("A"));
        assertFalse(library.titles().isEmpty());
    }

    // ---------- 4. OBJECT equality: needs equals()/hashCode() in the class, else compare getters ----------
    @Test
    void testObjectsEqual() {
        assertEquals(new Book("Dune"), new Book("Dune"));            // works because Book overrides equals
        assertNotEquals(new Book("Dune"), new Book("Emma"));
        Book b = new Book("X");
        assertSame(b, b);                                            // same REFERENCE
        assertNotSame(new Book("X"), new Book("X"));
        assertNull(library.find("nobody"));                          // not found -> null
    }

    // ---------- 5. assertAll: report EVERY failing check, not just the first ----------
    @Test
    void testGroupedChecks() {
        library.addBook(new Book("A"));
        assertAll("library state",
                () -> assertEquals(1, library.getBookCount()),
                () -> assertTrue(library.titles().contains("A")),
                () -> assertNotNull(library.titles()));
    }

    // ---------- 6. DOUBLES: always a delta ----------
    @Test
    void testDouble() {
        assertEquals(0.3, 0.1 + 0.2, 0.0001);                        // assertEquals(0.3, 0.1 + 0.2) would FAIL
    }

    // ---------- 7. SKIP (not fail, not pass) when a precondition is not met ----------
    @Test
    void testOnlyIfPrecondition() {
        assumeTrue(library.getBookCount() == 0);                     // false -> test is SKIPPED
        assertEquals(0, library.titles().size());
    }

    // ---------- 8. NEVER branch inside a test: split into two tests, one per branch ----------
    //   BAD:   if (mode == FAST) assertEquals(2, r); else assertEquals(5, r);
    //   GOOD:  testFastMode() { ... assertEquals(2, r); }   testSlowMode() { ... assertEquals(5, r); }

    // ---------- 9. GROUP related tests; each @Nested class gets its own @BeforeEach ----------
    @Nested
    @DisplayName("when the library already holds a book")
    class WithOneBook {
        @BeforeEach
        void addOne() { library.addBook(new Book("Dune")); }         // runs AFTER the outer setUp

        @Test
        void countIsOne() { assertEquals(1, library.getBookCount()); }

        @Test
        void canFindIt() { assertEquals(new Book("Dune"), library.find("Dune")); }
    }

    // ---------- Lifecycle quick reference ----------
    //  @BeforeEach / @AfterEach  : around every test          @BeforeAll / @AfterAll : once, METHOD MUST BE static
    //  @Disabled("reason")       : skip on purpose            @DisplayName("text")   : readable name in the report
    //  Never rename a given test method; never leave fail("not implemented").

    // ---------- Dummy classes so this file compiles. DELETE these. ----------
    static class Book {
        private final String title;
        Book(String title) { this.title = title; }
        String getTitle() { return title; }
        @Override public boolean equals(Object o) { return o instanceof Book b && Objects.equals(title, b.title); }
        @Override public int hashCode() { return Objects.hash(title); }
    }

    static class Library {
        private final List<Book> books = new ArrayList<>();
        void addBook(Book b) {
            if (b == null) throw new IllegalArgumentException("Book must not be null");
            books.add(b);
        }
        int getBookCount() { return books.size(); }
        List<String> titles() { return books.stream().map(Book::getTitle).toList(); }
        Book find(String title) { return books.stream().filter(b -> b.getTitle().equals(title)).findFirst().orElse(null); }
    }
}
