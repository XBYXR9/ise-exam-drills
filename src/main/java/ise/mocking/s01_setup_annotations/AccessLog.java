package ise.mocking.s01_setup_annotations;

/** Collaborator: audit trail. Noise for most tests -- a good candidate for a NICE mock. */
public interface AccessLog {

    void record(String memberId, boolean granted);
}
