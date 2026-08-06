package ise.solid.exam_enrollment;

import java.util.ArrayList;
import java.util.List;

/** Mock exam (Jun 2026), exercise 5. Given by the template -- do not change the API. */
public class Student {

    private final String name;
    private final List<Course> completedCourses = new ArrayList<>();
    private final List<Course> enrolledCourses = new ArrayList<>();

    public Student(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void completeCourse(Course course) {
        completedCourses.add(course);
    }

    public void enrollIn(Course course) {
        enrolledCourses.add(course);
    }

    public List<Course> getCompletedCourses() {
        return completedCourses;
    }

    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }
}
