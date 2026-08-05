package ise.testing.s11_validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * One test per invalid equivalence class, never one test with five assertThrows in
 * it: when a single guard breaks you want the report to name that guard.
 */
public class CatalogueItemValidationTest {

    /** Overridable so the mutation drill can point these guards at a broken class. */
    protected CatalogueItem newItem(String name, double price, int stock) {
        return new CatalogueItem(name, price, stock);
    }

    @Test
    @DisplayName("a null name is rejected")
    void nullNameRejected() {
        assertThrows(IllegalArgumentException.class, () -> newItem(null, 1.0, 1));
    }

    @Test
    @DisplayName("an empty name is rejected")
    void emptyNameRejected() {
        assertThrows(IllegalArgumentException.class, () -> newItem("", 1.0, 1));
    }

    @Test
    @DisplayName("a blank name is rejected as well, which isBlank catches but isEmpty does not")
    void blankNameRejected() {
        assertThrows(IllegalArgumentException.class, () -> newItem("   ", 1.0, 1));
    }

    @Test
    @DisplayName("a negative price is rejected")
    void negativePriceRejected() {
        assertThrows(IllegalArgumentException.class, () -> newItem("Pen", -0.01, 1));
    }

    @Test
    @DisplayName("a negative stock is rejected")
    void negativeStockRejected() {
        assertThrows(IllegalArgumentException.class, () -> newItem("Pen", 1.0, -1));
    }

    @Test
    @DisplayName("zero is VALID for both price and stock, which is the boundary the guards must not eat")
    void zeroIsAccepted() {
        // If the guard said price <= 0 instead of price < 0, this test is the only one
        // that goes red. Every invalid-input test would still pass.
        CatalogueItem free = assertDoesNotThrow(() -> newItem("Sample", 0.0, 0));
        assertEquals(0.0, free.getPrice(), 0.0001);
        assertEquals(0, free.getStock());
    }
}
