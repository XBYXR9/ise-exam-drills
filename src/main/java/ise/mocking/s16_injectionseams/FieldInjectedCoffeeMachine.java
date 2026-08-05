package ise.mocking.s16_injectionseams;

/**
 * SEAM C -- plain field, no setter, no constructor argument.
 * Only reachable from a test via @TestSubject + @Mock, which is exactly why the
 * exam SUTs look like this.
 */
public class FieldInjectedCoffeeMachine {

    private WaterPump pump;

    public boolean brew(int millilitres) {
        return pump.pump(millilitres);
    }
}
