package ise.practice.testing;

/**
 * PRACTICE DRILL -- Mock exam (Jun 2026), exercise 5. SOLID principles, 25 POINTS.
 *
 * This exercise is INVERTED compared with every other drill in this project: the
 * tests are given and YOU write the source. So the practice version is not a gutted
 * test class -- it is the production code.
 *
 * HOW TO RUN THIS DRILL
 *   1. open  src\main\java\ise\solid\exam_enrollment\PrerequisiteRule.java
 *            src\main\java\ise\solid\exam_enrollment\EnrollmentService.java
 *   2. delete the bodies of  PrerequisiteRule.canEnroll  and  EnrollmentService.enroll
 *      (keep the signatures -- "do not change public method names or constructor
 *      signatures")
 *   3. run:  gradlew.bat test --tests "ise.solid.*"
 *      and make all four @Nested task blocks go green again
 *   4. diff against git:  git diff src/main/java/ise/solid
 *
 * THE FOUR TASKS, in exam wording
 *   1. Complete PrerequisiteRule. A course may have another course as a prerequisite.
 *      Enrollment should only be permitted if the student has already completed that
 *      prerequisite. If a course has NO prerequisite, this rule must not block
 *      enrollment.
 *   2. Complete EnrollmentService. The service must evaluate all configured rules
 *      before deciding. If every rule permits, BOTH the student and the course must be
 *      updated. If any check fails, NEITHER object should be modified. Duplicate
 *      enrollment attempts must be detected and rejected without changing any state.
 *   3. The enrollment system must support any rule that conforms to the EnrollmentRule
 *      abstraction. New rule types must be usable without modifying the service.
 *   4. When multiple rules are configured, enrollment is only permitted if EVERY rule
 *      allows it, and the outcome must not depend on the order of evaluation.
 *
 * THE FOUR TRAPS, in the order people fall into them
 *   - `return student.getCompletedCourses().contains(course.getPrerequisite())`
 *     looks complete and blocks every course that has no prerequisite at all.
 *   - Updating the student but forgetting `course.addStudent(student)`. The course
 *     then still reports a free seat it has already given away.
 *   - Mutating before all rules have been consulted, so a late refusal leaves the two
 *     objects inconsistent.
 *   - `if (rule instanceof PrerequisiteRule)` anywhere in the service. That is the
 *     Dependency Inversion Principle inverted back the wrong way, and it fails task 3.
 */
public final class EnrollmentPracticeNotes {

    private EnrollmentPracticeNotes() {
    }
}
