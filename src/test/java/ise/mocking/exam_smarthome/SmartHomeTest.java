package ise.mocking.exam_smarthome;

import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MOCK EXAM (Jun 2026), EXERCISE 3 -- Mocking, 25 points. The full solution.
 *
 * Task 1: "Setup the SmartHomeTest class. Add the test subject and mock attributes
 *          and annotate them."
 * Task 2: testAddDevice()    -- device called correctly + attributes updated + return value
 * Task 3: testRemoveDevice() -- attributes updated + return value
 * Task 4: testSendCommand()  -- device called correctly AND receives the expected command
 *
 * Note what is different from the Vehicle and Docking exercises: both Device methods
 * return VOID. There is no expect(...) to write, so every expectation is
 * `mock.method(args); expectLastCall();` and verify() is the only thing that can
 * prove the call happened at all.
 */
@ExtendWith(EasyMockExtension.class)
class SmartHomeTest {

    // TASK 1 -- the two annotated attributes.
    @TestSubject
    private SmartHome smartHome = new SmartHome(new HomeOwner("Yahya"));

    @Mock
    private Device deviceMock;   // DEFAULT mock: any unrecorded call fails the test

    private HomeOwner owner;

    @BeforeEach
    void setUp() {
        owner = smartHome.getOwner();
    }

    @Test
    @DisplayName("adding a device registers it with the owner, stores it, and reports success")
    void testAddDevice() {
        // RECORD -- register() returns void, so the call itself is the expectation.
        // Passing `owner` rather than anyObject() is what proves the SUT handed the
        // device THIS home's owner and not some other object.
        deviceMock.register(owner);
        expectLastCall();

        replay(deviceMock);

        boolean added = smartHome.addDevice(deviceMock);

        // "the device object is called correctly"
        verify(deviceMock);
        // "the attributes of the SmartHome class are updated correctly"
        assertEquals(1, smartHome.getDevices().size());
        assertTrue(smartHome.getDevices().contains(deviceMock));
        // "the method you are testing returns the correct value"
        assertTrue(added);
    }

    @Test
    @DisplayName("adding the same device twice is refused and does not register it again")
    void testAddDeviceTwiceIsRefused() {
        deviceMock.register(owner);
        expectLastCall().once();   // exactly ONCE, not twice

        replay(deviceMock);

        assertTrue(smartHome.addDevice(deviceMock));
        boolean addedAgain = smartHome.addDevice(deviceMock);

        verify(deviceMock);
        assertFalse(addedAgain);
        // The count is what catches a duplicate check that returns false but still adds.
        assertEquals(1, smartHome.getDevices().size());
    }

    @Test
    @DisplayName("removing a registered device empties the list and reports success")
    void testRemoveDevice() {
        // The device must be added first, which is the only call the mock expects.
        deviceMock.register(owner);
        expectLastCall();

        replay(deviceMock);

        smartHome.addDevice(deviceMock);
        boolean removed = smartHome.removeDevice(deviceMock);

        // Task 3 says nothing about the device being called, so nothing extra is
        // recorded -- and on a default mock that means removeDevice() touching the
        // device in any way would fail here.
        verify(deviceMock);
        assertEquals(0, smartHome.getDevices().size());
        assertFalse(smartHome.getDevices().contains(deviceMock));
        assertTrue(removed);
    }

    @Test
    @DisplayName("removing a device that was never added reports failure and changes nothing")
    void testRemoveUnknownDevice() {
        replay(deviceMock);   // zero expectations: nothing may happen to the device

        boolean removed = smartHome.removeDevice(deviceMock);

        verify(deviceMock);
        assertFalse(removed);
        assertEquals(0, smartHome.getDevices().size());
    }

    @Test
    @DisplayName("sending a command forwards exactly that command string to the device")
    void testSendCommand() {
        deviceMock.register(owner);
        expectLastCall();
        // The literal "TURN_ON" is the assertion: with anyString() a SUT that forwards
        // the wrong command, or a hard-coded one, would still pass.
        deviceMock.executeCommand("TURN_ON");
        expectLastCall();

        replay(deviceMock);

        smartHome.addDevice(deviceMock);
        smartHome.sendCommand(deviceMock, "TURN_ON");

        // sendCommand returns void and changes no SmartHome state, so verify() is the
        // ONLY thing in this test that can fail on a broken implementation.
        verify(deviceMock);
    }

    @Test
    @DisplayName("a device that was never registered must not receive commands")
    void testSendCommandToUnknownDevice() {
        // executeCommand is deliberately NOT recorded -- calling it is the defect.
        replay(deviceMock);

        smartHome.sendCommand(deviceMock, "TURN_ON");

        verify(deviceMock);
    }
}
