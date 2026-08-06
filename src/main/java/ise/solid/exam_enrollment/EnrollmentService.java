package ise.solid.exam_enrollment;

import java.util.ArrayList;
import java.util.List;

/**
 * TASK 2 -- "The service must evaluate all configured rules before deciding whether
 * to enroll a student. If every rule permits the enrollment, both the student and the
 * course must be updated to reflect it. If any check fails, neither object should be
 * modified. Duplicate enrollment attempts must be detected and rejected without
 * changing any state."
 *
 * TASK 3 -- "New rule types must be usable without modifying the service."
 * TASK 4 -- "Enrollment is only permitted if every rule allows it. The outcome must
 *            not depend on the order in which rules are evaluated."
 *
 * HOW THE SOLID PRINCIPLES SHOW UP IN THIS FILE, concretely:
 *
 *   Dependency Inversion -- the field is List<EnrollmentRule>, the abstraction.
 *     There is no `import` of CapacityRule or PrerequisiteRule anywhere in this class,
 *     and no `instanceof` either. If you find yourself writing
 *         if (rule instanceof PrerequisiteRule) { ... }
 *     you have inverted the dependency back the wrong way and lost the marks.
 *
 *   Open/Closed -- adding a rule means writing a new class and calling addRule().
 *     This file never changes again. Compare with the version this exercise is
 *     rescuing you from, where enroll() would hard-code
 *         if (course.hasFreeSeat() && student.getCompletedCourses().contains(...))
 *     and every new policy would mean editing enroll() -- open to modification.
 *
 *   Single Responsibility -- each rule answers exactly one question; the service only
 *     sequences them and applies the result.
 */
public class EnrollmentService {

    private final List<EnrollmentRule> rules;

    public EnrollmentService() {
        this.rules = new ArrayList<>();
    }

    public EnrollmentService(List<EnrollmentRule> rules) {
        // Defensive copy: the caller must not be able to reach in and mutate the
        // service's rule set afterwards.
        this.rules = new ArrayList<>(rules);
    }

    public void addRule(EnrollmentRule rule) {
        rules.add(rule);
    }

    public List<EnrollmentRule> getRules() {
        return rules;
    }

    /**
     * @return true only if every configured rule permits the enrollment and the
     *         student was not already enrolled.
     */
    public boolean enroll(Student student, Course course) {
        if (student == null || course == null) {
            return false;
        }

        // Duplicate check FIRST, and before any mutation: "duplicate enrollment
        // attempts must be detected and rejected without changing any state".
        if (student.getEnrolledCourses().contains(course)) {
            return false;
        }

        // Every rule is consulted BEFORE anything is written. That ordering is what
        // delivers "if any check fails, neither object should be modified" -- update
        // the student first and you can leave the two objects inconsistent when a
        // later rule says no.
        //
        // Returning early on the first refusal is still order-independent: the rules
        // are pure predicates and logical AND is commutative, so no permutation can
        // change the outcome.
        for (EnrollmentRule rule : rules) {
            if (!rule.canEnroll(student, course)) {
                return false;
            }
        }

        // BOTH sides of the association are updated. Doing only one is the classic
        // half-finished answer: the student thinks they are enrolled, the course
        // still reports a free seat, and CapacityRule then lets the next student in.
        student.enrollIn(course);
        course.addStudent(student);
        return true;
    }
}
