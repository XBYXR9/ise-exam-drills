package ise.mocking.exam_vehicle;

/**
 * SUT. Two guards in sequence:
 *   1. checkEngine() -- if false, assign(driver) must NEVER run
 *   2. assign(driver) -- if false, the driver must NOT record the vehicle
 */
public class VehicleManagementSystem {

    public void assign(Driver driver, Vehicle vehicle) {
        if (vehicle.checkEngine()) {
            if (vehicle.assign(driver)) {
                driver.assignVehicle(vehicle);
            }
        }
    }
}
