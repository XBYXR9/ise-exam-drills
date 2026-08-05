package ise.testing.s06_parameterized;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * One test body, many inputs. This is the natural home for equivalence classes and
 * boundary values: each case is reported separately, so a single failing boundary
 * is named in the report instead of hiding inside a loop.
 */
public class GradeConverterParameterizedTest {

    /*
     * GradeConverter exposes static methods, which cannot be overridden. These three
     * thin hooks give the mutation drill a seam: a broken subclass overrides them to
     * return wrong answers, and the assertions below stay exactly as written.
     */
    protected double toGrade(int points) {
        return GradeConverter.toGrade(points);
    }

    protected boolean isPassing(int points) {
        return GradeConverter.isPassing(points);
    }

    protected String normaliseName(String name) {
        return GradeConverter.normaliseName(name);
    }

    @ParameterizedTest
    @ValueSource(ints = {90, 95, 100})
    @DisplayName("every point count from 90 upwards is a 1.0")
    void topGradeRange(int points) {
        assertEquals(1.0, toGrade(points), 0.0001);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 101, 1000})
    @DisplayName("points outside 0..100 are rejected")
    void invalidPointsAreRejected(int points) {
        assertThrows(IllegalArgumentException.class, () -> toGrade(points));
    }

    @ParameterizedTest
    @NullAndEmptySource                       // contributes null and ""
    @ValueSource(strings = {"   ", "\t"})     // plus the blank-but-not-empty cases
    @DisplayName("null, empty and blank names are all rejected")
    void blankNamesAreRejected(String name) {
        assertThrows(IllegalArgumentException.class, () -> normaliseName(name));
    }

    @ParameterizedTest(name = "{0} points -> grade {1}")
    @CsvSource({
            "100, 1.0",
            " 90, 1.0",     // boundary: first 1.0
            " 89, 2.0",     // boundary: last 2.0
            " 80, 2.0",
            " 70, 3.0",
            " 50, 4.0",
            " 49, 5.0",     // boundary: first fail
            "  0, 5.0"
    })
    @DisplayName("the whole grade table, including both sides of every boundary")
    void gradeTable(int points, double expected) {
        // Every limit L in the SUT appears here as L-1 and L. An off-by-one such as
        // >= 90 becoming > 90 breaks exactly one of these rows, and the custom name
        // pattern tells you which one without opening the stack trace.
        assertEquals(expected, toGrade(points), 0.0001);
    }

    @ParameterizedTest
    @MethodSource("passFailCases")
    @DisplayName("isPassing agrees with the grade table on both sides of the pass mark")
    void passFail(int points, boolean expectedPassing) {
        assertEquals(expectedPassing, isPassing(points));
    }

    static Stream<Arguments> passFailCases() {
        // Use @MethodSource when the arguments are not literals: objects, collections,
        // or values you want to compute.
        return Stream.of(
                Arguments.of(50, true),
                Arguments.of(49, false),
                Arguments.of(100, true),
                Arguments.of(0, false));
    }

    @ParameterizedTest
    @EnumSource(Semester.class)
    @DisplayName("every enum constant has a usable name")
    void everySemesterHasAName(Semester semester) {
        assertNotNull(semester.name());
        assertFalse(semester.name().isBlank());
        assertTrue(semester == Semester.WINTER || semester == Semester.SUMMER);
    }
}
