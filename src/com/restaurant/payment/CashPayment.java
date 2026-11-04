package com.restaurant.payment;

// Concrete Strategy
public class CashPayment implements PaymentStrategy {

    private final int cashGiven;

    public CashPayment(int cashGiven) {
        this.cashGiven = cashGiven;
    }

    @Override
    public boolean pay(int amount) {
        if (cashGiven < amount) {
            System.out.printf("  [PAYMENT] %d KZT in cash is not enough, %d KZT more is needed%n",
                    cashGiven, amount - cashGiven);
            return false;
        }
        System.out.printf("  [PAYMENT] Received %d KZT in cash, change: %d KZT%n", cashGiven, cashGiven - amount);
        return true;
    }

    @Override
    public String getMethodName() {
        return "Cash";
    }
}
