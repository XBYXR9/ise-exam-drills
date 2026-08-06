package ise.solid.exam_enrollment;

import java.util.ArrayList;
import java.util.List;

/** Given by the template. A course may point at exactly one prerequisite course. */
public class Course {

    private final String title;
    private final int capacity;
    private final Course prerequisite;
    private final List<Student> enrolledStudents = new ArrayList<>();

    public Course(String title, int capacity) {
        this(title, capacity, null);
    }

    public Course(String title, int capacity, Course prerequisite) {
        this.title = title;
        this.capacity = capacity;
        this.prerequisite = prerequisite;
    }

    public String getTitle() {
        return title;
    }

    public int getCapacity() {
        return capacity;
    }

    public Course getPrerequisite() {
        return prerequisite;
    }

    public boolean hasFreeSeat() {
        return enrolledStudents.size() < capacity;
    }

    public void addStudent(Student student) {
        enrolledStudents.add(student);
    }

    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }
}
