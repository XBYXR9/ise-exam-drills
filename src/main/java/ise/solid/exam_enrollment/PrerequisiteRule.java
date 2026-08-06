package ise.solid.exam_enrollment;

/**
 * TASK 1 -- "A course may have another course as a prerequisite. Enrollment should
 * only be permitted if the student has already completed that prerequisite. If a
 * course has no prerequisite, this rule must not block enrollment."
 *
 * The second sentence is a separate branch and a separate test. Returning
 * `student.getCompletedCourses().contains(course.getPrerequisite())` alone looks
 * right and is wrong: with a null prerequisite it returns false and blocks every
 * course that has no prerequisite at all.
 */
public class PrerequisiteRule implements EnrollmentRule {

    @Override
    public boolean canEnroll(Student student, Course course) {
        Course prerequisite = course.getPrerequisite();
        if (prerequisite == null) {
            return true;   // nothing required -> this rule has no opinion
        }
        return student.getCompletedCourses().contains(prerequisite);
    }
}
