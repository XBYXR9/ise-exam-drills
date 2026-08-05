package ise.practice.mocking;

import ise.mocking.exam_docking.Astronaut;
import ise.mocking.exam_docking.DockingControlSystem;
import ise.mocking.exam_docking.Spaceship;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * PRACTICE DRILL -- Final exam, exercise 4 ("Mocky", 18 points).
 *
 * Delete @Disabled, fill in the TODOs, then diff against
 *     ise.practice.mocking.solutions.DockingControlSystemSolutionTest
 *
 * =====================================================================
 * PROBLEM STATEMENT (exam wording)
 *
 *   DockingControlSystem  +dock(Astronaut, Spaceship): void
 *   Astronaut             +assignShip(Spaceship): void   assignedShips *
 *   Spaceship (interface) +dock(Astronaut): boolean
 *
 * In this exercise, we want to write one unit test to verify an existing space
 * station management model. The DockingControlSystem object in the system model is
 * not yet implemented and needs to be mocked. Write a unit test for the Spaceship
 * object. There is no implementation yet, but there is already an interface for
 * Spaceship. Use the Java testing framework EasyMock to mock this object.
 * =====================================================================
 */
@Disabled("PRACTICE DRILL: delete this line, then fill in the TODOs below")
class DockingControlSystemPracticeTest {

    // TODO Task 1 -- "Setup the DockingControlSystemTest class following the Mock
    //   Object Pattern. Add the attributes dockingControlSystem and spaceshipMock and
    //   annotate them accordingly."
    //   Graded feedback last time: "You did not configure the test subject correctly."

    @Test
    void testDockAstronautSuccessful() {
        // TODO Task 2 -- "Write a test method named testDockAstronautSuccessful()."
        //   The ship accepts the docking. Assert the interaction AND the astronaut state.
    }

    @Test
    void testDockAstronautFailure() {
        // TODO Task 3 -- "the docking fails, because the method dock(Astronaut): boolean
        //   of the Spaceship mock returns false. Make sure that not only the behavior of
        //   the astronaut is correct, but also that the dock(Astronaut) method was
        //   actually executed by the DockingControlSystem."
        //
        //   Graded feedback: "Make sure that your implementation of
        //   testDockAstronautFailure() fails if the dock() method is never executed."
        //   That sentence names the exact call you are missing.
    }
}
