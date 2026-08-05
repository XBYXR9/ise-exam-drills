package ise.testing.s02_aaa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The canonical Arrange / Act / Assert skeleton. Three blank-line-separated blocks,
 * one action per test. If your Act block has two calls in it, you are testing two
 * things and a failure will not tell you which.
 */
public class BankAccountAaaTest {

    private BankAccount account;

    /** Overridable so the mutation drill can point the same assertions at a broken SUT. */
    protected BankAccount newAccount(double openingBalance) {
        return new BankAccount(openingBalance);
    }

    @BeforeEach
    void setUp() {
        account = newAccount(100.0);   // ARRANGE, shared by every test
    }

    @Test
    @DisplayName("depositing 50 on a balance of 100 leaves 150")
    void depositIncreasesTheBalance() {
        // ARRANGE
        double amount = 50.0;

        // ACT
        account.deposit(amount);

        // ASSERT -- the exact number, not just "more than before"
        assertEquals(150.0, account.getBalance(), 0.0001);
    }

    @Test
    @DisplayName("a negative deposit is rejected and the balance is left untouched")
    void negativeDepositIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> account.deposit(-10.0));

        // The second assertion is the one with teeth: an implementation that throws
        // AFTER adding the amount would pass the assertThrows on its own.
        assertEquals(100.0, account.getBalance(), 0.0001);
    }
}
