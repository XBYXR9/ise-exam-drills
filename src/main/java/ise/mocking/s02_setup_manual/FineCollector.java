package ise.mocking.s02_setup_manual;

/**
 * SUT injected through the CONSTRUCTOR -- the seam that always works, needs no
 * extension, and makes the dependency impossible to forget.
 */
public class FineCollector {

    private final PaymentTerminal terminal;

    public FineCollector(PaymentTerminal terminal) {
        this.terminal = terminal;
    }

    /** A fine of zero or less is not a fine: the terminal must not be touched at all. */
    public boolean settle(String memberId, double amount) {
        if (amount <= 0.0) {
            return false;
        }
        return terminal.charge(memberId, amount);
    }
}
