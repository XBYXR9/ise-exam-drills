package ise.testing.s08_nested_ordered;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @Nested groups tests by the STATE they exercise, and each inner class gets its
 * own @BeforeEach. The report then reads as sentences: "when the catalogue is
 * empty > removing a title reports nothing was removed".
 */
public class CatalogueStructureTest {

    protected LibraryCatalogue newCatalogue() {
        return new LibraryCatalogue();
    }

    @Nested
    @DisplayName("when the catalogue is empty")
    class WhenEmpty {

        private LibraryCatalogue catalogue;

        @BeforeEach
        void setUp() {
            catalogue = newCatalogue();
        }

        @Test
        @DisplayName("it holds no titles")
        void sizeIsZero() {
            assertEquals(0, catalogue.size());
        }

        @Test
        @DisplayName("removing anything reports that nothing was removed")
        void removeReportsFalse() {
            assertFalse(catalogue.remove("Dune"));
            assertEquals(0, catalogue.size());
        }
    }

    @Nested
    @DisplayName("when the catalogue holds two titles")
    class WhenStocked {

        private LibraryCatalogue catalogue;

        @BeforeEach
        void setUp() {
            catalogue = newCatalogue();
            catalogue.add("Dune");
            catalogue.add("Solaris");
        }

        @Test
        @DisplayName("both titles are found")
        void bothArePresent() {
            assertEquals(2, catalogue.size());
            assertTrue(catalogue.contains("Dune"));
            assertTrue(catalogue.contains("Solaris"));
        }

        @Test
        @DisplayName("removing one leaves exactly the other")
        void removeDeletesOnlyThatTitle() {
            assertTrue(catalogue.remove("Dune"));

            assertEquals(1, catalogue.size());
            assertFalse(catalogue.contains("Dune"));   // negative check
            assertTrue(catalogue.contains("Solaris")); // and the survivor check
        }
    }
}
