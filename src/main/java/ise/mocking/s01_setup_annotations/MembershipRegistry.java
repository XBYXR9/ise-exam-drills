package ise.mocking.s01_setup_annotations;

/** Collaborator: the campus system that knows whether a gym membership is still valid. */
public interface MembershipRegistry {

    boolean isMembershipValid(String memberId);
}
