package ise.testing.exam_cloudserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ISE HN 2026 exam, exercise 1, tasks 2-6 -- the five tests.
 *
 * WHAT WAS WRONG IN THE STARTER TESTS (every one of these loses marks)
 *   1. `allocationResult` was never assigned. The call was `cloudServer.allocateTask(...)`
 *      with the return value thrown away, so `allocationResult.isSuccess()` was a
 *      NullPointerException. Always write  `AllocationResult result = server.allocateTask(...)`.
 *   2. assertEquals(actual, expected) -- arguments were backwards. JUnit's order is
 *      (expected, actual); backwards only matters for the failure message, but it makes the
 *      message lie to you.
 *   3. The speed expectation was `(2*80)/(5+10)+40`: both operands are int, so Java does
 *      INTEGER division (160/15 = 10) and the expected value is 50 instead of 50.666...
 *   4. The speed was compared against getActualRamCost() -- two different fields.
 *   5. The RAM expectation was `5 * 0.6`; that is the COST, not what is left. The server
 *      started with 50, so what is left is 50 - 3.0 = 47.0.
 *   6. testFreeRamNormal expected `10 * 1.3 = 13`, forgetting that the server already held
 *      10 -- the new level is 10 + 13 = 23.
 *   7. doubles were compared with no delta. Use assertEquals(expected, actual, 1e-9).
 *
 * Every test builds the server through newServer(...) so the mutation drill can swap in a
 * broken subclass without touching a single assertion.
 */
public class CloudServerTest {

    private static final double DELTA = 1e-9;

    protected CloudServer newServer(double ram, ServerTier tier, int optimizationLevel) {
        return new CloudServer(ram, tier, optimizationLevel);
    }

    @Test
    @DisplayName("2. same tier: success, reduced cost 0.6 x load deducted, +40 speed bonus")
    void testSuccessfulAllocationSameTier() {
        CloudServer server = newServer(50.0, ServerTier.HIGH_MEM, 2);

        AllocationResult result = server.allocateTask(ServerTier.HIGH_MEM, 5.0);

        assertTrue(result.isSuccess());
        assertEquals(5.0 * 0.6, result.getActualRamCost(), DELTA);          // cost reported: 3.0
        assertEquals(50.0 - 5.0 * 0.6, server.getRam(), DELTA);             // RAM left: 47.0
        assertEquals((2 * 80.0) / (5.0 + 10.0) + 40.0, result.getProcessingSpeed(), DELTA);
    }

    @Test
    @DisplayName("3. different tier: success, standard cost 1.5 x load deducted, NO speed bonus")
    void testSuccessfulAllocationDifferentTier() {
        CloudServer server = newServer(50.0, ServerTier.STANDARD, 2);

        AllocationResult result = server.allocateTask(ServerTier.HIGH_MEM, 5.0);

        assertTrue(result.isSuccess());
        assertEquals(5.0 * 1.5, result.getActualRamCost(), DELTA);          // cost reported: 7.5
        assertEquals(50.0 - 5.0 * 1.5, server.getRam(), DELTA);             // RAM left: 42.5
        assertEquals((2 * 80.0) / (5.0 + 10.0), result.getProcessingSpeed(), DELTA);
    }

    @Test
    @DisplayName("4. not enough RAM: failure, RAM untouched, speed and cost both 0.0")
    void testFailedAllocationInsufficientRam() {
        // 10 GB free, the task needs 10 x 1.5 = 15 GB -> must fail.
        CloudServer server = newServer(10.0, ServerTier.STANDARD, 2);

        AllocationResult result = server.allocateTask(ServerTier.HIGH_MEM, 10.0);

        assertFalse(result.isSuccess());
        assertEquals(10.0, server.getRam(), DELTA);       // proves nothing was deducted
        assertEquals(0.0, result.getProcessingSpeed(), DELTA);
        assertEquals(0.0, result.getActualRamCost(), DELTA);
    }

    @Test
    @DisplayName("4b. exactly enough RAM is enough: the check is ram < cost, not ram <= cost")
    void testAllocationWithExactlyEnoughRamSucceeds() {
        CloudServer server = newServer(7.5, ServerTier.STANDARD, 2);

        AllocationResult result = server.allocateTask(ServerTier.HIGH_MEM, 5.0);   // cost 7.5

        assertTrue(result.isSuccess());
        assertEquals(0.0, server.getRam(), DELTA);
    }

    @Test
    @DisplayName("5. normal release: RAM grows by amount x (1.2 + level x 0.05) and that is returned")
    void testFreeRamNormal() {
        CloudServer server = newServer(10.0, ServerTier.STANDARD, 2);

        double returned = server.freeRam(10.0);

        double expected = 10.0 + 10.0 * (1.2 + 2 * 0.05);   // 10 + 13 = 23
        assertEquals(expected, returned, DELTA);
        assertEquals(expected, server.getRam(), DELTA);     // the returned value AND the state
    }

    @Test
    @DisplayName("6. capped release: RAM stops at exactly 600 + level x 40")
    void testFreeRamCap() {
        CloudServer server = newServer(10.0, ServerTier.STANDARD, 2);

        double returned = server.freeRam(60000.0);

        assertEquals(600.0 + 2 * 40.0, returned, DELTA);    // 680, NOT a wrapped-around remainder
        assertEquals(680.0, server.getRam(), DELTA);
    }

    @Test
    @DisplayName("extra: amount <= 0 changes nothing and returns the current RAM")
    void testFreeRamIgnoresNonPositiveAmount() {
        CloudServer server = newServer(10.0, ServerTier.STANDARD, 2);

        assertEquals(10.0, server.freeRam(0.0), DELTA);
        assertEquals(10.0, server.freeRam(-5.0), DELTA);
        assertEquals(10.0, server.getRam(), DELTA);
    }
}
