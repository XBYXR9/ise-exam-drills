package ise.testing.s03_assertions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * The complete assertion set, each on a real value.
 * assertEquals(EXPECTED, ACTUAL) -- expected first, always. Swapping them does not
 * change pass or fail, but it inverts every failure message you will read under
 * time pressure.
 */
public class AllAssertionsTest {

    protected SeminarRoom newRoom() {
        return new SeminarRoom("MI-01", 4);
    }

    @Test
    @DisplayName("assertEquals and assertNotEquals compare by equals()")
    void equality() {
        SeminarRoom room = newRoom();
        assertEquals("MI-01", room.getCode());
        assertEquals(4, room.getCapacity(), "capacity was read from the wrong field");
        assertNotEquals("MI-02", room.getCode());
    }

    @Test
    @DisplayName("a double comparison without a delta is a bug waiting to happen")
    void floatingPointNeedsADelta() {
        SeminarRoom room = newRoom();
        room.admit("ann");

        // 1/4 happens to be exact, but 1/3 is not: without the delta this style of
        // assertion fails at random depending on the numbers involved.
        assertEquals(0.25, room.occupancyRate(), 0.0001);
    }

    @Test
    @DisplayName("assertTrue and assertFalse both belong in a suite -- the negative one has the teeth")
    void booleans() {
        SeminarRoom room = newRoom();
        assertTrue(room.getAttendees().isEmpty());
        room.admit("ann");
        assertFalse(room.getAttendees().isEmpty());
    }

    @Test
    @DisplayName("assertNull and assertNotNull check presence, never content")
    void nullness() {
        SeminarRoom room = newRoom();
        assertNull(room.getProjectorModel());
        room.installProjector("Epson-X");
        assertNotNull(room.getProjectorModel());
        // assertNotNull alone is a weak assertion: it passes for ANY value. Follow it
        // with the assertEquals that says which value you actually expected.
        assertEquals("Epson-X", room.getProjectorModel());
    }

    @Test
    @DisplayName("assertSame compares references, assertEquals compares values")
    void identityVersusEquality() {
        SeminarRoom room = newRoom();
        assertSame(room.getAttendees(), room.getAttendees());
        assertNotSame(newRoom(), newRoom());
        assertEquals(newRoom().getCode(), newRoom().getCode());
    }

    @Test
    @DisplayName("assertArrayEquals and assertIterableEquals compare contents in order")
    void arraysAndIterables() {
        SeminarRoom room = newRoom();
        room.admit("ann");
        room.admit("bob");

        assertArrayEquals(new String[]{"ann", "bob"}, room.getAttendeeArray());
        // Order matters here, which is exactly what makes it catch a SUT that
        // accidentally prepends instead of appending.
        assertIterableEquals(List.of("ann", "bob"), room.getAttendees());
    }

    @Test
    @DisplayName("assertLinesMatch understands regex lines and the >> >> skip marker")
    void lineMatching() {
        List<String> actual = List.of("start", "step 1", "step 2", "done");
        assertLinesMatch(List.of("start", ">> any number of lines >>", "done"), actual);
    }

    @Test
    @DisplayName("assertInstanceOf checks the runtime type and hands the value back typed")
    void typeAssertion() {
        SeminarRoom room = newRoom();
        String description = assertInstanceOf(String.class, room.describe());
        assertTrue(description.contains("MI-01"));
    }

    @Test
    @DisplayName("fail() marks a branch that must never be reached")
    void unreachableBranch() {
        SeminarRoom room = newRoom();
        if (room.getCapacity() < 0) {
            fail("a seminar room cannot have negative capacity");
        }
        assertTrue(room.getCapacity() > 0);
    }
}
