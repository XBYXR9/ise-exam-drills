package ise.mutants.testing;

import ise.blackbox.exam_graphics.GraphicsConfigurator;
import ise.blackbox.exam_graphics.GraphicsConfiguratorBlackBoxTest;
import ise.testing.exam_cloudserver.AllocationResult;
import ise.testing.exam_cloudserver.CloudServer;
import ise.testing.exam_cloudserver.CloudServerTest;
import ise.testing.exam_cloudserver.ServerTier;
import org.junit.jupiter.api.Tag;

/**
 * MUTANTS for ISE HN 2026 exercise 1 (CloudServer). Each one is a plausible defect a grader
 * would inject -- the first is the real bug in the code you submitted.
 */
final class CloudServerMutants {

    private CloudServerMutants() {
    }
}

/** freeRam wraps with % instead of capping -- the bug in the code you submitted. */
class CloudServerThatWrapsAround extends CloudServer {
    private double ramShadow;

    CloudServerThatWrapsAround(double ram, ServerTier tier, int level) {
        super(ram, tier, level);
        this.ramShadow = ram;
    }

    @Override
    public double getRam() {
        return ramShadow;
    }

    @Override
    public AllocationResult allocateTask(ServerTier taskTier, double taskLoad) {
        AllocationResult r = super.allocateTask(taskTier, taskLoad);
        ramShadow = super.getRam();
        return r;
    }

    @Override
    public double freeRam(double amount) {
        if (amount <= 0.0) {
            return ramShadow;
        }
        ramShadow = (ramShadow + amount * (1.2 + getOptimizationLevel() * 0.05))
                % (600.0 + getOptimizationLevel() * 40.0);
        return ramShadow;
    }
}

@Tag("mutant")
class CloudServerTest_WrapsAround extends CloudServerTest {
    @Override
    protected CloudServer newServer(double ram, ServerTier tier, int level) {
        return new CloudServerThatWrapsAround(ram, tier, level);
    }
    // CAUGHT BY: testFreeRamCap -- 60010 % 680 is nowhere near 680.
}

/** The +40 bonus is never added. */
class CloudServerWithoutBonus extends CloudServer {
    CloudServerWithoutBonus(double ram, ServerTier tier, int level) {
        super(ram, tier, level);
    }

    @Override
    public AllocationResult allocateTask(ServerTier taskTier, double taskLoad) {
        AllocationResult r = super.allocateTask(taskTier, taskLoad);
        return r.isSuccess()
                ? new AllocationResult(true, r.getProcessingSpeed() - (taskTier == getServerTier() ? 40.0 : 0.0),
                r.getActualRamCost())
                : r;
    }
}

@Tag("mutant")
class CloudServerTest_NoBonus extends CloudServerTest {
    @Override
    protected CloudServer newServer(double ram, ServerTier tier, int level) {
        return new CloudServerWithoutBonus(ram, tier, level);
    }
    // CAUGHT BY: testSuccessfulAllocationSameTier -- only the speed assertion notices.
}

/** Same-tier discount ignored: every task pays 1.5 x load. */
class CloudServerWithoutDiscount extends CloudServer {
    private double ramLeft;

    CloudServerWithoutDiscount(double ram, ServerTier tier, int level) {
        super(ram, tier, level);
        this.ramLeft = ram;
    }

    @Override
    public double getRam() {
        return ramLeft;
    }

    @Override
    public AllocationResult allocateTask(ServerTier taskTier, double taskLoad) {
        double cost = taskLoad * 1.5;
        if (ramLeft < cost) {
            return new AllocationResult(false, 0.0, 0.0);
        }
        ramLeft -= cost;
        double speed = (getOptimizationLevel() * 80.0) / (taskLoad + 10.0)
                + (taskTier == getServerTier() ? 40.0 : 0.0);
        return new AllocationResult(true, speed, cost);
    }

    @Override
    public double freeRam(double amount) {
        return ramLeft;
    }
}

@Tag("mutant")
class CloudServerTest_NoDiscount extends CloudServerTest {
    @Override
    protected CloudServer newServer(double ram, ServerTier tier, int level) {
        return new CloudServerWithoutDiscount(ram, tier, level);
    }
    // CAUGHT BY: testSuccessfulAllocationSameTier -- cost 7.5 instead of 3.0.
}

/** "ram < cost" became "ram <= cost": a server with exactly enough RAM is refused. */
class CloudServerThatRefusesExactFit extends CloudServer {
    CloudServerThatRefusesExactFit(double ram, ServerTier tier, int level) {
        super(ram, tier, level);
    }

    @Override
    public AllocationResult allocateTask(ServerTier taskTier, double taskLoad) {
        double cost = taskTier == getServerTier() ? taskLoad * 0.6 : taskLoad * 1.5;
        if (getRam() <= cost) {
            return new AllocationResult(false, 0.0, 0.0);
        }
        return super.allocateTask(taskTier, taskLoad);
    }
}

@Tag("mutant")
class CloudServerTest_OffByOneFit extends CloudServerTest {
    @Override
    protected CloudServer newServer(double ram, ServerTier tier, int level) {
        return new CloudServerThatRefusesExactFit(ram, tier, level);
    }
    // CAUGHT BY: testAllocationWithExactlyEnoughRamSucceeds -- the boundary test I added.
    // testFailedAllocationInsufficientRam passes against this mutant (10 < 15 either way).
}

// ---------------------------------------------------------------------------
// ISE HN 2026 exercise 3 (black-box): the baseline is exclusive instead of inclusive.
// ---------------------------------------------------------------------------
class GraphicsConfiguratorWithExclusiveBaseline extends GraphicsConfigurator {
    @Override
    public String selectPreset(String deviceType, int vramMB) {
        if (vramMB == MIN_VRAM) {
            return UNSUPPORTED;
        }
        return super.selectPreset(deviceType, vramMB);
    }
}

@Tag("mutant")
class GraphicsConfiguratorTest_OffByOneBaseline extends GraphicsConfiguratorBlackBoxTest {
    @Override
    protected GraphicsConfigurator newConfigurator() {
        return new GraphicsConfiguratorWithExclusiveBaseline();
    }
    // CAUGHT BY: TC11 (Mobile, 2048, performance) -- and by nothing in TC10/TC12.
}
