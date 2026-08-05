package ise.mocking.s04_mocktypes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The SAME expectations written three times, once per mock type, so you can see
 * exactly what each type catches and what it waves through.
 *
 * Every "this one fails" case is wrapped in assertThrows(AssertionError.class, ...)
 * -- EasyMock signals a violated expectation by throwing AssertionError, so asserting
 * that it is thrown proves the mock caught the bug while keeping the suite green.
 *
 *              wrong order   unexpected call   recorded-but-never-called
 *   default    tolerated     FAILS             FAILS at verify()
 *   strict     FAILS         FAILS             FAILS at verify()
 *   nice       tolerated     tolerated         FAILS at verify()
 */
class MockTypeComparisonTest {

    @Nested
    @DisplayName("createMock (default): catches unexpected calls, ignores order")
    class DefaultMock {

        @Test
        @DisplayName("a default mock tolerates the two mails arriving in the wrong order")
        void wrongOrderIsTolerated() {
            MailSender mail = createMock(MailSender.class);
            mail.sendConfirmation("a@tum.de");
            mail.sendWelcome("a@tum.de");
            replay(mail);

            // Both calls happen, just swapped. A default mock only checks WHICH calls
            // happened and how often -- never in which sequence.
            assertDoesNotThrow(() -> new SubscriptionService(mail).subscribeInWrongOrder("a@tum.de"));
            verify(mail);
        }

        @Test
        @DisplayName("a default mock rejects a third, unrecorded mail")
        void unexpectedCallFails() {
            MailSender mail = createMock(MailSender.class);
            mail.sendConfirmation("a@tum.de");
            mail.sendWelcome("a@tum.de");
            replay(mail);

            // sendWelcome was recorded once; the second one is "Unexpected method call".
            assertThrows(AssertionError.class,
                    () -> new SubscriptionService(mail).subscribeAndSpam("a@tum.de"));
        }

        @Test
        @DisplayName("a default mock fails at verify() when a recorded mail never went out")
        void missingCallFailsOnVerify() {
            MailSender mail = createMock(MailSender.class);
            mail.sendConfirmation("a@tum.de");
            mail.sendWelcome("a@tum.de");
            replay(mail);

            // The SUT is never exercised at all -> nothing happened -> verify() is the
            // only thing that can notice. This is why a test without verify() is worthless.
            assertThrows(AssertionError.class, () -> verify(mail));
        }
    }

    @Nested
    @DisplayName("createStrictMock: additionally enforces the call order")
    class StrictMock {

        @Test
        @DisplayName("a strict mock accepts the documented order confirmation-then-welcome")
        void rightOrderPasses() {
            MailSender mail = createStrictMock(MailSender.class);
            mail.sendConfirmation("a@tum.de");
            mail.sendWelcome("a@tum.de");
            replay(mail);

            new SubscriptionService(mail).subscribe("a@tum.de");

            verify(mail);
        }

        @Test
        @DisplayName("a strict mock is the only type that catches welcome-before-confirmation")
        void wrongOrderFails() {
            MailSender mail = createStrictMock(MailSender.class);
            mail.sendConfirmation("a@tum.de");
            mail.sendWelcome("a@tum.de");
            replay(mail);

            // Reach for strict ONLY when the task says "first ... then ...".
            // Used carelessly it makes tests brittle against harmless reorderings.
            assertThrows(AssertionError.class,
                    () -> new SubscriptionService(mail).subscribeInWrongOrder("a@tum.de"));
        }
    }

    @Nested
    @DisplayName("createNiceMock: waves through anything you did not record")
    class NiceMock {

        @Test
        @DisplayName("a nice mock silently allows the spam mail -- so it cannot prove a call did NOT happen")
        void unexpectedCallIsSilentlyAllowed() {
            MailSender mail = createNiceMock(MailSender.class);
            mail.sendConfirmation("a@tum.de");
            mail.sendWelcome("a@tum.de");
            replay(mail);

            // THIS is the trap behind "your test does not fail on a wrong implementation":
            // the extra sendWelcome is a real defect and the nice mock does not care.
            assertDoesNotThrow(() -> new SubscriptionService(mail).subscribeAndSpam("a@tum.de"));
            assertDoesNotThrow(() -> verify(mail));
        }

        @Test
        @DisplayName("a nice mock still fails at verify() when a recorded mail never went out")
        void missingCallStillFailsOnVerify() {
            MailSender mail = createNiceMock(MailSender.class);
            mail.sendConfirmation("a@tum.de");
            mail.sendWelcome("a@tum.de");
            replay(mail);

            // "Nice" only relaxes UNEXPECTED calls. Recorded expectations are still verified,
            // which is the one guarantee a nice mock does give you.
            assertThrows(AssertionError.class, () -> verify(mail));
        }
    }
}
