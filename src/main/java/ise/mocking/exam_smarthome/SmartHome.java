package ise.mocking.exam_smarthome;

import java.util.ArrayList;
import java.util.List;

/**
 * THE SUT (orange box in the exam UML).
 *
 *   +addDevice(Device): boolean
 *   +removeDevice(Device): boolean
 *   +sendCommand(Device, String): void
 *
 * Read the three task descriptions carefully -- they tell you exactly what to assert:
 *   addDevice    "the device object is called correctly", "attributes updated",
 *                "returns the correct value"      -> verify + state + return value
 *   removeDevice "attributes updated", "returns the correct value"
 *                (note: NO mention of the device being called -> nothing is expected
 *                 on the mock, so any call to it must fail the test)
 *   sendCommand  "the device object is called correctly and receives the expected
 *                 command"                        -> verify with the exact argument
 */
public class SmartHome {

    private final HomeOwner owner;
    private final List<Device> devices = new ArrayList<>();

    public SmartHome(HomeOwner owner) {
        this.owner = owner;
    }

    /** Registers the device with this home. @return false if it is null or already known. */
    public boolean addDevice(Device device) {
        if (device == null || devices.contains(device)) {
            return false;
        }
        device.register(owner);
        devices.add(device);
        return true;
    }

    /** @return false if the device was not registered here in the first place. */
    public boolean removeDevice(Device device) {
        return devices.remove(device);
    }

    /** A device that is not registered here must not be commanded. */
    public void sendCommand(Device device, String command) {
        if (devices.contains(device)) {
            device.executeCommand(command);
        }
    }

    public List<Device> getDevices() {
        return devices;
    }

    public HomeOwner getOwner() {
        return owner;
    }
}
