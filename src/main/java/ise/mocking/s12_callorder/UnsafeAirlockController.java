package ise.mocking.s12_callorder;

/**
 * The same two calls in the WRONG order: the outer door opens while the inner one
 * is still unsealed. Scenario 12 points a strict mock at this class to prove the
 * test can actually go red.
 */
public class UnsafeAirlockController {

    private final Airlock airlock;

    public UnsafeAirlockController(Airlock airlock) {
        this.airlock = airlock;
    }

    public boolean cycle() {
        boolean opened = airlock.openOuterDoor();
        airlock.sealInnerDoor();
        return opened;
    }
}
