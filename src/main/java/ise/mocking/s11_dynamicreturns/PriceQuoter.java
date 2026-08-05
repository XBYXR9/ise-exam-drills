package ise.mocking.s11_dynamicreturns;

/** SUT: converts a EUR price into a foreign currency using the live rate. */
public class PriceQuoter {

    private final ExchangeRateService rates;

    public PriceQuoter(ExchangeRateService rates) {
        this.rates = rates;
    }

    public double quote(double priceInEuro, String currency) {
        return priceInEuro * rates.rateFor(currency);
    }
}
