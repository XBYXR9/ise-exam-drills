package ise.testing.s01_lifecycle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The same fact from the other direction: JUnit 5 constructs a NEW instance of the
 * test class for every @Test method. Fields are therefore automatically fresh, and
 * tests cannot leak state into each other.
 */
public class FreshInstancePerTestTest {

    private static final Set<Integer> SEEN_INSTANCES = new HashSet<>();

    private int counter = 0;   // plain field, never reset by any hook

    @Test
    @DisplayName("the counter is 1 here even though the other test also increments it")
    void firstTest() {
        SEEN_INSTANCES.add(System.identityHashCode(this));
        counter++;
        assertEquals(1, counter);
    }

    @Test
    @DisplayName("and 1 here too, because this is a different object entirely")
    void secondTest() {
        SEEN_INSTANCES.add(System.identityHashCode(this));
        counter++;
        assertEquals(1, counter);
        assertEquals(2, SEEN_INSTANCES.size(), "JUnit should have built two instances");
    }
}
