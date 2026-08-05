package ise.testing.s13_calculations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Normal case + both extremes + empty case + invalid case, with a delta on every
 * double comparison.
 *
 * Note the chosen numbers. 0.0 as an input is useless for catching a wrong formula
 * because 0 times anything is 0; and 1.0 hides a stray multiplication. Every value
 * here is picked so that a plausible wrong formula gives a DIFFERENT answer.
 */
public class InvoiceCalculatorTest {

    private InvoiceCalculator calculator;

    protected InvoiceCalculator newCalculator() {
        return new InvoiceCalculator();
    }

    @BeforeEach
    void setUp() {
        calculator = newCalculator();
    }

    @Test
    @DisplayName("normal case: 3 items at 12.50 net out at 37.50")
    void normalNet() {
        assertEquals(37.50, calculator.net(12.50, 3), 0.0001);
    }

    @Test
    @DisplayName("normal case: the same order grosses up by 19% VAT to 44.625")
    void normalGross() {
        // A SUT that added the VAT instead of multiplying would give 37.69 -- a
        // different number, which is the whole point of not testing with 0 or 1.
        assertEquals(44.625, calculator.gross(12.50, 3), 0.0001);
    }

    @Test
    @DisplayName("extreme: a quantity of zero costs nothing")
    void zeroQuantity() {
        assertEquals(0.0, calculator.net(12.50, 0), 0.0001);
    }

    @Test
    @DisplayName("extreme: a 100% discount makes the invoice free")
    void fullDiscount() {
        assertEquals(0.0, calculator.discounted(37.50, 1.0), 0.0001);
    }

    @Test
    @DisplayName("extreme: a 0% discount leaves the amount exactly as it was")
    void zeroDiscount() {
        assertEquals(37.50, calculator.discounted(37.50, 0.0), 0.0001);
    }

    @Test
    @DisplayName("normal case: 10% off 37.50 is 33.75")
    void partialDiscount() {
        assertEquals(33.75, calculator.discounted(37.50, 0.10), 0.0001);
    }

    @Test
    @DisplayName("invalid: a negative unit price is rejected")
    void negativeUnitPriceRejected() {
        assertThrows(IllegalArgumentException.class, () -> calculator.net(-1.0, 3));
    }

    @Test
    @DisplayName("invalid: a negative quantity is rejected")
    void negativeQuantityRejected() {
        assertThrows(IllegalArgumentException.class, () -> calculator.net(12.50, -1));
    }

    @Test
    @DisplayName("invalid: a discount rate outside 0..1 is rejected on both sides")
    void discountRateOutOfRangeRejected() {
        assertThrows(IllegalArgumentException.class, () -> calculator.discounted(37.50, -0.01));
        assertThrows(IllegalArgumentException.class, () -> calculator.discounted(37.50, 1.01));
    }
}
