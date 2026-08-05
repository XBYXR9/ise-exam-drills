package ise.mocking.s16_injectionseams;

/** SEAM D -- pass the collaborator in as a method parameter. No state at all. */
public class ParameterInjectedCoffeeMachine {

    public boolean brew(int millilitres, WaterPump pump) {
        return pump.pump(millilitres);
    }
}
