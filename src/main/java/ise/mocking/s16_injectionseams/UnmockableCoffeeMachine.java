package ise.mocking.s16_injectionseams;

/**
 * THE ANTI-PATTERN. The dependency is created inside the method, so no test can
 * ever put a mock in its place: there is no seam. This violates the Dependency
 * Inversion Principle, and in an exam it is the sign that you are looking at the
 * wrong class -- find the interface.
 */
public class UnmockableCoffeeMachine {

    public boolean brew(int millilitres) {
        WaterPump pump = new BuiltInWaterPump();   // hard-wired: nothing to inject
        return pump.pump(millilitres);
    }
}
