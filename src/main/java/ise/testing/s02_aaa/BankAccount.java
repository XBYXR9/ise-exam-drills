package ise.testing.s02_aaa;

/** Minimal SUT for the Arrange / Act / Assert skeleton. */
public class BankAccount {

    private double balance;

    public BankAccount(double openingBalance) {
        this.balance = openingBalance;
    }

    public void deposit(double amount) {
        if (amount <= 0.0) {
            throw new IllegalArgumentException("Deposit must be positive");
        }
        balance += amount;
    }

    public double getBalance() {
        return balance;
    }
}
