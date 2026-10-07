package de.tum.ise;

public class CloudServer {

    private double ram;
    private ServerTier serverTier;
    private final int optimizationLevel;

    public CloudServer(double ram, ServerTier serverTier, int optimizationLevel) {
        this.ram = ram;
        this.serverTier = serverTier;
        this.optimizationLevel = optimizationLevel;
    }

    public double getRam() {
        return ram;
    }

    public ServerTier getServerTier() {
        return serverTier;
    }

    public int getOptimizationLevel() {
        return optimizationLevel;
    }

    public AllocationResult allocateTask(ServerTier taskTier, double taskLoad) {
        double actualRamCost = taskLoad * 1.5;
        if (serverTier == taskTier) {
            actualRamCost = taskLoad * 0.6;
        }
        if (ram < actualRamCost) {
            return new AllocationResult(false, 0.0, 0.0);   // RAM untouched on failure
        }
        ram -= actualRamCost;
        double processingSpeed = (optimizationLevel * 80.0) / (taskLoad + 10.0);
        if (serverTier == taskTier) {
            processingSpeed += 40.0;
        }
        return new AllocationResult(true, processingSpeed, actualRamCost);
    }

    public double freeRam(double amount) {
        if (amount <= 0.0) {
            return ram;
        }
        double effectiveRam = amount * (1.2 + (optimizationLevel * 0.05));
        double cap = 600.0 + (optimizationLevel * 40.0);
        ram = Math.min(ram + effectiveRam, cap);               // cap, NOT modulo
        return ram;
    }
}
