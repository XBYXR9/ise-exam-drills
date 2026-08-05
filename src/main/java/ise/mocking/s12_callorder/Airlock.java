package ise.mocking.s12_callorder;

/** Collaborator whose two operations have a MANDATORY order. */
public interface Airlock {

    boolean sealInnerDoor();

    boolean openOuterDoor();
}
