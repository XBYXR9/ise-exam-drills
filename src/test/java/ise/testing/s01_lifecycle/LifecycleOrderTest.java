package ise.testing.s01_lifecycle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

/**
 * The lifecycle, proved rather than asserted in a comment.
 *
 * EVENTS is static so it survives across test instances; every hook appends to it,
 * and afterAll checks the whole sequence. Run with --info to see the printout.
 */
public class LifecycleOrderTest {

    private static final List<String> EVENTS = new ArrayList<>();

    private ThesisTracker tracker;

    @BeforeAll
    static void beforeAll() {
        // MUST be static (unless @TestInstance(PER_CLASS)): it runs before any
        // instance of this class exists.
        EVENTS.add("beforeAll");
        System.out.println("[lifecycle] beforeAll");
    }

    @BeforeEach
    void beforeEach() {
        EVENTS.add("beforeEach");
        System.out.println("[lifecycle] beforeEach");
        tracker = new ThesisTracker();   // fresh SUT for every test
    }

    @AfterEach
    void afterEach() {
        EVENTS.add("afterEach");
        System.out.println("[lifecycle] afterEach");
    }

    @AfterAll
    static void afterAll() {
        EVENTS.add("afterAll");
        System.out.println("[lifecycle] afterAll -> " + EVENTS);

        // beforeAll once, then beforeEach/test/afterEach per test, then afterAll once.
        assertIterableEquals(
                List.of("beforeAll",
                        "beforeEach", "testA", "afterEach",
                        "beforeEach", "testB", "afterEach",
                        "afterAll"),
                EVENTS);
    }

    @Test
    @DisplayName("test A gets a brand-new tracker, so what test B does cannot reach it")
    void testA() {
        EVENTS.add("testA");
        tracker.submit("Thesis A");
        assertEquals(1, tracker.getSubmissionCount());
    }

    @Test
    @DisplayName("test B also sees exactly one submission, proving the instance was recreated")
    void testB() {
        EVENTS.add("testB");
        tracker.submit("Thesis B");
        // If JUnit reused one instance, or if beforeEach were missing, this would be 2.
        // That independence is why you build the SUT in @BeforeEach and not in a field
        // initialiser you then mutate.
        assertEquals(1, tracker.getSubmissionCount());
    }
}
