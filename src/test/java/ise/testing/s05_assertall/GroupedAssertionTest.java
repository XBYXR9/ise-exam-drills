package ise.testing.s05_assertall;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.MultipleFailuresError;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Without assertAll, the FIRST failing assertion aborts the method and hides every
 * later one. With it, all of them run and every failure is reported at once -- which
 * on a graded exercise means you see all the broken expectations in one run.
 */
public class GroupedAssertionTest {

    protected ShoppingBasket newBasket() {
        return new ShoppingBasket();
    }

    @Test
    @DisplayName("a new basket is empty on every observable property at once")
    void newBasketIsFullyInitialised() {
        ShoppingBasket basket = newBasket();

        assertAll("new basket",
                () -> assertNotNull(basket.getItems()),
                () -> assertTrue(basket.getItems().isEmpty()),
                () -> assertEquals(0, basket.getItems().size()),
                () -> assertTrue(basket.isEmpty()),
                () -> assertEquals(0.0, basket.getTotal(), 0.0001));
    }

    @Test
    @DisplayName("a filled basket reports size and total together")
    void filledBasketReportsBothProperties() {
        ShoppingBasket basket = newBasket();
        basket.add("Book", 20.0);
        basket.add("Pen", 2.5);

        assertAll("filled basket",
                () -> assertEquals(2, basket.getItems().size()),
                () -> assertTrue(basket.getItems().contains("Book")),
                () -> assertEquals(22.5, basket.getTotal(), 0.0001));
    }

    @Test
    @DisplayName("assertAll collects failures into one MultipleFailuresError instead of stopping at the first")
    void allFailuresAreReportedTogether() {
        ShoppingBasket basket = newBasket();

        MultipleFailuresError failures = assertThrows(MultipleFailuresError.class,
                () -> assertAll(
                        () -> assertEquals(1, basket.getItems().size()),
                        () -> assertEquals(99.0, basket.getTotal(), 0.0001),
                        () -> assertTrue(basket.getItems().contains("Book"))));

        // Three deliberately wrong assertions, three reported failures. Chained plain
        // assertions would have told you about the first one only.
        assertEquals(3, failures.getFailures().size());
    }
}
