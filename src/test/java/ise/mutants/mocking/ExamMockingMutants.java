package ise.mutants.mocking;

import ise.mocking.exam_docking.Astronaut;
import ise.mocking.exam_docking.DockingControlSystem;
import ise.mocking.exam_docking.Spaceship;
import ise.mocking.exam_vehicle.Driver;
import ise.mocking.exam_vehicle.Vehicle;
import ise.mocking.exam_vehicle.VehicleManagementSystem;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * MUTANTS for the two exam mocking exercises.
 *
 * These four are the graded ones. Each mutation is exactly what the exam feedback
 * described, and the assertion that catches it is named underneath.
 */
final class ExamMockingMutants {

    private ExamMockingMutants() {
    }
}

// ---------------------------------------------------------------------------
// exam_vehicle -- MUTATION: the engine result is ignored, assign runs anyway.
//   Feedback: "testAssignWithEngineFailure() does not fail on a wrong
//              implementation. It should check that the assign method on the
//              vehicle mock is not called."
// ---------------------------------------------------------------------------
class VehicleSystemThatIgnoresTheEngine extends VehicleManagementSystem {

    @Override
    public void assign(Driver driver, Vehicle vehicle) {
        boolean engineOk = vehicle.checkEngine();
        if (vehicle.assign(driver) && engineOk) {   // assign() runs before the guard
            driver.assignVehicle(vehicle);
        }
    }
}

@Tag("mutant")
class VehicleManagementSystemTest_IgnoresTheEngine {

    @Test
    void testAssignWithEngineFailure() {
        Vehicle vehicleMock = createMock(Vehicle.class);   // DEFAULT mock, not nice
        Driver driver = new Driver();

        expect(vehicleMock.checkEngine()).andReturn(false);
        // assign(Driver) deliberately NOT recorded -- that absence is the assertion.
        replay(vehicleMock);

        // CAUGHT BY the unrecorded mock: "Unexpected method call assign(...)".
        // Note what does NOT catch it: the driver list is empty either way, because
        // this mutant's assign() returns false. State assertions alone score zero here.
        new VehicleSystemThatIgnoresTheEngine().assign(driver, vehicleMock);

        verify(vehicleMock);
        assertEquals(0, driver.getAssignedVehicles().size());
        assertFalse(driver.getAssignedVehicles().contains(vehicleMock));
    }
}

// ---------------------------------------------------------------------------
// exam_vehicle -- MUTATION: assign(Driver) is skipped entirely.
//   Feedback: "testAssignDriverFailure() does not fail on a wrong implementation"
// ---------------------------------------------------------------------------
class VehicleSystemThatNeverAssigns extends VehicleManagementSystem {

    @Override
    public void assign(Driver driver, Vehicle vehicle) {
        vehicle.checkEngine();
        // vehicle.assign(driver) is gone, so the driver never gets the vehicle
    }
}

@Tag("mutant")
class VehicleManagementSystemTest_NeverAssigns {

    @Test
    void testAssignDriverFailure() {
        Vehicle vehicleMock = createMock(Vehicle.class);
        Driver driver = new Driver();

        expect(vehicleMock.checkEngine()).andReturn(true);
        expect(vehicleMock.assign(driver)).andReturn(false);
        replay(vehicleMock);

        new VehicleSystemThatNeverAssigns().assign(driver, vehicleMock);

        // CAUGHT BY verify(): assign(driver) was recorded and never happened.
        // The two state assertions below both PASS against this mutant -- an empty
        // driver list is what "refused" and "never asked" have in common. Leaving
        // verify() out is precisely how this test scored zero.
        verify(vehicleMock);

        assertEquals(0, driver.getAssignedVehicles().size());
        assertFalse(driver.getAssignedVehicles().contains(vehicleMock));
    }
}

// ---------------------------------------------------------------------------
// exam_vehicle -- MUTATION: the driver is never told about the vehicle
// ---------------------------------------------------------------------------
class VehicleSystemThatForgetsTheDriver extends VehicleManagementSystem {

    @Override
    public void assign(Driver driver, Vehicle vehicle) {
        if (vehicle.checkEngine()) {
            vehicle.assign(driver);
            // driver.assignVehicle(vehicle) is gone
        }
    }
}

@Tag("mutant")
class VehicleManagementSystemTest_ForgetsTheDriver {

    @Test
    void testAssignDriverSuccessful() {
        Vehicle vehicleMock = createMock(Vehicle.class);
        Driver driver = new Driver();

        expect(vehicleMock.checkEngine()).andReturn(true);
        expect(vehicleMock.assign(driver)).andReturn(true);
        replay(vehicleMock);

        new VehicleSystemThatForgetsTheDriver().assign(driver, vehicleMock);

        // verify() PASSES here: both mock calls happened exactly as recorded. Only the
        // state assertion notices that the real Driver was left empty. This is the
        // mirror image of the mutant above, and the reason you always need both.
        verify(vehicleMock);
        assertEquals(1, driver.getAssignedVehicles().size());
    }
}

// ---------------------------------------------------------------------------
// exam_docking -- MUTATION: dock() is never executed.
//   Feedback: "Make sure that your implementation of testDockAstronautFailure()
//              fails if the dock() method is never executed."
// ---------------------------------------------------------------------------
class DockingSystemThatNeverDocks extends DockingControlSystem {

    @Override
    public void dock(Astronaut astronaut, Spaceship spaceship) {
        // body deleted
    }
}

@Tag("mutant")
class DockingControlSystemTest_NeverDocks {

    @Test
    void testDockAstronautFailure() {
        Spaceship spaceshipMock = createMock(Spaceship.class);
        Astronaut astronaut = new Astronaut();

        expect(spaceshipMock.dock(astronaut)).andReturn(false);
        replay(spaceshipMock);

        new DockingSystemThatNeverDocks().dock(astronaut, spaceshipMock);

        // CAUGHT BY verify() and nothing else: an astronaut with no ships is exactly
        // what an empty method body produces, so both assertions below stay green.
        verify(spaceshipMock);

        assertEquals(0, astronaut.getAssignedShips().size());
        assertFalse(astronaut.getAssignedShips().contains(spaceshipMock));
    }
}
