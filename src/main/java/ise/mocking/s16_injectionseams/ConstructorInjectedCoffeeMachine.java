package ise.mocking.s16_injectionseams;

/** SEAM A -- constructor injection. The dependency is impossible to forget. */
public class ConstructorInjectedCoffeeMachine {

    private final WaterPump pump;

    public ConstructorInjectedCoffeeMachine(WaterPump pump) {
        this.pump = pump;
    }

    public boolean brew(int millilitres) {
        return pump.pump(millilitres);
    }
}
