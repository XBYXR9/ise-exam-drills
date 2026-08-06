package ise.mutants.mockexam;

import ise.acceptance.exam_login.LoginResult;
import ise.acceptance.exam_login.LoginService;
import ise.blackbox.exam_gamelauncher.AccessDecision;
import ise.blackbox.exam_gamelauncher.GameLauncher;
import ise.mocking.exam_smarthome.Device;
import ise.mocking.exam_smarthome.HomeOwner;
import ise.mocking.exam_smarthome.SmartHome;
import ise.solid.exam_enrollment.CapacityRule;
import ise.solid.exam_enrollment.Course;
import ise.solid.exam_enrollment.EnrollmentService;
import ise.solid.exam_enrollment.PrerequisiteRule;
import ise.solid.exam_enrollment.Student;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MUTANTS for the June 2026 mock exam exercises.
 *
 * Same contract as the other mutant files: each class carries the ORIGINAL assertions
 * pointed at a broken SUT, is @Tag("mutant") so the normal suite skips it, and is
 * executed by MutationDrillTest which asserts it goes red.
 */
final class MockExamMutants {

    private MockExamMutants() {
    }
}

// ===========================================================================
//  EXERCISE 3 -- SmartHome (mocking, 25 pts)
// ===========================================================================

/** MUTATION: the collaborator call is skipped -- the device is never registered. */
class SmartHomeThatNeverRegisters extends SmartHome {

    SmartHomeThatNeverRegisters(HomeOwner owner) {
        super(owner);
    }

    @Override
    public boolean addDevice(Device device) {
        if (device == null || getDevices().contains(device)) {
            return false;
        }
        getDevices().add(device);   // device.register(owner) is gone
        return true;
    }
}

@Tag("mutant")
class SmartHomeTest_NeverRegisters {

    @Test
    void testAddDevice() {
        HomeOwner owner = new HomeOwner("Yahya");
        SmartHome smartHome = new SmartHomeThatNeverRegisters(owner);
        Device deviceMock = createMock(Device.class);

        deviceMock.register(owner);
        expectLastCall();
        replay(deviceMock);

        boolean added = smartHome.addDevice(deviceMock);

        // CAUGHT BY verify() alone. The list is updated and true is returned, so both
        // the state assertion and the return-value assertion stay green.
        verify(deviceMock);
        assertEquals(1, smartHome.getDevices().size());
        assertTrue(added);
    }
}

/** MUTATION: the duplicate check is dropped, so the same device is added twice. */
class SmartHomeThatAllowsDuplicates extends SmartHome {

    SmartHomeThatAllowsDuplicates(HomeOwner owner) {
        super(owner);
    }

    @Override
    public boolean addDevice(Device device) {
        device.register(getOwner());
        getDevices().add(device);
        return true;
    }
}

@Tag("mutant")
class SmartHomeTest_AllowsDuplicates {

    @Test
    void testAddDeviceTwiceIsRefused() {
        HomeOwner owner = new HomeOwner("Yahya");
        SmartHome smartHome = new SmartHomeThatAllowsDuplicates(owner);
        Device deviceMock = createMock(Device.class);

        deviceMock.register(owner);
        expectLastCall().once();   // exactly once
        replay(deviceMock);

        assertTrue(smartHome.addDevice(deviceMock));
        boolean addedAgain = smartHome.addDevice(deviceMock);

        // CAUGHT three ways: register() runs twice (unexpected call), the size is 2,
        // and the second call returns true.
        verify(deviceMock);
        assertFalse(addedAgain);
        assertEquals(1, smartHome.getDevices().size());
    }
}

/** MUTATION: removeDevice reports success without removing anything. */
class SmartHomeThatNeverRemoves extends SmartHome {

    SmartHomeThatNeverRemoves(HomeOwner owner) {
        super(owner);
    }

    @Override
    public boolean removeDevice(Device device) {
        return true;   // the list is untouched
    }
}

@Tag("mutant")
class SmartHomeTest_NeverRemoves {

    @Test
    void testRemoveDevice() {
        HomeOwner owner = new HomeOwner("Yahya");
        SmartHome smartHome = new SmartHomeThatNeverRemoves(owner);
        Device deviceMock = createMock(Device.class);

        deviceMock.register(owner);
        expectLastCall();
        replay(deviceMock);

        smartHome.addDevice(deviceMock);
        boolean removed = smartHome.removeDevice(deviceMock);

        verify(deviceMock);
        assertTrue(removed);
        // CAUGHT BY the state assertions. The return value is true in both versions,
        // which is exactly why "attributes updated correctly" is in the task text.
        assertEquals(0, smartHome.getDevices().size());
        assertFalse(smartHome.getDevices().contains(deviceMock));
    }
}

/** MUTATION: sendCommand does nothing at all. */
class SmartHomeThatSwallowsCommands extends SmartHome {

    SmartHomeThatSwallowsCommands(HomeOwner owner) {
        super(owner);
    }

    @Override
    public void sendCommand(Device device, String command) {
        // body deleted
    }
}

@Tag("mutant")
class SmartHomeTest_SwallowsCommands {

    @Test
    void testSendCommand() {
        HomeOwner owner = new HomeOwner("Yahya");
        SmartHome smartHome = new SmartHomeThatSwallowsCommands(owner);
        Device deviceMock = createMock(Device.class);

        deviceMock.register(owner);
        expectLastCall();
        deviceMock.executeCommand("TURN_ON");
        expectLastCall();
        replay(deviceMock);

        smartHome.addDevice(deviceMock);
        smartHome.sendCommand(deviceMock, "TURN_ON");

        // CAUGHT BY verify() and nothing else: sendCommand returns void and changes
        // no SmartHome state, so there is no other observable difference.
        verify(deviceMock);
    }
}

/** MUTATION: the registration guard is dropped -- unknown devices get commanded. */
class SmartHomeThatCommandsAnything extends SmartHome {

    SmartHomeThatCommandsAnything(HomeOwner owner) {
        super(owner);
    }

    @Override
    public void sendCommand(Device device, String command) {
        device.executeCommand(command);   // no membership check
    }
}

@Tag("mutant")
class SmartHomeTest_CommandsAnything {

    @Test
    void testSendCommandToUnknownDevice() {
        SmartHome smartHome = new SmartHomeThatCommandsAnything(new HomeOwner("Yahya"));
        Device deviceMock = createMock(Device.class);

        // executeCommand deliberately NOT recorded.
        replay(deviceMock);

        // CAUGHT BY the unrecorded default mock: "Unexpected method call". This is the
        // must-not-be-called pattern again, in the new exam.
        smartHome.sendCommand(deviceMock, "TURN_ON");

        verify(deviceMock);
    }
}

// ===========================================================================
//  EXERCISE 5 -- Course enrollment (SOLID, 25 pts)
// ===========================================================================

/** MUTATION: the classic one-liner -- a course with no prerequisite is blocked. */
class PrerequisiteRuleThatBlocksEverything extends PrerequisiteRule {

    @Override
    public boolean canEnroll(Student student, Course course) {
        // contains(null) is false, so every ordinary course is refused.
        return student.getCompletedCourses().contains(course.getPrerequisite());
    }
}

@Tag("mutant")
class PrerequisiteRuleTest_BlocksEverything {

    @Test
    void noPrerequisiteDoesNotBlock() {
        Student student = new Student("Yahya");
        Course maths = new Course("Maths", 2);

        // CAUGHT BY the no-prerequisite branch. The "completed" and "missing"
        // prerequisite tests both still pass, so this one test is carrying the task.
        assertTrue(new PrerequisiteRuleThatBlocksEverything().canEnroll(student, maths));
    }
}

/** MUTATION: only one side of the association is updated. */
class EnrollmentServiceThatForgetsTheCourse extends EnrollmentService {

    EnrollmentServiceThatForgetsTheCourse(List<ise.solid.exam_enrollment.EnrollmentRule> rules) {
        super(rules);
    }

    @Override
    public boolean enroll(Student student, Course course) {
        if (student.getEnrolledCourses().contains(course)) {
            return false;
        }
        for (ise.solid.exam_enrollment.EnrollmentRule rule : getRules()) {
            if (!rule.canEnroll(student, course)) {
                return false;
            }
        }
        student.enrollIn(course);
        // course.addStudent(student) is gone
        return true;
    }
}

@Tag("mutant")
class EnrollmentServiceTest_ForgetsTheCourse {

    @Test
    void successUpdatesBothSides() {
        Student student = new Student("Yahya");
        Course maths = new Course("Maths", 2);
        EnrollmentService service = new EnrollmentServiceThatForgetsTheCourse(
                List.of(new CapacityRule(), new PrerequisiteRule()));

        assertTrue(service.enroll(student, maths));

        assertTrue(student.getEnrolledCourses().contains(maths));
        // CAUGHT BY the course-side assertions. Checking only the student passes, and
        // the course then still reports a free seat it has already given away.
        assertTrue(maths.getEnrolledStudents().contains(student));
        assertEquals(1, maths.getEnrolledStudents().size());
    }
}

/** MUTATION: the student is enrolled BEFORE the rules are evaluated. */
class EnrollmentServiceThatMutatesTooEarly extends EnrollmentService {

    EnrollmentServiceThatMutatesTooEarly(List<ise.solid.exam_enrollment.EnrollmentRule> rules) {
        super(rules);
    }

    @Override
    public boolean enroll(Student student, Course course) {
        student.enrollIn(course);          // mutate first ...
        course.addStudent(student);
        for (ise.solid.exam_enrollment.EnrollmentRule rule : getRules()) {
            if (!rule.canEnroll(student, course)) {
                return false;              // ... and never roll back
            }
        }
        return true;
    }
}

@Tag("mutant")
class EnrollmentServiceTest_MutatesTooEarly {

    @Test
    void failureModifiesNothing() {
        Student student = new Student("Yahya");
        Course maths = new Course("Maths", 2);
        Course advanced = new Course("Advanced Maths", 2, maths);
        EnrollmentService service = new EnrollmentServiceThatMutatesTooEarly(
                List.of(new PrerequisiteRule()));

        assertFalse(service.enroll(student, advanced));

        // CAUGHT BY the state assertions after the refusal. The return value is false
        // in both versions, so assertFalse on its own proves nothing.
        assertEquals(0, student.getEnrolledCourses().size());
        assertEquals(0, advanced.getEnrolledStudents().size());
    }
}

/** MUTATION: the duplicate check is missing. */
class EnrollmentServiceWithoutDuplicateCheck extends EnrollmentService {

    EnrollmentServiceWithoutDuplicateCheck(List<ise.solid.exam_enrollment.EnrollmentRule> rules) {
        super(rules);
    }

    @Override
    public boolean enroll(Student student, Course course) {
        for (ise.solid.exam_enrollment.EnrollmentRule rule : getRules()) {
            if (!rule.canEnroll(student, course)) {
                return false;
            }
        }
        student.enrollIn(course);
        course.addStudent(student);
        return true;
    }
}

@Tag("mutant")
class EnrollmentServiceTest_NoDuplicateCheck {

    @Test
    void duplicateIsRejected() {
        Student student = new Student("Yahya");
        Course maths = new Course("Maths", 2);   // capacity 2, so CapacityRule permits both
        EnrollmentService service = new EnrollmentServiceWithoutDuplicateCheck(
                List.of(new CapacityRule()));

        assertTrue(service.enroll(student, maths));
        assertFalse(service.enroll(student, maths));

        assertEquals(1, student.getEnrolledCourses().size());
        assertEquals(1, maths.getEnrolledStudents().size());
    }
}

// ===========================================================================
//  EXERCISE 4 -- Game launcher (black-box, 10 pts)
// ===========================================================================

/** MUTATION: off-by-one at the VIP lower bracket limit (>= becomes >). */
class GameLauncherWithOffByOneVipLimit extends GameLauncher {

    @Override
    public AccessDecision checkBetaAccess(String accountType, int characterLevel) {
        if (characterLevel < MIN_LEVEL || characterLevel > MAX_LEVEL) {
            return AccessDecision.INVALID;
        }
        if ("standard".equals(accountType)) {
            return (characterLevel >= STANDARD_MIN && characterLevel <= STANDARD_MAX)
                    ? AccessDecision.ACCEPTED : AccessDecision.REJECTED;
        }
        if ("vip".equals(accountType)) {
            return (characterLevel > VIP_MIN && characterLevel <= VIP_MAX)   // mutated
                    ? AccessDecision.ACCEPTED : AccessDecision.REJECTED;
        }
        return AccessDecision.INVALID;
    }
}

@Tag("mutant")
class GameLauncherTest_OffByOneVipLimit {

    @Test
    void vipLowerLimitBoundary() {
        GameLauncher launcher = new GameLauncherWithOffByOneVipLimit();

        // TC10 and TC12 both still pass against this mutant.
        assertEquals(AccessDecision.REJECTED, launcher.checkBetaAccess("vip", 9));
        assertEquals(AccessDecision.ACCEPTED, launcher.checkBetaAccess("vip", 11));
        // TC11 -- the value exactly ON the boundary -- is the only one that catches it.
        assertEquals(AccessDecision.ACCEPTED, launcher.checkBetaAccess("vip", 10));
    }
}

// ===========================================================================
//  EXERCISE 1 -- Login (acceptance tests, 5 pts)
// ===========================================================================

/** MUTATION: an unknown username is let in. */
class LoginServiceThatTrustsEveryone extends LoginService {

    @Override
    public LoginResult login(String username, String password) {
        return LoginResult.granted();
    }
}

@Tag("mutant")
class LoginServiceTest_TrustsEveryone {

    @Test
    void invalidCredentialsAreRefused() {
        LoginService loginService = new LoginServiceThatTrustsEveryone();
        loginService.register("student", "password123");

        LoginResult result = loginService.login("student", "wrongpassword");

        // CAUGHT BY the Then steps of the second scenario. A scenario that only checked
        // the happy path would never notice.
        assertFalse(result.isAccessGranted());
        assertTrue(result.hasErrorMessage());
        assertTrue(result.isOnLoginPage());
    }
}
