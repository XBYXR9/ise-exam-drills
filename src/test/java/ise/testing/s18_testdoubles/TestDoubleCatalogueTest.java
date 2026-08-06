package ise.testing.s18_testdoubles;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ALL FIVE TEST DOUBLES, on one SUT, so the differences are concrete.
 *
 * This is quiz question 15 of the June 2026 mock exam ("match each test double with
 * its key characteristic"), and it is the decision you make silently every time you
 * write a mocking test.
 *
 *   DUMMY  never actually called, only satisfies the API / constructor signature
 *   STUB   provides fixed or configurable return values, does NOT verify usage
 *   SPY    records calls and data so the test can verify them afterwards
 *   MOCK   expectations set in ADVANCE, verified after the exercise stage
 *   FAKE   a real but simplified implementation (in-memory database)
 *
 * The one that matters for marks: STUB feeds values IN, SPY and MOCK check what came
 * OUT. A stub can never prove an interaction happened.
 */
class TestDoubleCatalogueTest {

    // ---- the doubles, written out once ------------------------------

    /** DUMMY: exists only to fill a constructor slot. Being called is a bug. */
    static class DummyPaymentGateway implements PaymentGateway {
        @Override
        public boolean charge(String subscriberId, double amount) {
            throw new AssertionError("the dummy gateway must never be called");
        }
    }

    /** STUB: a canned answer, nothing recorded, nothing verified. */
    static class StubPaymentGateway implements PaymentGateway {
        private final boolean answer;

        StubPaymentGateway(boolean answer) {
            this.answer = answer;
        }

        @Override
        public boolean charge(String subscriberId, double amount) {
            return answer;
        }
    }

    /** SPY: forwards nothing, but remembers everything it was told. */
    static class SpyAuditLog implements AuditLog {
        final List<String> messages = new ArrayList<>();

        @Override
        public void record(String message) {
            messages.add(message);
        }
    }

    /** FAKE: a working implementation, just not a production-grade one. */
    static class FakeSubscriberRepository implements SubscriberRepository {
        private final Map<String, Subscriber> store = new HashMap<>();
        int saveCount;

        FakeSubscriberRepository(Subscriber... subscribers) {
            for (Subscriber subscriber : subscribers) {
                store.put(subscriber.getId(), subscriber);
            }
        }

        @Override
        public Subscriber findById(String subscriberId) {
            return store.get(subscriberId);
        }

        @Override
        public void save(Subscriber subscriber) {
            store.put(subscriber.getId(), subscriber);
            saveCount++;
        }
    }

    // ---- one test per double ----------------------------------------

    @Nested
    @DisplayName("DUMMY -- never called, only fills a parameter slot")
    class Dummy {

        @Test
        @DisplayName("cancel() needs a gateway to construct the service but never uses it")
        void cancelNeverTouchesTheGateway() {
            Subscriber subscriber = new Subscriber("S-1");
            subscriber.setActive(true);
            FakeSubscriberRepository repository = new FakeSubscriberRepository(subscriber);

            SubscriptionService service = new SubscriptionService(
                    new DummyPaymentGateway(), new SpyAuditLog(), repository);

            // If cancel() ever charged the card, the dummy would throw and the test
            // would go red. That is the whole contribution of a dummy: it turns
            // "should not be used" into a runtime guarantee.
            assertTrue(service.cancel("S-1"));
            assertFalse(subscriber.isActive());
        }
    }

    @Nested
    @DisplayName("STUB -- feeds a fixed value IN, verifies nothing")
    class Stub {

        @Test
        @DisplayName("a stub that approves the payment drives the SUT down the success branch")
        void approvingStub() {
            Subscriber subscriber = new Subscriber("S-1");
            FakeSubscriberRepository repository = new FakeSubscriberRepository(subscriber);

            SubscriptionService service = new SubscriptionService(
                    new StubPaymentGateway(true), new SpyAuditLog(), repository);

            assertTrue(service.renew("S-1", 9.99));
            assertTrue(subscriber.isActive());
        }

        @Test
        @DisplayName("a stub that declines drives the SUT down the failure branch")
        void decliningStub() {
            Subscriber subscriber = new Subscriber("S-1");
            FakeSubscriberRepository repository = new FakeSubscriberRepository(subscriber);

            SubscriptionService service = new SubscriptionService(
                    new StubPaymentGateway(false), new SpyAuditLog(), repository);

            assertFalse(service.renew("S-1", 9.99));
            // The stub cannot tell you that charge() was called at all -- it records
            // nothing. Only the SUT state betrays which branch ran.
            assertFalse(subscriber.isActive());
        }
    }

    @Nested
    @DisplayName("SPY -- records what came OUT, asserted afterwards")
    class Spy {

        @Test
        @DisplayName("the spy captures the exact audit message the SUT produced")
        void spyCapturesTheMessage() {
            Subscriber subscriber = new Subscriber("S-1");
            SpyAuditLog audit = new SpyAuditLog();

            SubscriptionService service = new SubscriptionService(
                    new StubPaymentGateway(true), audit, new FakeSubscriberRepository(subscriber));

            service.renew("S-1", 9.99);

            // Count AND content. A spy that is only checked for "not empty" would pass
            // against a SUT that logged the failure message on the success path.
            assertEquals(1, audit.messages.size());
            assertEquals("renewed S-1", audit.messages.get(0));
        }

        @Test
        @DisplayName("a declined payment produces the failure message instead")
        void spyCapturesTheFailureMessage() {
            Subscriber subscriber = new Subscriber("S-1");
            SpyAuditLog audit = new SpyAuditLog();

            SubscriptionService service = new SubscriptionService(
                    new StubPaymentGateway(false), audit, new FakeSubscriberRepository(subscriber));

            service.renew("S-1", 9.99);

            assertEquals(List.of("payment failed for S-1"), audit.messages);
        }
    }

    @Nested
    @DisplayName("MOCK -- expectations in advance, verified after")
    class Mock {

        @Test
        @DisplayName("EasyMock states what must happen BEFORE the SUT runs, then checks it")
        void mockVerifiesTheInteraction() {
            Subscriber subscriber = new Subscriber("S-1");
            PaymentGateway gateway = createMock(PaymentGateway.class);
            AuditLog audit = createMock(AuditLog.class);

            // Set up front -- this is the distinguishing feature versus a spy.
            expect(gateway.charge("S-1", 9.99)).andReturn(true);
            audit.record("renewed S-1");
            expectLastCall();
            replay(gateway, audit);

            SubscriptionService service = new SubscriptionService(
                    gateway, audit, new FakeSubscriberRepository(subscriber));

            assertTrue(service.renew("S-1", 9.99));

            // A mock is a spy that also fails on anything you did NOT declare, and
            // fails at verify() on anything you declared that never happened.
            verify(gateway, audit);
            assertTrue(subscriber.isActive());
        }
    }

    @Nested
    @DisplayName("FAKE -- a real, working, simplified implementation")
    class Fake {

        @Test
        @DisplayName("the in-memory repository actually stores and returns objects")
        void fakeBehavesLikeTheRealThing() {
            Subscriber subscriber = new Subscriber("S-1");
            FakeSubscriberRepository repository = new FakeSubscriberRepository(subscriber);

            SubscriptionService service = new SubscriptionService(
                    new StubPaymentGateway(true), new SpyAuditLog(), repository);

            assertTrue(service.renew("S-1", 9.99));
            // Unlike a stub, a fake has real behaviour: what you save, you can read back.
            assertTrue(repository.findById("S-1").isActive());
            assertEquals(1, repository.saveCount);

            // And it answers honestly for an id it does not know.
            assertFalse(service.renew("S-999", 9.99));
        }
    }
}
