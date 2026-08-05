package ise.mocking.s02_setup_manual;

/** Collaborator: the card terminal at the library desk. */
public interface PaymentTerminal {

    boolean charge(String memberId, double amount);
}
