package ise.solid.exam_enrollment;

/** Already implemented in the exam template. Kept exactly as given. */
public class CapacityRule implements EnrollmentRule {

    @Override
    public boolean canEnroll(Student student, Course course) {
        return course.hasFreeSeat();
    }
}
