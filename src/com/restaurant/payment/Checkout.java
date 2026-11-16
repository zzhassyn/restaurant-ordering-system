package com.restaurant.payment;


public class Checkout {

    private PaymentStrategy paymentStrategy;

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public boolean processPayment(int amount) {
        if (paymentStrategy == null) {
            throw new IllegalStateException("Payment method is not selected");
        }
        System.out.printf("  [PAYMENT] Paying %d KZT by %s%n", amount, paymentStrategy.getMethodName());
        return paymentStrategy.pay(amount);
    }
}
