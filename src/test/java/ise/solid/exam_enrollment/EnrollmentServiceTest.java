package ise.solid.exam_enrollment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MOCK EXAM (Jun 2026), EXERCISE 5 -- SOLID principles, 25 points.
 *
 * The exercise is inverted compared with the mocking and testing tasks: here the
 * TESTS are the grader and YOU write the source. These are the tests your
 * implementation has to satisfy, one @Nested block per task.
 */
class EnrollmentServiceTest {

    private Student student;
    private Course maths;
    private Course advancedMaths;

    @BeforeEach
    void setUp() {
        student = new Student("Yahya");
        maths = new Course("Maths", 2);
        advancedMaths = new Course("Advanced Maths", 2, maths);   // prerequisite: maths
    }

    @Nested
    @DisplayName("Task 1 -- PrerequisiteRule")
    class Task1 {

        private final PrerequisiteRule rule = new PrerequisiteRule();

        @Test
        @DisplayName("a completed prerequisite permits enrollment")
        void completedPrerequisitePermits() {
            student.completeCourse(maths);
            assertTrue(rule.canEnroll(student, advancedMaths));
        }

        @Test
        @DisplayName("a missing prerequisite blocks enrollment")
        void missingPrerequisiteBlocks() {
            assertFalse(rule.canEnroll(student, advancedMaths));
        }

        @Test
        @DisplayName("a course with NO prerequisite is never blocked by this rule")
        void noPrerequisiteDoesNotBlock() {
            // The branch the one-liner implementation gets wrong: with a null
            // prerequisite, contains(null) is false and every ordinary course is blocked.
            assertTrue(rule.canEnroll(student, maths));
        }

        @Test
        @DisplayName("completing an unrelated course does not satisfy the prerequisite")
        void unrelatedCourseDoesNotSatisfy() {
            student.completeCourse(new Course("History", 5));
            assertFalse(rule.canEnroll(student, advancedMaths));
        }
    }

    @Nested
    @DisplayName("Task 2 -- the enrollment workflow")
    class Task2 {

        @Test
        @DisplayName("when every rule permits, BOTH the student and the course are updated")
        void successUpdatesBothSides() {
            EnrollmentService service = new EnrollmentService(
                    List.of(new CapacityRule(), new PrerequisiteRule()));

            assertTrue(service.enroll(student, maths));

            // Both sides, every time. Asserting only the student passes against an
            // implementation that forgets course.addStudent(), and then the course
            // still reports a free seat it no longer has.
            assertTrue(student.getEnrolledCourses().contains(maths));
            assertEquals(1, student.getEnrolledCourses().size());
            assertTrue(maths.getEnrolledStudents().contains(student));
            assertEquals(1, maths.getEnrolledStudents().size());
        }

        @Test
        @DisplayName("when a rule refuses, NEITHER object is modified")
        void failureModifiesNothing() {
            EnrollmentService service = new EnrollmentService(List.of(new PrerequisiteRule()));

            // Prerequisite not completed, so the rule refuses.
            assertFalse(service.enroll(student, advancedMaths));

            // This pair of assertions is what catches an implementation that enrolls
            // first and validates afterwards.
            assertEquals(0, student.getEnrolledCourses().size());
            assertEquals(0, advancedMaths.getEnrolledStudents().size());
        }

        @Test
        @DisplayName("a full course refuses the next student and leaves both sides untouched")
        void capacityIsEnforced() {
            Course seminar = new Course("Seminar", 1);
            EnrollmentService service = new EnrollmentService(List.of(new CapacityRule()));
            Student first = new Student("Ann");

            assertTrue(service.enroll(first, seminar));
            assertFalse(service.enroll(student, seminar));

            assertEquals(1, seminar.getEnrolledStudents().size());
            assertEquals(0, student.getEnrolledCourses().size());
        }

        @Test
        @DisplayName("a duplicate enrollment is rejected without changing any state")
        void duplicateIsRejected() {
            EnrollmentService service = new EnrollmentService(List.of(new CapacityRule()));

            assertTrue(service.enroll(student, maths));
            assertFalse(service.enroll(student, maths));

            // Exactly one on each side. A missing duplicate check gives 2 and 2 here
            // while both return values still look plausible.
            assertEquals(1, student.getEnrolledCourses().size());
            assertEquals(1, maths.getEnrolledStudents().size());
        }

        @Test
        @DisplayName("a service with no rules at all still enrolls")
        void noRulesMeansNoObjections() {
            assertTrue(new EnrollmentService().enroll(student, maths));
            assertEquals(1, student.getEnrolledCourses().size());
        }
    }

    @Nested
    @DisplayName("Task 3 -- the design stays extensible (Open/Closed + Dependency Inversion)")
    class Task3 {

        /**
         * A rule type that did not exist when EnrollmentService was written. If the
         * service is closed to modification and depends only on the abstraction, this
         * class works without a single character changing in EnrollmentService.
         */
        static class MaxCoursesRule implements EnrollmentRule {

            private final int maxCourses;

            MaxCoursesRule(int maxCourses) {
                this.maxCourses = maxCourses;
            }

            @Override
            public boolean canEnroll(Student student, Course course) {
                return student.getEnrolledCourses().size() < maxCourses;
            }
        }

        @Test
        @DisplayName("a brand-new rule type is honoured without touching the service")
        void newRuleTypeIsHonoured() {
            EnrollmentService service = new EnrollmentService();
            service.addRule(new MaxCoursesRule(1));

            assertTrue(service.enroll(student, maths));
            // The second enrollment is refused by a rule the service has never heard of.
            assertFalse(service.enroll(student, new Course("Physics", 10)));
            assertEquals(1, student.getEnrolledCourses().size());
        }

        @Test
        @DisplayName("addRule and the constructor both feed the same rule list")
        void bothConfigurationRoutesWork() {
            EnrollmentService service = new EnrollmentService(List.of(new CapacityRule()));
            service.addRule(new PrerequisiteRule());

            assertEquals(2, service.getRules().size());
            assertFalse(service.enroll(student, advancedMaths));   // prerequisite missing
        }
    }

    @Nested
    @DisplayName("Task 4 -- rules combine with AND, independent of order")
    class Task4 {

        @Test
        @DisplayName("all rules must permit: one refusal is enough to block")
        void allRulesMustPermit() {
            EnrollmentService service = new EnrollmentService(
                    List.of(new CapacityRule(), new PrerequisiteRule()));

            // Capacity is fine, prerequisite is not -> blocked.
            assertFalse(service.enroll(student, advancedMaths));
        }

        @Test
        @DisplayName("swapping the rule order does not change the outcome")
        void outcomeIsOrderIndependent() {
            Student a = new Student("A");
            Student b = new Student("B");
            Course full = new Course("Full", 0, maths);

            EnrollmentService capacityFirst = new EnrollmentService(
                    List.of(new CapacityRule(), new PrerequisiteRule()));
            EnrollmentService prerequisiteFirst = new EnrollmentService(
                    List.of(new PrerequisiteRule(), new CapacityRule()));

            // Both rules refuse, in both orders, with identical results and identical
            // (unchanged) state. This is the test that a short-circuit with a
            // side-effecting rule would fail.
            assertEquals(capacityFirst.enroll(a, full), prerequisiteFirst.enroll(b, full));
            assertFalse(capacityFirst.enroll(a, full));
            assertEquals(0, full.getEnrolledStudents().size());
        }

        @Test
        @DisplayName("both rules satisfied means the enrollment goes through")
        void bothSatisfiedEnrolls() {
            student.completeCourse(maths);
            EnrollmentService service = new EnrollmentService(
                    List.of(new PrerequisiteRule(), new CapacityRule()));

            assertTrue(service.enroll(student, advancedMaths));
            assertEquals(1, advancedMaths.getEnrolledStudents().size());
        }
    }
}
