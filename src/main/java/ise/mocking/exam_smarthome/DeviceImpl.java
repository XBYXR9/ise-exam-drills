package ise.mocking.exam_smarthome;

/**
 * The real implementation from the system model. Present so the package compiles as
 * the diagram shows it; the tests never use it, because the whole point of the
 * exercise is that Device is replaced by a double.
 */
public class DeviceImpl implements Device {

    private HomeOwner owner;
    private String lastCommand;

    @Override
    public void register(HomeOwner owner) {
        this.owner = owner;
    }

    @Override
    public void executeCommand(String command) {
        this.lastCommand = command;
    }

    public HomeOwner getOwner() {
        return owner;
    }

    public String getLastCommand() {
        return lastCommand;
    }
}
