package ise.testing.s13_calculations;

/**
 * Money arithmetic. Note the deliberate choice of test values in the test class:
 * never 0.0 for the input, because 0.0 * anything is 0.0 and a wrong formula would
 * still produce the expected result.
 */
public class InvoiceCalculator {

    public static final double VAT_RATE = 0.19;

    public double net(double unitPrice, int quantity) {
        if (unitPrice < 0.0) {
            throw new IllegalArgumentException("Unit price must not be negative");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must not be negative");
        }
        return unitPrice * quantity;
    }

    public double gross(double unitPrice, int quantity) {
        return net(unitPrice, quantity) * (1.0 + VAT_RATE);
    }

    public double discounted(double amount, double rate) {
        if (rate < 0.0 || rate > 1.0) {
            throw new IllegalArgumentException("Discount rate must be between 0 and 1");
        }
        return amount * (1.0 - rate);
    }
}
