package de.tum.ise;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CloudServerTest {

    private static final double DELTA = 0.0001;

    @Test
    void testSuccessfulAllocationSameTier() {
        CloudServer cloudServer = new CloudServer(50.0, ServerTier.HIGH_MEM, 2);

        AllocationResult allocationResult = cloudServer.allocateTask(ServerTier.HIGH_MEM, 5.0);

        assertTrue(allocationResult.isSuccess());
        assertEquals(5.0 * 0.6, allocationResult.getActualRamCost(), DELTA);        // 3.0 deducted
        assertEquals(50.0 - 5.0 * 0.6, cloudServer.getRam(), DELTA);                // 47.0 left
        assertEquals((2 * 80.0) / (5.0 + 10.0) + 40.0, allocationResult.getProcessingSpeed(), DELTA);
    }

    @Test
    void testSuccessfulAllocationDifferentTier() {
        CloudServer cloudServer = new CloudServer(50.0, ServerTier.STANDARD, 2);

        AllocationResult allocationResult = cloudServer.allocateTask(ServerTier.HIGH_MEM, 5.0);

        assertTrue(allocationResult.isSuccess());
        assertEquals(5.0 * 1.5, allocationResult.getActualRamCost(), DELTA);        // 7.5 deducted
        assertEquals(50.0 - 5.0 * 1.5, cloudServer.getRam(), DELTA);                // 42.5 left
        assertEquals((2 * 80.0) / (5.0 + 10.0), allocationResult.getProcessingSpeed(), DELTA);
    }

    @Test
    void testFailedAllocationInsufficientRam() {
        CloudServer cloudServer = new CloudServer(10.0, ServerTier.STANDARD, 2);

        AllocationResult allocationResult = cloudServer.allocateTask(ServerTier.HIGH_MEM, 10.0);   // needs 15

        assertFalse(allocationResult.isSuccess());
        assertEquals(10.0, cloudServer.getRam(), DELTA);                            // unchanged
        assertEquals(0.0, allocationResult.getProcessingSpeed(), DELTA);
        assertEquals(0.0, allocationResult.getActualRamCost(), DELTA);
    }

    @Test
    void testFreeRamNormal() {
        CloudServer cloudServer = new CloudServer(10.0, ServerTier.STANDARD, 2);

        double result = cloudServer.freeRam(10.0);

        double expected = 10.0 + 10.0 * (1.2 + (2 * 0.05));                         // 10 + 13 = 23
        assertEquals(expected, result, DELTA);
        assertEquals(expected, cloudServer.getRam(), DELTA);
    }

    @Test
    void testFreeRamCap() {
        CloudServer cloudServer = new CloudServer(10.0, ServerTier.STANDARD, 2);

        double result = cloudServer.freeRam(60000.0);

        double cap = 600.0 + (2 * 40.0);                                            // 680
        assertEquals(cap, result, DELTA);
        assertEquals(cap, cloudServer.getRam(), DELTA);
    }
}
