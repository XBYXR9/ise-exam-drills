package ise.mocking.s01_setup_annotations;

import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.MockType;
import org.easymock.TestSubject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SETUP STYLE A -- @ExtendWith(EasyMockExtension.class) + @TestSubject + @Mock.
 * This is the style the exam task "set up the test class following the Mock Object
 * Pattern" is asking for, and the style whose absence earned the feedback
 * "You did not configure the test subject correctly".
 *
 * Three things must all be true or the injection silently does nothing:
 *   1. the class carries @ExtendWith(EasyMockExtension.class)
 *   2. the SUT field carries @TestSubject and is ALREADY INSTANTIATED
 *   3. each collaborator field carries @Mock and its type matches a SUT field
 */
@ExtendWith(EasyMockExtension.class)
class TurnstileControllerTest {

    @TestSubject
    private TurnstileController turnstileController = new TurnstileController();

    @Mock
    private MembershipRegistry membershipRegistry;

    // NICE: the audit trail is noise here. A nice mock returns defaults for calls
    // we never recorded, so it cannot make the test red for the wrong reason.
    // Never do this for a collaborator whose call you are trying to prove.
    @Mock(type = MockType.NICE)
    private AccessLog accessLog;

    @Test
    @DisplayName("a valid membership opens the turnstile")
    void validMembershipGrantsEntry() {
        expect(membershipRegistry.isMembershipValid("M-1")).andReturn(true);
        replay(membershipRegistry, accessLog);

        boolean granted = turnstileController.requestEntry("M-1");

        verify(membershipRegistry, accessLog);
        // verify() alone would still pass if requestEntry returned a constant, so the
        // return value has to be asserted too. Interaction AND state, always both.
        assertTrue(granted);
    }

    @Test
    @DisplayName("an expired membership keeps the turnstile shut")
    void invalidMembershipDeniesEntry() {
        expect(membershipRegistry.isMembershipValid("M-2")).andReturn(false);
        replay(membershipRegistry, accessLog);

        boolean granted = turnstileController.requestEntry("M-2");

        verify(membershipRegistry);
        // The false branch is what a "return true always" mutation breaks.
        assertFalse(granted);
    }
}
