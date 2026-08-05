package ise.mocking.s04_mocktypes;

/**
 * SUT whose contract includes an ORDER: confirm the address first, only then
 * welcome the reader. Scenario 04 uses this to show what each mock type notices.
 */
public class SubscriptionService {

    private final MailSender mail;

    public SubscriptionService(MailSender mail) {
        this.mail = mail;
    }

    public void subscribe(String email) {
        mail.sendConfirmation(email);
        mail.sendWelcome(email);
    }

    /** Deliberate variant: the same two calls in the WRONG order. */
    public void subscribeInWrongOrder(String email) {
        mail.sendWelcome(email);
        mail.sendConfirmation(email);
    }

    /** Deliberate variant: an extra call nobody asked for. */
    public void subscribeAndSpam(String email) {
        mail.sendConfirmation(email);
        mail.sendWelcome(email);
        mail.sendWelcome(email);
    }
}
