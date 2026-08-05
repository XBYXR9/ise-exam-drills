package ise.mocking.s16_injectionseams;

/**
 * SEAM E -- the SUT still creates its pump per brew, but asks a factory for it.
 * This is the refactoring that rescues UnmockableCoffeeMachine: mock the factory,
 * and the object it hands back is yours.
 */
public class FactoryInjectedCoffeeMachine {

    private final WaterPumpFactory factory;

    public FactoryInjectedCoffeeMachine(WaterPumpFactory factory) {
        this.factory = factory;
    }

    public boolean brew(int millilitres) {
        return factory.createPump().pump(millilitres);
    }
}
