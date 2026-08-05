package ise.mocking.s16_injectionseams;

/** SEAM B -- setter injection. Needed when a framework demands a no-arg constructor. */
public class SetterInjectedCoffeeMachine {

    private WaterPump pump;

    public void setPump(WaterPump pump) {
        this.pump = pump;
    }

    public boolean brew(int millilitres) {
        return pump.pump(millilitres);
    }
}
