package ise.practice.mocking;

import ise.mocking.exam_smarthome.Device;
import ise.mocking.exam_smarthome.HomeOwner;
import ise.mocking.exam_smarthome.SmartHome;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * PRACTICE DRILL -- Mock exam (Jun 2026), exercise 3. Mocking, 25 POINTS.
 * The single biggest exercise on the paper.
 *
 * Delete @Disabled, fill in the TODOs, then diff against
 *     ise.mocking.exam_smarthome.SmartHomeTest
 *
 * =====================================================================
 * PROBLEM STATEMENT (exam wording)
 *
 * Your company is developing a smart home management system. Home owners can
 * register smart devices, remove them from the system, and send commands to them.
 * Some classes have not yet been fully implemented, but the team wants to write unit
 * tests to verify that the current version of the system behaves correctly.
 *
 *   SmartHome (SUT)  +addDevice(Device): boolean
 *                    +removeDevice(Device): boolean
 *                    +sendCommand(Device, String): void
 *                    owner -> HomeOwner,  devices -> Device *
 *   Device (interface)  +register(HomeOwner): void
 *                       +executeCommand(String): void
 *
 * You can find the test cases in the test folder. Do not change the code in main.
 * =====================================================================
 */
@Disabled("PRACTICE DRILL: delete this line, then fill in the TODOs below")
class SmartHomePracticeTest {

    // TODO Task 1 -- "Setup the SmartHomeTest class. Add the test subject and mock
    //   attributes and annotate them."
    //   Same three ingredients as always: the class-level extension, an instantiated
    //   @TestSubject field, and a @Mock field. Note SmartHome has no no-arg
    //   constructor -- it needs a HomeOwner.

    @Test
    void testAddDevice() {
        // TODO Task 2 -- "Make sure the device object is called correctly. Make sure
        //   that the attributes of the SmartHome class are updated correctly and that
        //   the method you are testing returns the correct value."
        //
        //   Three things in one sentence, so three kinds of assertion.
        //   Careful: register(HomeOwner) returns void. There is no expect(...) here.
        //   Which EasyMock call records a void method?
        //   And which HomeOwner should you expect it to be handed?
    }

    @Test
    void testRemoveDevice() {
        // TODO Task 3 -- "Make sure the attributes of the SmartHome class are updated
        //   correctly and that the method you are testing returns the correct value."
        //
        //   Note what this task does NOT say: nothing about the device being called.
        //   So what should the mock expect during removeDevice, and what happens if
        //   the implementation touches the device anyway?
    }

    @Test
    void testSendCommand() {
        // TODO Task 4 -- "Make sure the device object is called correctly and receives
        //   the expected command."
        //
        //   sendCommand returns void AND changes no SmartHome state. So exactly one
        //   thing in this test can fail on a broken implementation -- make sure you
        //   write it. And record the literal command string, not anyString().
    }
}
