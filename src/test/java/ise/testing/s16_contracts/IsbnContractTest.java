package ise.testing.s16_contracts;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The equals / hashCode / toString contract, including the clauses people forget. */
public class IsbnContractTest {

    protected Isbn newIsbn(String code, String title) {
        return new Isbn(code, title);
    }

    @Test
    @DisplayName("two ISBNs with the same code are equal even when the titles differ")
    void equalByCode() {
        assertEquals(newIsbn("978-1", "Dune"), newIsbn("978-1", "Dune, 2nd ed."));
    }

    @Test
    @DisplayName("different codes are not equal")
    void differentCodesAreNotEqual() {
        assertNotEquals(newIsbn("978-1", "Dune"), newIsbn("978-2", "Dune"));
    }

    @Test
    @DisplayName("equal objects must have equal hash codes, or hash collections break")
    void equalObjectsShareAHashCode() {
        assertEquals(newIsbn("978-1", "Dune").hashCode(), newIsbn("978-1", "x").hashCode());
    }

    @Test
    @DisplayName("the practical consequence: a HashSet treats them as one element")
    void hashSetDeduplicates() {
        Set<Isbn> set = new HashSet<>();
        set.add(newIsbn("978-1", "Dune"));
        set.add(newIsbn("978-1", "Dune, 2nd ed."));

        // This is the test that actually catches an equals() without a matching
        // hashCode(): the equality tests above pass, and this one does not.
        assertEquals(1, set.size());
    }

    @Test
    @DisplayName("equals is reflexive, symmetric and null-safe")
    void equalsContractClauses() {
        Isbn isbn = newIsbn("978-1", "Dune");
        Isbn twin = newIsbn("978-1", "Dune");

        assertEquals(isbn, isbn);            // reflexive
        assertEquals(isbn, twin);            // symmetric, both ways
        assertEquals(twin, isbn);
        assertNotEquals(null, isbn);         // never throws on null
        assertNotEquals("978-1", isbn);      // and never equals a foreign type
    }

    @Test
    @DisplayName("toString mentions the identifying fields")
    void toStringIsInformative() {
        String text = newIsbn("978-1", "Dune").toString();
        assertTrue(text.contains("978-1"));
        assertTrue(text.contains("Dune"));
    }
}
