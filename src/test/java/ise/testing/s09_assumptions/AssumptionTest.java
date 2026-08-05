package ise.testing.s09_assumptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * An assumption SKIPS the test when a precondition is absent; an assertion FAILS it.
 * Use assumptions for facts about the environment you do not control, never to paper
 * over a SUT you have not made deterministic -- a permanently skipped test is worth
 * exactly as much as a test that cannot fail.
 */
public class AssumptionTest {

    protected PlatformProbe newProbe() {
        return new PlatformProbe();
    }

    @Test
    @DisplayName("on Windows the path separator is a backslash -- skipped elsewhere")
    void windowsPathSeparator() {
        PlatformProbe probe = newProbe();

        assumeTrue(probe.isWindows(), "not running on Windows");

        assertEquals("\\", probe.pathSeparator());
    }

    @Test
    @DisplayName("on anything but Windows the separator is a forward slash -- skipped on Windows")
    void posixPathSeparator() {
        PlatformProbe probe = newProbe();

        assumeFalse(probe.isWindows(), "running on Windows");

        assertEquals("/", probe.pathSeparator());
    }

    @Test
    @DisplayName("a separator exists on every platform, so this one always runs")
    void separatorAlwaysExists() {
        assertNotNull(newProbe().pathSeparator());
    }
}
