package ise.mocking.exam_docking;

/**
 * SUT. Only when the ship accepts the docking does the astronaut record the ship.
 * The exam feedback was: "Make sure that your implementation of
 * testDockAstronautFailure() fails if the dock() method is never executed."
 * -- i.e. verify(spaceshipMock) is not optional.
 */
public class DockingControlSystem {

    public void dock(Astronaut astronaut, Spaceship spaceship) {
        if (spaceship.dock(astronaut)) {
            astronaut.assignShip(spaceship);
        }
    }
}
