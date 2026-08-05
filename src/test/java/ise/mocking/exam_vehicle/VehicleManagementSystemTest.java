package ise.mocking.exam_vehicle;

import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RETAKE EXAM, EXERCISE 5 -- the full solution, all four tasks.
 *
 * Task 1: set the class up following the Mock Object Pattern, with the attributes
 *         vehicleManagementSystem and vehicleMock, annotated accordingly.
 * Task 2: testAssignDriverSuccessful()
 * Task 3: testAssignDriverFailure()   -- must prove assign(Driver) really ran
 * Task 4: testAssignWithEngineFailure() -- must prove assign(Driver) did NOT run
 *
 * What cost points last time, and what fixes it:
 *   "testAssignDriverFailure() does not fail on a wrong implementation"
 *        -> the test asserted only that the driver list was empty. An implementation
 *           that never calls assign(driver) at all leaves it empty too. verify() is
 *           what separates the two.
 *   "testAssignWithEngineFailure() ... should check that the assign method on the
 *    vehicle mock is not called"
 *        -> assign(Driver) must be left out of the record phase, on a DEFAULT mock,
 *           so that calling it is an immediate failure.
 */
@ExtendWith(EasyMockExtension.class)
class VehicleManagementSystemTest {

    // TASK 1 -- the two annotated attributes the task asks for, by those exact names.
    @TestSubject
    private VehicleManagementSystem vehicleManagementSystem = new VehicleManagementSystem();

    @Mock
    private Vehicle vehicleMock;   // default mock: unrecorded calls fail immediately

    private Driver driver;

    @BeforeEach
    void setUp() {
        driver = new Driver();     // the REAL driver, so its state can be asserted
    }

    @Test
    @DisplayName("engine healthy and vehicle accepts: the driver ends up holding the vehicle")
    void testAssignDriverSuccessful() {
        // RECORD -- both collaborator calls are expected, in this order
        expect(vehicleMock.checkEngine()).andReturn(true);
        expect(vehicleMock.assign(driver)).andReturn(true);

        // REPLAY
        replay(vehicleMock);

        // EXERCISE
        vehicleManagementSystem.assign(driver, vehicleMock);

        // VERIFY -- interaction ...
        verify(vehicleMock);
        // ... AND state. Without these three lines an implementation that calls both
        // mock methods and then forgets driver.assignVehicle(vehicle) still passes.
        assertEquals(1, driver.getAssignedVehicles().size());
        assertTrue(driver.getAssignedVehicles().contains(vehicleMock));
        assertEquals(vehicleMock, driver.getAssignedVehicles().get(0));
    }

    @Test
    @DisplayName("vehicle refuses the driver: assign IS attempted, but nothing is recorded on the driver")
    void testAssignDriverFailure() {
        expect(vehicleMock.checkEngine()).andReturn(true);
        expect(vehicleMock.assign(driver)).andReturn(false);

        replay(vehicleMock);

        vehicleManagementSystem.assign(driver, vehicleMock);

        // THE line that was missing last time. It asserts that assign(driver) was
        // actually executed; an implementation that skips it entirely would leave the
        // recorded expectation unsatisfied and fail right here.
        verify(vehicleMock);

        assertEquals(0, driver.getAssignedVehicles().size());
        assertTrue(driver.getAssignedVehicles().isEmpty());
        assertFalse(driver.getAssignedVehicles().contains(vehicleMock));
    }

    @Test
    @DisplayName("engine check fails: assign(Driver) must never be called at all")
    void testAssignWithEngineFailure() {
        expect(vehicleMock.checkEngine()).andReturn(false);
        // assign(Driver) is deliberately NOT recorded. vehicleMock is a default mock,
        // so any call to assign() raises "Unexpected method call" on the spot. That
        // absence is the explicit verification of non-execution the task demanded.
        //
        // Do NOT use @Mock(type = MockType.NICE) here: a nice mock would swallow the
        // forbidden call and the test could never go red.

        replay(vehicleMock);

        vehicleManagementSystem.assign(driver, vehicleMock);

        verify(vehicleMock);   // proves checkEngine() itself did happen
        assertEquals(0, driver.getAssignedVehicles().size());
        assertFalse(driver.getAssignedVehicles().contains(vehicleMock));
    }
}
