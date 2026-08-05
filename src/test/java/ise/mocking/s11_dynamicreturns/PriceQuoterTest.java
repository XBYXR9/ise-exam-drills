package ise.mocking.s11_dynamicreturns;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** Returning something COMPUTED from the arguments instead of a fixed value. */
class PriceQuoterTest {

    /** A tiny hand-written fake, used as the delegate target below. */
    static class FakeRates implements ExchangeRateService {
        @Override
        public double rateFor(String currency) {
            return "USD".equals(currency) ? 1.10 : 1.00;
        }
    }

    @Test
    @DisplayName("andAnswer computes the return value from the arguments at call time")
    void andAnswerReadsTheArguments() {
        ExchangeRateService rates = createMock(ExchangeRateService.class);

        expect(rates.rateFor(anyString())).andAnswer(() -> {
            String currency = (String) getCurrentArguments()[0];
            return "USD".equals(currency) ? 1.10 : 1.00;
        }).anyTimes();
        replay(rates);

        PriceQuoter quoter = new PriceQuoter(rates);

        // Two different currencies through ONE recorded expectation. Note the values:
        // 100 x 1.10 = 110 is not reachable by any plain copy of the input, so a SUT
        // that ignored the rate would be caught here.
        assertEquals(110.0, quoter.quote(100.0, "USD"), 0.0001);
        assertEquals(100.0, quoter.quote(100.0, "CHF"), 0.0001);
        verify(rates);
    }

    @Test
    @DisplayName("andDelegateTo forwards the call to a real fake implementation")
    void andDelegateToForwardsToAFake() {
        ExchangeRateService rates = createMock(ExchangeRateService.class);

        expect(rates.rateFor(anyString())).andDelegateTo(new FakeRates()).anyTimes();
        replay(rates);

        PriceQuoter quoter = new PriceQuoter(rates);

        assertEquals(110.0, quoter.quote(100.0, "USD"), 0.0001);
        assertEquals(100.0, quoter.quote(100.0, "CHF"), 0.0001);
        // Delegating keeps verify() available, so you get fake behaviour AND
        // interaction checking in the same object.
        verify(rates);
    }
}
