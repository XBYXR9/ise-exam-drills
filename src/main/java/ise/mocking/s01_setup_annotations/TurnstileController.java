package ise.mocking.s01_setup_annotations;

/**
 * SUT with a no-arg constructor and package-private-ish fields.
 * That shape is what makes @TestSubject field injection possible: EasyMock walks
 * the SUT's declared fields and assigns each @Mock whose type fits.
 */
public class TurnstileController {

    private MembershipRegistry membershipRegistry;   // injected by type
    private AccessLog accessLog;                     // injected by type

    public boolean requestEntry(String memberId) {
        boolean valid = membershipRegistry.isMembershipValid(memberId);
        accessLog.record(memberId, valid);
        return valid;
    }
}
