package ise.testing.s07_extras;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The annotations that shape HOW a test runs rather than what it asserts. */
public class TestAnnotationExtrasTest {

    protected TokenGenerator newGenerator() {
        return new TokenGenerator();
    }

    @RepeatedTest(20)
    @DisplayName("a randomised token is positive in every one of 20 runs")
    void tokenIsAlwaysPositive(RepetitionInfo info) {
        // Repetition is the right tool for non-deterministic code: a single run of a
        // random generator proves almost nothing.
        assertTrue(newGenerator().nextToken() > 0,
                "failed on repetition " + info.getCurrentRepetition());
    }

    @Test
    @Timeout(2)   // seconds; the test FAILS if it takes longer
    @DisplayName("factorial finishes well inside two seconds")
    void finishesQuickly() {
        assertEquals(3628800L, newGenerator().factorial(10));
    }

    @Test
    @DisplayName("assertTimeout puts the limit on one statement instead of the whole method")
    void assertTimeoutOnOneCall() {
        long result = assertTimeout(Duration.ofMillis(500), () -> newGenerator().factorial(10));
        // Always assert the RESULT too. A timeout assertion on its own only proves the
        // code was fast, not that it was correct.
        assertEquals(3628800L, result);
    }

    @Test
    @Disabled("kept as a template: @Disabled always needs a reason, and the reason shows up in the report")
    @DisplayName("this is what a parked test looks like")
    void parkedTest() {
        throw new AssertionError("never executed while @Disabled is present");
    }

    @Test
    @Tag("slow")
    @DisplayName("tagged tests can be included or excluded from a run by tag")
    void taggedTest() {
        // This project uses the same mechanism to keep the mutation tests out of the
        // normal suite: build.gradle says useJUnitPlatform { excludeTags 'mutant' }.
        assertTrue(newGenerator().factorial(0) == 1L);
    }
}
