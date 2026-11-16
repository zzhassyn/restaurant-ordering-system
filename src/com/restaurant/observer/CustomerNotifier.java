package com.restaurant.observer;

import com.restaurant.order.Order;


public class CustomerNotifier implements OrderObserver {

    private final String customerName;

    public CustomerNotifier(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public void update(Order order) {
        System.out.printf("  [CUSTOMER %s] %s%n", customerName, messageFor(order));
    }

    private String messageFor(Order order) {
        int id = order.getId();
        return switch (order.getStatus()) {
            case NEW -> "Order #" + id + " is being created";
            case PLACED -> "Order #" + id + " received, total " + order.getTotalPrice() + " KZT";
            case PREPARING -> "The chef started cooking order #" + id;
            case READY -> readyMessage(order);
            case COMPLETED -> "Order #" + id + " completed. Enjoy your meal!";
            case CANCELLED -> "Order #" + id + " has been cancelled";
        };
    }

    private String readyMessage(Order order) {
        int id = order.getId();
        return switch (order.getOrderType()) {
            case DINE_IN -> "Order #" + id + " is ready, a waiter is bringing it to table " + order.getTableNumber();
            case TAKEAWAY -> "Order #" + id + " is ready for pickup at the counter";
            case DELIVERY -> "Order #" + id + " is ready, the courier is heading to " + order.getDeliveryAddress();
        };
    }
}
