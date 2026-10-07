package de.tum.ise;

public class AllocationResult {
    private final boolean success;
    private final double processingSpeed;
    private final double actualRamCost;

    public AllocationResult(boolean success, double processingSpeed, double actualRamCost) {
        this.success = success;
        this.processingSpeed = processingSpeed;
        this.actualRamCost = actualRamCost;
    }

    public boolean isSuccess() {
        return success;
    }

    public double getProcessingSpeed() {
        return processingSpeed;
    }

    public double getActualRamCost() {
        return actualRamCost;
    }
}
