package ise.testing.s06_parameterized;

/** Table-shaped logic, i.e. the ideal target for parameterized tests. */
public class GradeConverter {

    public static double toGrade(int points) {
        if (points < 0 || points > 100) {
            throw new IllegalArgumentException("Points must be between 0 and 100");
        }
        if (points >= 90) return 1.0;
        if (points >= 80) return 2.0;
        if (points >= 70) return 3.0;
        if (points >= 50) return 4.0;
        return 5.0;
    }

    public static boolean isPassing(int points) {
        return toGrade(points) < 5.0;
    }

    public static String normaliseName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        return name.trim().toUpperCase();
    }
}
