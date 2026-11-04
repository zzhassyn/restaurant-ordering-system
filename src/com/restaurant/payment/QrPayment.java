package com.restaurant.payment;

// Concrete Strategy
public class QrPayment implements PaymentStrategy {

    private final String phoneNumber;

    public QrPayment(String phoneNumber) {
        this.phoneNumber = phoneNumber.trim();
    }

    @Override
    public boolean pay(int amount) {
        if (phoneNumber.isEmpty()) {
            System.out.println("  [PAYMENT] QR payment needs the phone number of the customer's banking app");
            return false;
        }
        System.out.printf("  [PAYMENT] QR code scanned from %s, %d KZT confirmed%n", phoneNumber, amount);
        return true;
    }

    @Override
    public String getMethodName() {
        return "QR code";
    }
}
