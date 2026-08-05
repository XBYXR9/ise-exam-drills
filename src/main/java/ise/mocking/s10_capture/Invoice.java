package ise.mocking.s10_capture;

/** The object the SUT builds and hands to the repository. Capture inspects it. */
public class Invoice {

    private final String customer;
    private final double total;

    public Invoice(String customer, double total) {
        this.customer = customer;
        this.total = total;
    }

    public String getCustomer() {
        return customer;
    }

    public double getTotal() {
        return total;
    }
}
