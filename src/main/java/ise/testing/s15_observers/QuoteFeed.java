package ise.testing.s15_observers;

import java.util.List;
import java.util.function.Consumer;

/**
 * Callback-style API, the same shape as the REST client task: the result arrives
 * through a Consumer instead of a return value. The test has to capture it.
 */
public class QuoteFeed {

    public void loadQuotes(Consumer<List<Double>> callback) {
        callback.accept(List.of(101.5, 99.0, 103.25));
    }
}
