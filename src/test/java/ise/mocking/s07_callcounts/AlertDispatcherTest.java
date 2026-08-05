package ise.mocking.s07_callcounts;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Every call-count qualifier, each shown on the case where it is the right one. */
class AlertDispatcherTest {

    @Test
    @DisplayName("once(): a gateway that accepts immediately is called exactly one time")
    void firstAttemptSucceeds() {
        SmsGateway gateway = createMock(SmsGateway.class);
        expect(gateway.send("+49170", "flood")).andReturn(true).once();
        replay(gateway);

        assertTrue(new AlertDispatcher(gateway, 3).dispatch("+49170", "flood"));
        // once() plus verify() is what catches a retry loop that forgot to return early.
        verify(gateway);
    }

    @Test
    @DisplayName("times(n): all attempts are used up when every send is refused")
    void allAttemptsAreExhausted() {
        SmsGateway gateway = createMock(SmsGateway.class);
        expect(gateway.send("+49170", "flood")).andReturn(false).times(3);
        replay(gateway);

        assertFalse(new AlertDispatcher(gateway, 3).dispatch("+49170", "flood"));
        // An off-by-one in the retry loop (2 or 4 attempts) fails here and nowhere else.
        verify(gateway);
    }

    @Test
    @DisplayName("times(min, max): the retry budget is a range, not an exact number")
    void attemptsStayWithinARange() {
        SmsGateway gateway = createMock(SmsGateway.class);
        expect(gateway.send("+49170", "flood")).andReturn(false).times(2, 5);
        replay(gateway);

        assertFalse(new AlertDispatcher(gateway, 3).dispatch("+49170", "flood"));
        verify(gateway);
    }

    @Test
    @DisplayName("atLeastOnce(): the message must go out, however often the SUT retries")
    void atLeastOneAttempt() {
        SmsGateway gateway = createMock(SmsGateway.class);
        expect(gateway.send("+49170", "flood")).andReturn(false).atLeastOnce();
        replay(gateway);

        assertFalse(new AlertDispatcher(gateway, 3).dispatch("+49170", "flood"));
        verify(gateway);
    }

    @Test
    @DisplayName("anyTimes(): 0..n calls are all acceptable, so on its own it proves nothing")
    void anyNumberOfAttempts() {
        SmsGateway gateway = createMock(SmsGateway.class);
        expect(gateway.send("+49170", "flood")).andReturn(true).anyTimes();
        replay(gateway);

        // anyTimes() also covers ZERO calls, so verify() cannot fail here. The
        // assertTrue is doing all the work; if the SUT never sent anything, only the
        // return value would give it away.
        assertTrue(new AlertDispatcher(gateway, 3).dispatch("+49170", "flood"));
        verify(gateway);
    }

    @Test
    @DisplayName("PLAYBOOK ERROR: times(0) does not exist in EasyMock 5.2.0, it throws IllegalArgumentException")
    void timesZeroIsRejectedByEasyMock() {
        SmsGateway gateway = createMock(SmsGateway.class);

        // MockingPlaybook.java section 07 lists
        //     expect(m.charge(7.0)).andReturn(true).times(0);   // must NOT be called
        // and section 14 "OPTION 2" repeats it. Neither runs: EasyMock validates
        // 1 <= max, so times(0) blows up before your test even reaches replay().
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> expect(gateway.send("+49170", "")).andReturn(true).times(0));

        assertTrue(error.getMessage().contains("maximum must be >= 1"),
                "unexpected message: " + error.getMessage());
    }

    @Test
    @DisplayName("must-not-be-called is expressed by NOT recording the call at all")
    void blankTextIsNeverSent() {
        SmsGateway gateway = createMock(SmsGateway.class);
        // Nothing recorded. On a default mock every unrecorded call is an immediate
        // failure, so "not recorded" already means "must not be called". This is the
        // working replacement for the times(0) that the playbook suggests.
        replay(gateway);

        assertFalse(new AlertDispatcher(gateway, 3).dispatch("+49170", ""));
        verify(gateway);
    }
}
