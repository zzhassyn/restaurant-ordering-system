package com.restaurant.observer;

import com.restaurant.meal.Meal;
import com.restaurant.order.Order;


public class KitchenDisplay implements OrderObserver {

    @Override
    public void update(Order order) {
        switch (order.getStatus()) {
            case PLACED -> printTicket(order);
            case PREPARING -> print("Ticket #%d: cooking started", order.getId());
            case READY -> print("Ticket #%d: ready to hand over", order.getId());
            case COMPLETED -> print("Ticket #%d: closed", order.getId());
            case CANCELLED -> print("Ticket #%d: CANCELLED - stop cooking", order.getId());
            default -> {
                // NEW orders are not on the kitchen screen yet
            }
        }
    }

    private void printTicket(Order order) {
        print("New ticket #%d (%s)", order.getId(), order.describeDestination());
        for (Meal meal : order.getMeals()) {
            print("  - %s", meal.getDescription());
        }
        if (!order.getNotes().isEmpty()) {
            print("  Notes: %s", order.getNotes());
        }
    }

    private void print(String format, Object... args) {
        System.out.println("  [KITCHEN] " + String.format(format, args));
    }
}
