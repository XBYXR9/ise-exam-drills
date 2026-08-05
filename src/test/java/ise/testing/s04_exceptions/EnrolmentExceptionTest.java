package ise.testing.s04_exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exception assertions, plus the two traps the exam quiz actually tested.
 */
public class EnrolmentExceptionTest {

    protected Enrolment newEnrolment() {
        return new Enrolment("Yahya", 3);
    }

    @Test
    @DisplayName("the SETTER rejects semester 0, and the lambda contains only that one call")
    void setterThrows() {
        Enrolment enrolment = newEnrolment();

        // TRAP 1: the task says the SETTER must throw. Calling the constructor here
        // instead would pass for the wrong reason and was marked wrong in the exam.
        assertThrows(IllegalArgumentException.class, () -> enrolment.setSemester(0));

        // And the state must be unchanged -- an implementation that assigns first and
        // validates afterwards would slip past the assertThrows alone.
        assertEquals(3, enrolment.getSemester());
    }

    @Test
    @DisplayName("assertThrows returns the exception, so the message can be asserted too")
    void messageIsAsserted() {
        Enrolment enrolment = newEnrolment();

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> enrolment.setSemester(-1));

        assertEquals("Semester must be at least 1", thrown.getMessage());
        assertTrue(thrown.getMessage().contains("Semester"));
    }

    @Test
    @DisplayName("the CONSTRUCTOR rejects a blank name -- a separate test from the setter")
    void constructorThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Enrolment("", 1));
        assertThrows(IllegalArgumentException.class, () -> new Enrolment(null, 1));
    }

    @Test
    @DisplayName("assertDoesNotThrow states that the happy path really is exception-free")
    void validInputDoesNotThrow() {
        assertDoesNotThrow(() -> new Enrolment("Yahya", 1));
    }

    @Test
    @DisplayName("WHY try/catch is banned: this test passes no matter what the SUT does")
    void tryCatchMakesATestUseless() {
        Enrolment enrolment = newEnrolment();

        // This is the shape that was offered as a wrong answer in the exam quiz.
        // The catch block swallows the exception, so the test is green when the SUT
        // throws AND green when it does not. It asserts nothing whatsoever.
        try {
            enrolment.setSemester(-10);
        } catch (IllegalArgumentException ignored) {
            // swallowed
        }

        // The only thing rescuing this test is the assertion below -- and if the SUT
        // silently accepted -10, this line is what would catch it. Without it, zero points.
        assertEquals(3, enrolment.getSemester());
    }
}
