package ise.mocking.s16_injectionseams;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A mock is useless unless the SUT actually sees it. These are the four seams you
 * can reach without annotations, plus the one class that has no seam at all.
 */
class InjectionSeamTest {

    @Test
    @DisplayName("SEAM A: constructor injection hands the mock over at construction time")
    void constructorSeam() {
        WaterPump pump = createMock(WaterPump.class);
        expect(pump.pump(250)).andReturn(true);
        replay(pump);

        assertTrue(new ConstructorInjectedCoffeeMachine(pump).brew(250));
        verify(pump);
    }

    @Test
    @DisplayName("SEAM B: setter injection needs the setter call before exercising the SUT")
    void setterSeam() {
        WaterPump pump = createMock(WaterPump.class);
        expect(pump.pump(250)).andReturn(true);
        replay(pump);

        SetterInjectedCoffeeMachine machine = new SetterInjectedCoffeeMachine();
        machine.setPump(pump);   // forget this line and you get a NullPointerException

        assertTrue(machine.brew(250));
        verify(pump);
    }

    @Test
    @DisplayName("SEAM D: parameter injection passes the mock straight into the method")
    void parameterSeam() {
        WaterPump pump = createMock(WaterPump.class);
        expect(pump.pump(250)).andReturn(true);
        replay(pump);

        assertTrue(new ParameterInjectedCoffeeMachine().brew(250, pump));
        verify(pump);
    }

    @Test
    @DisplayName("SEAM E: when the SUT creates its own collaborator, mock the FACTORY instead")
    void factorySeam() {
        WaterPumpFactory factory = createMock(WaterPumpFactory.class);
        WaterPump pump = createMock(WaterPump.class);

        expect(factory.createPump()).andReturn(pump);
        expect(pump.pump(250)).andReturn(true);
        replay(factory, pump);

        assertTrue(new FactoryInjectedCoffeeMachine(factory).brew(250));
        verify(factory, pump);
    }

    @Test
    @DisplayName("NO SEAM: a SUT that news up its collaborator can only ever be tested for real")
    void noSeamAtAll() {
        // There is nowhere to hand a mock in, so this is an integration test wearing a
        // unit test costume: the assertions describe BuiltInWaterPump, not the machine.
        UnmockableCoffeeMachine machine = new UnmockableCoffeeMachine();

        assertTrue(machine.brew(250));
        assertFalse(machine.brew(600));

        // The refactored twin, one line away, is fully controllable -- and that control
        // is what lets you test the machine when the pump is slow, broken or absent.
        WaterPump pump = createMock(WaterPump.class);
        expect(pump.pump(600)).andReturn(true);   // a pump that CAN do 600ml
        replay(pump);
        assertTrue(new ConstructorInjectedCoffeeMachine(pump).brew(600));
        verify(pump);
    }
}
