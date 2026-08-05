package ise.mocking.exam_vehicle;

import java.util.ArrayList;
import java.util.List;

/**
 * REAL class, never mocked. The exam feedback asked for "not only the behavior of
 * the driver", so this list is the state half of every assertion.
 */
public class Driver {

    private final List<Vehicle> assignedVehicles = new ArrayList<>();

    public void assignVehicle(Vehicle vehicle) {
        assignedVehicles.add(vehicle);
    }

    public List<Vehicle> getAssignedVehicles() {
        return assignedVehicles;
    }
}
