package ise.mocking.s07_callcounts;

/**
 * SUT: retries a failing SMS up to maxAttempts times.
 * "How many times was the collaborator called" IS the behaviour under test here,
 * so the call count is the assertion -- not a side note.
 */
public class AlertDispatcher {

    private final SmsGateway gateway;
    private final int maxAttempts;

    public AlertDispatcher(SmsGateway gateway, int maxAttempts) {
        this.gateway = gateway;
        this.maxAttempts = maxAttempts;
    }

    public boolean dispatch(String number, String text) {
        if (text == null || text.isBlank()) {
            return false;   // nothing to send: the gateway must stay untouched
        }
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            if (gateway.send(number, text)) {
                return true;
            }
        }
        return false;
    }
}
