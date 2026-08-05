package ise.mocking.exam_docking;

import java.util.ArrayList;
import java.util.List;

/** REAL class. assignedShips is the observable state the test must assert on. */
public class Astronaut {

    private final List<Spaceship> assignedShips = new ArrayList<>();

    public void assignShip(Spaceship spaceship) {
        assignedShips.add(spaceship);
    }

    public List<Spaceship> getAssignedShips() {
        return assignedShips;
    }
}
