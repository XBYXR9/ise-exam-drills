package ise.testing.exam_cloudserver;

/**
 * ISE HN 2026 exam, exercise 1 -- "CloudServer": implement two methods, then test them.
 *
 * THE TWO FORMULAS THE TASK GIVES YOU (copy them, do not "improve" them)
 *   allocateTask:  cost  = taskLoad * 1.5          (0.6 if the tier matches)
 *                  speed = optimizationLevel * 80.0 / (taskLoad + 10.0)   (+40.0 if it matches)
 *   freeRam:       freed = amount * (1.2 + optimizationLevel * 0.05), then CAP at 600 + level * 40
 *
 * The bug in the code you submitted, fixed here:
 *   freeRam used  ram % cap  -- a modulo WRAPS around to a small number instead of
 *   stopping at the cap. "Never exceeds the cap" means Math.min, not %.
 * Also keep in mind: the failed branch must not touch state ("do not modify the server's RAM"),
 * so the check must come BEFORE the deduction.
 */
public class CloudServer {

    private double ram;
    private final ServerTier serverTier;
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

    public double maxRam() {
        return 600.0 + (optimizationLevel * 40.0);
    }

    public AllocationResult allocateTask(ServerTier taskTier, double taskLoad) {
        boolean specialised = serverTier == taskTier;
        double actualRamCost = specialised ? taskLoad * 0.6 : taskLoad * 1.5;

        if (ram < actualRamCost) {
            // Note: "<", not "<=" -- a server with EXACTLY enough RAM succeeds and ends at 0.
            return new AllocationResult(false, 0.0, 0.0);
        }

        ram -= actualRamCost;
        double processingSpeed = (optimizationLevel * 80.0) / (taskLoad + 10.0);
        if (specialised) {
            processingSpeed += 40.0;
        }
        return new AllocationResult(true, processingSpeed, actualRamCost);
    }

    public double freeRam(double amount) {
        if (amount <= 0.0) {
            return ram;
        }
        double effectiveRam = amount * (1.2 + (optimizationLevel * 0.05));
        ram = Math.min(ram + effectiveRam, maxRam());
        return ram;
    }
}
