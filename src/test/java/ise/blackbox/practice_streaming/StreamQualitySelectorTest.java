package ise.blackbox.practice_streaming;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Answer key to the practice black-box exercise -- see docs/ISEHN_2026_EXAM.md, part C. */
class StreamQualitySelectorTest {

    private final StreamQualitySelector selector = new StreamQualitySelector();

    @ParameterizedTest(name = "{0} | {1} | {2} | expect {3}")
    @CsvSource({
            "PT1, Basic,   500,  unavailable",
            "PT2, Basic,   2000, SD",
            "PT3, Basic,   5000, HD",
            "PT4, Premium, 4000, HD",
            "PT5, Premium, 7000, UHD",
            "PT6, Premium, 9000, unavailable",
            "PT7, Gold,    4000, unavailable"
    })
    void representativeCases(String tc, String plan, int kbps, String expected) {
        assertEquals(expected, selector.select(plan, kbps), tc);
    }

    @ParameterizedTest(name = "Basic {0} -> {1}")
    @CsvSource({"2999, SD", "3000, SD", "3001, HD"})
    void basicSdUpperLimit(int kbps, String expected) {
        assertEquals(expected, selector.select("Basic", kbps));
    }

    @ParameterizedTest(name = "Premium {0} -> {1}")
    @CsvSource({"999, unavailable", "1000, HD", "1001, HD", "8000, UHD", "8001, unavailable"})
    void premiumOuterLimits(int kbps, String expected) {
        assertEquals(expected, selector.select("Premium", kbps));
    }
}
