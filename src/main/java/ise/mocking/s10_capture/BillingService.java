package ise.mocking.s10_capture;

/**
 * SUT that builds an object internally. The only way to check WHAT it built is to
 * capture the argument it passed to the collaborator -- the mock version of a spy.
 */
public class BillingService {

    private final InvoiceRepository repository;

    public BillingService(InvoiceRepository repository) {
        this.repository = repository;
    }

    public boolean bill(String customer, int quantity, double unitPrice) {
        return repository.save(new Invoice(customer, quantity * unitPrice));
    }

    /** One invoice per item, so CaptureType.ALL has several values to collect. */
    public void billSeparately(String customer, double... unitPrices) {
        for (double unitPrice : unitPrices) {
            repository.save(new Invoice(customer, unitPrice));
        }
    }
}
