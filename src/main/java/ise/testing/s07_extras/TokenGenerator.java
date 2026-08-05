package ise.testing.s07_extras;

import java.util.concurrent.ThreadLocalRandom;

/** Fast and non-deterministic: the natural home for @RepeatedTest and @Timeout. */
public class TokenGenerator {

    public int nextToken() {
        return ThreadLocalRandom.current().nextInt(1, 1_000_000);
    }

    public long factorial(int n) {
        long result = 1L;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
