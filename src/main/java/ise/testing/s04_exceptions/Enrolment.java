package ise.testing.s04_exceptions;

/**
 * Has BOTH a throwing constructor and a throwing setter, because the exam quiz
 * punished exactly that confusion: the task said the SETTER must throw, and the
 * answer that called the constructor was marked wrong.
 */
public class Enrolment {

    private String studentName;
    private int semester;

    public Enrolment(String studentName, int semester) {
        if (studentName == null || studentName.isBlank()) {
            throw new IllegalArgumentException("Student name must not be blank");
        }
        setSemester(semester);
        this.studentName = studentName;
    }

    public void setSemester(int semester) {
        if (semester < 1) {
            throw new IllegalArgumentException("Semester must be at least 1");
        }
        this.semester = semester;
    }

    public int getSemester() {
        return semester;
    }

    public String getStudentName() {
        return studentName;
    }
}
