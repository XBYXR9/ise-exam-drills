package ise.mocking.s06_voidmethods;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.anyDouble;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A void method has no return value, so expect(...) has nothing to wrap.
 * The idiom is: CALL the method on the mock during the record phase, then declare
 * the expectation for that last call with expectLastCall().
 */
class SampleFreezerTest {

    @Test
    @DisplayName("every reading is written to the log and the log is flushed once")
    void allReadingsAreRecordedThenFlushed() {
        TemperatureLog log = createMock(TemperatureLog.class);

        log.record(-18.0);
        expectLastCall();          // exactly once, with exactly this argument
        log.record(-19.5);
        expectLastCall();
        log.flush();
        expectLastCall();

        replay(log);

        new SampleFreezer(log).runCycle(-18.0, -19.5);

        // Only verify() can catch a deleted loop body here: there is no return value
        // and no SUT state to look at.
        verify(log);
    }

    @Test
    @DisplayName("expectLastCall().times(n) pins down how often a void method runs")
    void timesOnAVoidMethod() {
        TemperatureLog log = createMock(TemperatureLog.class);

        log.record(-18.0);
        expectLastCall().times(3);
        log.flush();
        expectLastCall();

        replay(log);

        new SampleFreezer(log).runCycle(-18.0, -18.0, -18.0);

        verify(log);
    }

    @Test
    @DisplayName("expectLastCall().anyTimes() accepts any number of readings")
    void anyTimesOnAVoidMethod() {
        TemperatureLog log = createMock(TemperatureLog.class);

        log.record(anyDouble());
        expectLastCall().anyTimes();   // 0..n readings are all fine
        log.flush();
        expectLastCall();              // but the flush is mandatory

        replay(log);

        new SampleFreezer(log).runCycle(-18.0, -19.0, -20.0, -21.0);

        verify(log);
    }

    @Test
    @DisplayName("expectLastCall().andThrow makes a void collaborator fail, and the SUT propagates it")
    void andThrowOnAVoidMethod() {
        TemperatureLog log = createMock(TemperatureLog.class);

        log.record(-18.0);
        expectLastCall();
        log.flush();
        expectLastCall().andThrow(new IllegalStateException("disk full"));

        replay(log);

        SampleFreezer freezer = new SampleFreezer(log);
        assertThrows(IllegalStateException.class, () -> freezer.runCycle(-18.0));
        verify(log);
    }

    @Test
    @DisplayName("the safe variant swallows the disk failure and reports false")
    void safeVariantHandlesTheFailure() {
        TemperatureLog log = createMock(TemperatureLog.class);

        log.record(anyDouble());
        expectLastCall().asStub();     // allowed any number of times, never verified
        log.flush();
        expectLastCall().andThrow(new IllegalStateException("disk full"));

        replay(log);

        // Asserting false is the point. A check for "it did not throw" would also hold
        // for a method whose body was deleted.
        assertFalse(new SampleFreezer(log).runCycleSafely(-18.0));
        verify(log);
    }
}
