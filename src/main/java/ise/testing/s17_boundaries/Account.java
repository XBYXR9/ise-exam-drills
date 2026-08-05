package ise.testing.s17_boundaries;

/**
 * The playbook section 18 spec, implemented:
 *   withdraw(amount) returns the new balance, and the account may be overdrawn
 *   down to -2000. Anything beyond that, and any amount <= 0, is rejected.
 *
 * Every numeric limit here (0 and -2000) gets an L-1 / L / L+1 test.
 */
public class Account {

    public static final double OVERDRAFT_LIMIT = -2000.0;

    private double balance;

    public Account(double openingBalance) {
        this.balance = openingBalance;
    }

    public double withdraw(double amount) {
        if (amount <= 0.0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        double newBalance = balance - amount;
        if (newBalance < OVERDRAFT_LIMIT) {
            throw new IllegalArgumentException("Overdraft limit exceeded");
        }
        balance = newBalance;
        return balance;
    }

    public double getBalance() {
        return balance;
    }
}
