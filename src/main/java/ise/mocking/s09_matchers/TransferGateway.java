package ise.mocking.s09_matchers;

public interface TransferGateway {

    boolean transfer(String iban, double amount);

    Customer lookup(String name);

    boolean archive(Customer customer);
}
