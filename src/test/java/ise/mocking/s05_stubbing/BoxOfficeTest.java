package ise.mocking.s05_stubbing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

/** Every way to feed a value back into the SUT. */
class BoxOfficeTest {

    @Test
    @DisplayName("a single recorded return value serves exactly one call")
    void singleReturn() {
        TicketSequence sequence = createMock(TicketSequence.class);
        expect(sequence.getVenueCode()).andReturn("AUD1");
        expect(sequence.nextTicketNumber()).andReturn(41);
        replay(sequence);

        assertIterableEquals(List.of("AUD1-41"), new BoxOffice(sequence).issueTickets(1));
        verify(sequence);
    }

    @Test
    @DisplayName("consecutive andReturn calls hand out a different number to every ticket")
    void consecutiveReturns() {
        TicketSequence sequence = createMock(TicketSequence.class);
        // anyTimes() on the constant: the venue code is the same for every ticket, and
        // pinning it to exactly 3 would break the test on a harmless refactor.
        expect(sequence.getVenueCode()).andReturn("AUD1").anyTimes();
        // The chain is consumed in order: 1st call -> 41, 2nd -> 42, 3rd -> 43.
        expect(sequence.nextTicketNumber()).andReturn(41).andReturn(42).andReturn(43);
        replay(sequence);

        List<String> tickets = new BoxOffice(sequence).issueTickets(3);

        verify(sequence);
        // Three DIFFERENT numbers. A SUT that reuses one number would still pass a
        // plain size check, but it cannot pass this one.
        assertIterableEquals(List.of("AUD1-41", "AUD1-42", "AUD1-43"), tickets);
    }

    @Test
    @DisplayName("times(n) serves the same value to exactly n calls, no more and no fewer")
    void fixedNumberOfReturns() {
        TicketSequence sequence = createMock(TicketSequence.class);
        expect(sequence.getVenueCode()).andReturn("AUD1").times(2);
        expect(sequence.nextTicketNumber()).andReturn(7).times(2);
        replay(sequence);

        assertIterableEquals(List.of("AUD1-7", "AUD1-7"), new BoxOffice(sequence).issueTickets(2));
        // verify() is what turns times(2) into an assertion: issuing only one ticket
        // leaves an unconsumed expectation and goes red here.
        verify(sequence);
    }

    @Test
    @DisplayName("andStubReturn answers any number of calls and is NOT checked by verify")
    void stubReturnIsNeverVerified() {
        TicketSequence sequence = createMock(TicketSequence.class);
        expect(sequence.getVenueCode()).andStubReturn("AUD1");
        expect(sequence.nextTicketNumber()).andStubReturn(99);
        replay(sequence);

        // Zero tickets means neither stub is ever touched, and verify() still passes,
        // because a stub records no obligation. Use andStubReturn for background values
        // only, never for the call whose occurrence you are trying to prove.
        assertEquals(0, new BoxOffice(sequence).issueTickets(0).size());
        verify(sequence);
    }
}
