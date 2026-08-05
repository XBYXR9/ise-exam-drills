package ise.mocking.s09_matchers;

/** SUT for the matcher catalogue. */
public class TransferService {

    private final TransferGateway gateway;

    public TransferService(TransferGateway gateway) {
        this.gateway = gateway;
    }

    public boolean send(String iban, double amount) {
        if (amount <= 0.0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        return gateway.transfer(iban, amount);
    }

    /** Looks the customer up first; an unknown name must not reach archive(). */
    public boolean archiveByName(String name) {
        Customer customer = gateway.lookup(name);
        return customer != null && gateway.archive(customer);
    }
}
