package ise.solid.exam_enrollment;

/**
 * THE ABSTRACTION that makes tasks 3 and 4 possible.
 *
 * Dependency Inversion: EnrollmentService depends on this interface, never on
 * CapacityRule or PrerequisiteRule. High-level policy does not know its details.
 *
 * Open/Closed: a new rule is a new class implementing this interface. The service
 * is open to that extension and closed to modification -- you never touch it again.
 */
public interface EnrollmentRule {

    boolean canEnroll(Student student, Course course);
}
