package ise.mocking.exam_smarthome;

/**
 * THE RED TEST MODEL from the exam UML, written out literally:
 *
 *   DeviceMock
 *     - owner: HomeOwner
 *     - lastCommand: String
 *     + register(HomeOwner): void
 *     + executeCommand(String): void
 *
 * This is a HAND-WRITTEN test double -- no framework. It records what was done to it
 * so the test can inspect it afterwards, which by the course taxonomy makes it a SPY
 * (it records calls) doubling as a MOCK (the test then verifies them).
 *
 * Worth having next to the EasyMock version: the exam diagram shows this shape, and
 * quiz question 15 asks you to match each double to its characteristic.
 */
public class DeviceMock implements Device {

    private HomeOwner owner;
    private String lastCommand;
    private int registerCallCount;
    private int executeCommandCallCount;

    @Override
    public void register(HomeOwner owner) {
        this.owner = owner;
        this.registerCallCount++;
    }

    @Override
    public void executeCommand(String command) {
        this.lastCommand = command;
        this.executeCommandCallCount++;
    }

    public HomeOwner getOwner() {
        return owner;
    }

    public String getLastCommand() {
        return lastCommand;
    }

    public int getRegisterCallCount() {
        return registerCallCount;
    }

    public int getExecuteCommandCallCount() {
        return executeCommandCallCount;
    }
}
