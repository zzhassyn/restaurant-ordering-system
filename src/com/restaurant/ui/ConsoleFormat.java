package com.restaurant.ui;

import com.restaurant.facade.RestaurantFacade;
import com.restaurant.meal.Meal;
import com.restaurant.order.Order;
import com.restaurant.topping.Topping;

import java.util.List;
import java.util.Map;

/** Console output shared by the interactive app and the demo scenario. */
final class ConsoleFormat {

    private static final String DOUBLE_LINE = "============================================================";

    private ConsoleFormat() {
    }

    static void section(String title) {
        System.out.println();
        System.out.println(DOUBLE_LINE);
        System.out.println(" " + title);
        System.out.println(DOUBLE_LINE);
    }

    static void printMenu(RestaurantFacade restaurant) {
        System.out.println("  MENU");
        for (Map.Entry<Integer, Meal> item : restaurant.getMenuItems().entrySet()) {
            Meal meal = item.getValue();
            System.out.printf("   %d. %-28s %5d KZT%n", item.getKey(), meal.getDescription(), meal.getPrice());
        }
        System.out.println("  TOPPINGS");
        List<Topping> toppings = restaurant.getToppings();
        for (int i = 0; i < toppings.size(); i++) {
            Topping topping = toppings.get(i);
            System.out.printf("   %d. %-28s %5s KZT%n", i + 1, topping.getLabel(), "+" + topping.getPrice());
        }
    }

    static void printOrder(Order order) {
        System.out.printf("  #%d %s | %s | %s | %d KZT | %s%n",
                order.getId(), order.getCustomerName(), order.describeDestination(),
                order.getStatus(), order.getTotalPrice(), order.isPaid() ? "paid" : "not paid");
        for (Meal meal : order.getMeals()) {
            System.out.printf("       - %s (%d KZT)%n", meal.getDescription(), meal.getPrice());
        }
    }
}
