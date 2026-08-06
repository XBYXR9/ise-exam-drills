package ise.testing.s18_testdoubles;

public interface PaymentGateway {

    boolean charge(String subscriberId, double amount);
}
