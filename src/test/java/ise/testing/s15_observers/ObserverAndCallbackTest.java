package ise.testing.s15_observers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Observers and callbacks, tested WITHOUT a mocking framework: a hand-written test
 * spy is a five-line class that counts calls, and it is often clearer than a mock.
 *
 * This is the ETF / chart-view scenario from the exam patterns exercise.
 */
public class ObserverAndCallbackTest {

    /** The test spy: it implements the interface and records what happened to it. */
    static class SpyChartView implements ChartView {

        int updateCount = 0;

        @Override
        public void update() {
            updateCount++;
        }
    }

    protected Etf newEtf() {
        return new Etf();
    }

    @Test
    @DisplayName("a price change notifies every attached view exactly once")
    void allAttachedViewsAreNotified() {
        Etf etf = newEtf();
        SpyChartView lineChart = new SpyChartView();
        SpyChartView barChart = new SpyChartView();
        etf.attach(lineChart);
        etf.attach(barChart);

        etf.setPrice(101.0);

        // Exactly 1, not "at least 1": a notify loop that fires twice is also a bug.
        assertEquals(1, lineChart.updateCount);
        assertEquals(1, barChart.updateCount);
        assertEquals(101.0, etf.getPrice(), 0.0001);
    }

    @Test
    @DisplayName("a detached view is not notified any more")
    void detachedViewIsNotNotified() {
        Etf etf = newEtf();
        SpyChartView view = new SpyChartView();
        etf.attach(view);
        etf.detach(view);

        etf.setPrice(101.0);

        // 0 is the assertion that proves detach did something. Checking only the
        // remaining view count would pass for a detach that removed the wrong element.
        assertEquals(0, view.updateCount);
        assertEquals(0, etf.getViewCount());
    }

    @Test
    @DisplayName("detaching one view leaves the other subscribed")
    void detachRemovesOnlyThatView() {
        Etf etf = newEtf();
        SpyChartView kept = new SpyChartView();
        SpyChartView dropped = new SpyChartView();
        etf.attach(kept);
        etf.attach(dropped);

        etf.detach(dropped);
        etf.setPrice(101.0);

        assertEquals(1, kept.updateCount);
        assertEquals(0, dropped.updateCount);
    }

    @Test
    @DisplayName("a Consumer callback is captured into a local list and then asserted on")
    void callbackReceivesTheQuotes() {
        List<Double> received = new ArrayList<>();

        new QuoteFeed().loadQuotes(received::addAll);

        // The same pattern the REST client task needs: the result arrives through the
        // callback, so the test has to hold somewhere for it to land.
        assertFalse(received.isEmpty());
        assertEquals(3, received.size());
        assertEquals(101.5, received.get(0), 0.0001);
        assertTrue(received.contains(103.25));
    }
}
