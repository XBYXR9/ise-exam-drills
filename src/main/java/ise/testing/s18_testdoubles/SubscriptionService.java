package ise.testing.s18_testdoubles;

/**
 * One SUT with three collaborators, chosen so that all five kinds of test double
 * have a natural place:
 *   repository -> FAKE   (an in-memory implementation is simpler than stubbing it)
 *   gateway    -> STUB   in renew(), DUMMY in cancel() where it is never used
 *   audit      -> SPY    (hand-written) or MOCK (EasyMock)
 */
public class SubscriptionService {

    private final PaymentGateway gateway;
    private final AuditLog audit;
    private final SubscriberRepository repository;

    public SubscriptionService(PaymentGateway gateway, AuditLog audit, SubscriberRepository repository) {
        this.gateway = gateway;
        this.audit = audit;
        this.repository = repository;
    }

    public boolean renew(String subscriberId, double amount) {
        Subscriber subscriber = repository.findById(subscriberId);
        if (subscriber == null) {
            return false;
        }
        if (!gateway.charge(subscriberId, amount)) {
            audit.record("payment failed for " + subscriberId);
            return false;
        }
        subscriber.setActive(true);
        repository.save(subscriber);
        audit.record("renewed " + subscriberId);
        return true;
    }

    /** Cancelling costs nothing, so the payment gateway is never touched here. */
    public boolean cancel(String subscriberId) {
        Subscriber subscriber = repository.findById(subscriberId);
        if (subscriber == null) {
            return false;
        }
        subscriber.setActive(false);
        repository.save(subscriber);
        audit.record("cancelled " + subscriberId);
        return true;
    }
}
