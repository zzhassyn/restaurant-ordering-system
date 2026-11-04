package com.restaurant.payment;

/**
 * Strategy: a family of interchangeable payment algorithms.
 * The checkout works with any of them through this interface.
 */
public interface PaymentStrategy {

    /** Tries to charge the amount in KZT and returns true if the payment went through. */
    boolean pay(int amount);

    String getMethodName();
}
