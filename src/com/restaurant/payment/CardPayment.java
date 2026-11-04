package com.restaurant.payment;

// Concrete Strategy
public class CardPayment implements PaymentStrategy {

    private static final int CARD_NUMBER_LENGTH = 16;

    private final String cardNumber;

    public CardPayment(String cardNumber) {
        this.cardNumber = cardNumber.replaceAll("\\s", "");
    }

    @Override
    public boolean pay(int amount) {
        if (!isValidCardNumber()) {
            System.out.printf("  [PAYMENT] Card declined: the number must have %d digits%n", CARD_NUMBER_LENGTH);
            return false;
        }
        System.out.printf("  [PAYMENT] Charged %d KZT to card %s%n", amount, maskedCardNumber());
        return true;
    }

    @Override
    public String getMethodName() {
        return "Card";
    }

    private boolean isValidCardNumber() {
        return cardNumber.matches("\\d{" + CARD_NUMBER_LENGTH + "}");
    }

    private String maskedCardNumber() {
        return "**** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
