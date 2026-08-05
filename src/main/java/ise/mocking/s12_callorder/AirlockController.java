package ise.mocking.s12_callorder;

/**
 * SUT: seal the inner door BEFORE opening the outer one. Both orderings call the
 * same two methods the same number of times, so a default mock cannot tell them
 * apart -- only a strict mock can.
 */
public class AirlockController {

    private final Airlock airlock;

    public AirlockController(Airlock airlock) {
        this.airlock = airlock;
    }

    public boolean cycle() {
        if (!airlock.sealInnerDoor()) {
            return false;
        }
        return airlock.openOuterDoor();
    }
}
