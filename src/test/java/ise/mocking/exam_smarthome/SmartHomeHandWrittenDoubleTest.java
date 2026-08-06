package ise.mocking.exam_smarthome;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The SAME four tasks solved with the hand-written DeviceMock instead of EasyMock,
 * because the exam UML draws the test model that way.
 *
 * The trade-off, in one line each:
 *   EasyMock      -- an unrecorded call fails IMMEDIATELY, and verify() checks counts
 *                    for you. Nothing to write, nothing to forget.
 *   hand-written  -- you must assert the counts yourself, and a call you forgot to
 *                    think about passes silently. Cheaper to read, easier to get wrong.
 *
 * If the exam gives you a DeviceMock class, use it. If it says "annotate them", it
 * wants @TestSubject and @Mock.
 */
class SmartHomeHandWrittenDoubleTest {

    private SmartHome smartHome;
    private DeviceMock deviceMock;

    @BeforeEach
    void setUp() {
        smartHome = new SmartHome(new HomeOwner("Yahya"));
        deviceMock = new DeviceMock();
    }

    @Test
    @DisplayName("addDevice registers the double with this home owner exactly once")
    void testAddDevice() {
        boolean added = smartHome.addDevice(deviceMock);

        assertTrue(added);
        assertEquals(1, smartHome.getDevices().size());
        assertTrue(smartHome.getDevices().contains(deviceMock));
        // The double replaces verify(): the call count and the captured argument are
        // asserted by hand. Forget these two lines and the test cannot go red on a
        // SUT that never calls register().
        assertEquals(1, deviceMock.getRegisterCallCount());
        assertSame(smartHome.getOwner(), deviceMock.getOwner());
    }

    @Test
    @DisplayName("removeDevice updates the list and never touches the device")
    void testRemoveDevice() {
        smartHome.addDevice(deviceMock);

        boolean removed = smartHome.removeDevice(deviceMock);

        assertTrue(removed);
        assertEquals(0, smartHome.getDevices().size());
        // Nothing beyond the one register() from addDevice may have happened.
        assertEquals(1, deviceMock.getRegisterCallCount());
        assertEquals(0, deviceMock.getExecuteCommandCallCount());
    }

    @Test
    @DisplayName("sendCommand hands the device exactly the command string it was given")
    void testSendCommand() {
        smartHome.addDevice(deviceMock);

        smartHome.sendCommand(deviceMock, "TURN_ON");

        assertEquals(1, deviceMock.getExecuteCommandCallCount());
        // The recorded value is the whole point of lastCommand in the exam diagram.
        assertEquals("TURN_ON", deviceMock.getLastCommand());
    }

    @Test
    @DisplayName("an unregistered device receives no command at all")
    void testSendCommandToUnknownDevice() {
        smartHome.sendCommand(deviceMock, "TURN_ON");

        // With a hand-written double, proving a call did NOT happen means asserting
        // the count is 0. EasyMock does this for you by failing on the spot.
        assertEquals(0, deviceMock.getExecuteCommandCallCount());
        assertNull(deviceMock.getLastCommand());
        assertFalse(smartHome.getDevices().contains(deviceMock));
    }
}
