package com.restaurant.payment;


public interface PaymentStrategy {

    boolean pay(int amount);

    String getMethodName();
}
