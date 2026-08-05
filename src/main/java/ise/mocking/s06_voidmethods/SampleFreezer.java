package ise.mocking.s06_voidmethods;

/** SUT: logs every reading of a freezing cycle and then flushes the log to disk. */
public class SampleFreezer {

    private final TemperatureLog log;

    public SampleFreezer(TemperatureLog log) {
        this.log = log;
    }

    public void runCycle(double... readings) {
        for (double reading : readings) {
            log.record(reading);
        }
        log.flush();
    }

    /** Same cycle, but a failing disk must not abort the experiment. */
    public boolean runCycleSafely(double... readings) {
        try {
            runCycle(readings);
            return true;
        } catch (IllegalStateException diskFull) {
            return false;
        }
    }
}
