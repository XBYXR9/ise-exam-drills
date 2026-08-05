package ise.mocking.s08_exceptions;

/**
 * SUT offering both failure policies, so the two test shapes sit side by side:
 *   donate()       -- let the caller deal with it   -> assertThrows
 *   donateSafely() -- swallow and report            -> assertFalse + verify
 */
public class DonationService {

    private final CardProcessor processor;
    private int failedAttempts;

    public DonationService(CardProcessor processor) {
        this.processor = processor;
    }

    /** Propagates whatever the processor throws. */
    public boolean donate(String donorId, double amount) {
        return processor.capture(donorId, amount);
    }

    /** Handles the failure and records it, so the test has observable state to assert on. */
    public boolean donateSafely(String donorId, double amount) {
        try {
            return processor.capture(donorId, amount);
        } catch (GatewayTimeoutException timeout) {
            failedAttempts++;
            return false;
        }
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }
}
