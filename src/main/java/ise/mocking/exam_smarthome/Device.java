package ise.mocking.exam_smarthome;

/**
 * The collaborator interface from the green system model.
 * Both methods return void, so expect() cannot be used on either -- everything is
 * recorded with expectLastCall(), and verify() is the only witness that they ran.
 */
public interface Device {

    void register(HomeOwner owner);

    void executeCommand(String command);
}
