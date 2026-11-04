package com.restaurant.payment;

/**
 * Context of the Strategy. It does not know how a payment is made - it delegates
 * to whichever strategy is currently set, and the strategy can be swapped at runtime.
 */
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
