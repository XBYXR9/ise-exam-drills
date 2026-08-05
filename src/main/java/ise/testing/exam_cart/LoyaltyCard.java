package ise.testing.exam_cart;

/** Present on the exam UML diagram; carries the discount rate. */
public class LoyaltyCard {

    private final String cardHolderName;
    private final double discountRate;

    public LoyaltyCard(String cardHolderName, double discountRate) {
        this.cardHolderName = cardHolderName;
        this.discountRate = discountRate;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public double getDiscountRate() {
        return discountRate;
    }
}
