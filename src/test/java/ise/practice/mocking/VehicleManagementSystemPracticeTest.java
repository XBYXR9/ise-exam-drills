package ise.practice.mocking;

import ise.mocking.exam_vehicle.Driver;
import ise.mocking.exam_vehicle.Vehicle;
import ise.mocking.exam_vehicle.VehicleManagementSystem;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * PRACTICE DRILL -- Retake exam, exercise 5 (Mocking, 15 points).
 *
 * Delete the @Disabled below, fill in the TODOs, and run:
 *     gradlew.bat test --tests "ise.practice.mocking.VehicleManagementSystemPracticeTest"
 *
 * Then check yourself against
 *     ise.practice.mocking.solutions.VehicleManagementSystemSolutionTest
 *
 * =====================================================================
 * PROBLEM STATEMENT (exam wording)
 *
 *   VehicleManagementSystem  +assign(Driver, Vehicle): void
 *   Vehicle                  +assign(Driver): boolean   +checkEngine(): boolean
 *   Driver                   +assignVehicle(Vehicle): void   assignedVehicles *
 *
 * Write a unit test for VehicleManagementSystem using an EasyMock Vehicle. The
 * Vehicle object is not implemented yet, but there is an interface. Use the Java
 * testing framework EasyMock to mock this object.
 * =====================================================================
 */
@Disabled("PRACTICE DRILL: delete this line, then fill in the TODOs below")
class VehicleManagementSystemPracticeTest {

    // TODO Task 1 -- Prepare VehicleManagementSystemTest.
    //   "Set up the VehicleManagementSystemTest class following the Mock Object
    //    Pattern. Add the attributes vehicleManagementSystem and vehicleMock and
    //    annotate them accordingly."
    //
    //   Three things, all required:
    //     - the class annotation that activates EasyMock under JUnit 5
    //     - a field for the system under test, annotated and ALREADY instantiated
    //     - a field for the Vehicle mock, annotated
    //   Also give yourself a real Driver in a @BeforeEach -- you will need its state.

    @Test
    void testAssignDriverSuccessful() {
        // TODO Task 2 -- "This test should verify that a vehicle is successfully
        //   assigned to a driver."
        //
        //   RECORD:   the engine check passes, and the vehicle accepts the driver
        //   REPLAY:   switch the mock over
        //   EXERCISE: call the system under test
        //   VERIFY:   verify the mock AND assert the real driver now holds the vehicle
        //             (count, contains -- not just "no exception")
    }

    @Test
    void testAssignDriverFailure() {
        // TODO Task 3 -- "the vehicle assignment fails because the method
        //   Vehicle.assign(Driver): boolean of the Vehicle mock returns false. Make
        //   sure that not only the behavior of the driver is correct, but also that
        //   the assign(Driver) method was actually executed by the
        //   VehicleManagementSystem."
        //
        //   The second sentence is the whole task. An empty driver list is what
        //   "refused" and "never asked" have in common, so state assertions alone
        //   cannot tell them apart. Which single call proves assign() really ran?
    }

    @Test
    void testAssignWithEngineFailure() {
        // TODO Task 4 -- "verifies the behavior when the checkEngine() method on the
        //   Vehicle mock returns false before assignment. The test should ensure that
        //   the assign(Driver) method is NOT called if the engine check fails. This
        //   ... requires an explicit verification of a method's non-execution."
        //
        //   Two traps:
        //     - which mock TYPE lets you prove a call did not happen? (not the nice one)
        //     - the playbook suggests .times(0) for this. Try it and see what happens.
    }
}
