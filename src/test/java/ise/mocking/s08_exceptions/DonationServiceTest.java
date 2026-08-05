package ise.mocking.s08_exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** A collaborator that blows up: one SUT propagates the failure, one handles it. */
class DonationServiceTest {

    @Test
    @DisplayName("donate() lets the gateway timeout reach the caller unchanged")
    void failurePropagates() {
        CardProcessor processor = createMock(CardProcessor.class);
        expect(processor.capture("D-1", 25.0)).andThrow(new GatewayTimeoutException("timeout"));
        replay(processor);

        DonationService service = new DonationService(processor);

        GatewayTimeoutException thrown = assertThrows(GatewayTimeoutException.class,
                () -> service.donate("D-1", 25.0));

        // Asserting the message proves the ORIGINAL exception surfaced, rather than a
        // fresh one the SUT invented while swallowing the real cause.
        assertEquals("timeout", thrown.getMessage());
        verify(processor);
    }

    @Test
    @DisplayName("donateSafely() converts the timeout into false and counts the failure")
    void failureIsHandledGracefully() {
        CardProcessor processor = createMock(CardProcessor.class);
        expect(processor.capture("D-1", 25.0)).andThrow(new GatewayTimeoutException("timeout"));
        replay(processor);

        DonationService service = new DonationService(processor);

        assertFalse(service.donateSafely("D-1", 25.0));
        // The state assertion is the strong one: an empty catch block would also return
        // false, but it would leave failedAttempts at 0.
        assertEquals(1, service.getFailedAttempts());
        verify(processor);
    }

    @Test
    @DisplayName("a successful capture leaves the failure counter untouched")
    void successDoesNotCountAsFailure() {
        CardProcessor processor = createMock(CardProcessor.class);
        expect(processor.capture("D-1", 25.0)).andReturn(true);
        replay(processor);

        DonationService service = new DonationService(processor);

        assertTrue(service.donateSafely("D-1", 25.0));
        assertEquals(0, service.getFailedAttempts());
        verify(processor);
    }
}
