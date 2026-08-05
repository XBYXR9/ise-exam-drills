package ise.testing.s17_boundaries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * EQUIVALENCE CLASSES + BOUNDARY VALUES, worked fully.
 *
 * Spec: withdraw(amount) on an account opened with 100.00, overdraft down to -2000.
 *
 * The recipe:
 *   1. valid classes                     -> one test each
 *   2. invalid classes                   -> one test each
 *   3. every numeric limit L             -> L-1, L, L+1
 *   4. special values (0, null, empty, max)
 *
 * The limits here are the overdraft floor (-2000) and the amount guard (0), so both
 * get all three neighbours.
 */
public class AccountBoundaryTest {

    private Account account;

    protected Account newAccount(double openingBalance) {
        return new Account(openingBalance);
    }

    @BeforeEach
    void setUp() {
        account = newAccount(100.0);
    }

    // ---- valid equivalence classes ----------------------------------

    @Test
    @DisplayName("VALID: a withdrawal smaller than the balance leaves a positive balance")
    void normalWithdrawal() {
        assertEquals(50.0, account.withdraw(50.0), 0.0001);
        assertEquals(50.0, account.getBalance(), 0.0001);
    }

    @Test
    @DisplayName("VALID: withdrawing the whole balance leaves exactly zero")
    void withdrawWholeBalance() {
        assertEquals(0.0, account.withdraw(100.0), 0.0001);
    }

    @Test
    @DisplayName("VALID: withdrawing past the balance draws on the overdraft")
    void withdrawIntoOverdraft() {
        assertEquals(-500.0, account.withdraw(600.0), 0.0001);
    }

    // ---- boundary values around the -2000 overdraft floor ------------

    @Test
    @DisplayName("BOUNDARY L-1: one euro short of the floor is still allowed")
    void justInsideTheOverdraftLimit() {
        assertEquals(-1999.0, account.withdraw(2099.0), 0.0001);
    }

    @Test
    @DisplayName("BOUNDARY L: landing exactly on -2000 is allowed")
    void exactlyAtTheOverdraftLimit() {
        // This is the case a > vs >= mix-up breaks, and the only one that does.
        assertEquals(-2000.0, account.withdraw(2100.0), 0.0001);
    }

    @Test
    @DisplayName("BOUNDARY L+1: one euro past the floor is rejected and the balance is untouched")
    void justBeyondTheOverdraftLimit() {
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(2101.0));
        assertEquals(100.0, account.getBalance(), 0.0001);
    }

    // ---- boundary values around the amount guard ---------------------

    @Test
    @DisplayName("BOUNDARY: an amount of exactly zero is invalid")
    void zeroAmountRejected() {
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(0.0));
        assertEquals(100.0, account.getBalance(), 0.0001);
    }

    @Test
    @DisplayName("BOUNDARY L+1: the smallest sensible positive amount is valid")
    void smallestPositiveAmountAccepted() {
        assertEquals(99.99, account.withdraw(0.01), 0.0001);
    }

    // ---- invalid equivalence classes ---------------------------------

    @Test
    @DisplayName("INVALID: a negative amount is rejected")
    void negativeAmountRejected() {
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(-5.0));
        assertEquals(100.0, account.getBalance(), 0.0001);
    }

    @Test
    @DisplayName("a rejected withdrawal must not move the balance at all")
    void rejectedWithdrawalsAreAtomic() {
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(5000.0));
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(-1.0));
        // Two failed attempts, balance still exactly as it started. An implementation
        // that subtracts before validating fails here.
        assertEquals(100.0, account.getBalance(), 0.0001);
    }
}
