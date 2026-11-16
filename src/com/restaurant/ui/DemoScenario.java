package com.restaurant.ui;

import com.restaurant.facade.RestaurantFacade;
import com.restaurant.meal.Meal;
import com.restaurant.order.Order;
import com.restaurant.order.OrderType;
import com.restaurant.payment.CardPayment;
import com.restaurant.payment.CashPayment;
import com.restaurant.payment.PaymentStrategy;
import com.restaurant.payment.QrPayment;
import com.restaurant.topping.Topping;

import java.util.List;
import java.util.Map;


public class DemoScenario {

    private final RestaurantFacade restaurant;

    private Order aigerimOrder;
    private Order timurOrder;
    private Order danaOrder;
    private Order armanOrder;

    public DemoScenario(RestaurantFacade restaurant) {
        this.restaurant = restaurant;
    }

    public void run() {
        System.out.println("=== Restaurant Ordering System - design patterns demo ===");
        showFactoryMethod();
        showDecorator();
        showBuilder();
        showObserver();
        showStrategy();
        showFacade();
    }

    private void showFactoryMethod() {
        ConsoleFormat.section("1. FACTORY METHOD - each dish is created by its own factory");
        for (Map.Entry<Integer, Meal> item : restaurant.getMenuItems().entrySet()) {
            Meal meal = item.getValue();
            System.out.printf("   %d. %-20s %5d KZT   (class %s)%n",
                    item.getKey(), meal.getDescription(), meal.getPrice(), meal.getClass().getSimpleName());
        }
    }

    private void showDecorator() {
        ConsoleFormat.section("2. DECORATOR - toppings wrap a meal at runtime");
        Meal burger = restaurant.createMeal(1, List.of());
        printMeal(burger);
        burger = Topping.EXTRA_CHEESE.applyTo(burger);
        printMeal(burger);
        burger = Topping.BACON.applyTo(burger);
        printMeal(burger);
        System.out.println("  The same decorators work for any dish:");
        printMeal(restaurant.createMeal(2, List.of(Topping.MUSHROOMS, Topping.JALAPENO, Topping.EXTRA_SAUCE)));
        System.out.println("  A topping can even be added twice:");
        printMeal(restaurant.createMeal(1, List.of(Topping.BACON, Topping.BACON)));
    }

    private void showBuilder() {
        ConsoleFormat.section("3. BUILDER - orders are assembled step by step");
        aigerimOrder = new Order.Builder()
                .setCustomerName("Aigerim")
                .setOrderType(OrderType.DINE_IN)
                .setTableNumber(5)
                .addMeal(restaurant.createMeal(1, List.of(Topping.EXTRA_CHEESE, Topping.BACON)))
                .addMeal(restaurant.createMeal(4, List.of()))
                .setNotes("No onions, please")
                .build();
        timurOrder = new Order.Builder()
                .setCustomerName("Timur")
                .setOrderType(OrderType.DELIVERY)
                .setDeliveryAddress("Mangilik El 55, apt 12")
                .addMeal(restaurant.createMeal(2, List.of(Topping.MUSHROOMS, Topping.JALAPENO)))
                .addMeal(restaurant.createMeal(3, List.of()))
                .build();
        danaOrder = new Order.Builder()
                .setCustomerName("Dana")
                .setOrderType(OrderType.TAKEAWAY)
                .addMeal(restaurant.createMeal(3, List.of(Topping.EXTRA_CHEESE)))
                .build();
        armanOrder = new Order.Builder()
                .setCustomerName("Arman")
                .setOrderType(OrderType.DINE_IN)
                .setTableNumber(2)
                .addMeal(restaurant.createMeal(1, List.of()))
                .build();
        for (Order order : List.of(aigerimOrder, timurOrder, danaOrder, armanOrder)) {
            System.out.printf("   Built #%d %s | %s | %d meal(s) | %d KZT%n", order.getId(), order.getCustomerName(),
                    order.describeDestination(), order.getMeals().size(), order.getTotalPrice());
        }

        System.out.println("  Invalid combinations are rejected by build():");
        tryBuild("Dine-in without a table", new Order.Builder()
                .setCustomerName("Bob")
                .addMeal(restaurant.createMeal(1, List.of())));
        tryBuild("Order without meals", new Order.Builder()
                .setCustomerName("Bob")
                .setOrderType(OrderType.TAKEAWAY));
    }

    private void showObserver() {
        ConsoleFormat.section("4. OBSERVER - kitchen and customer follow every status change");
        step("Placing the orders (kitchen display and customer notifier subscribe to each)");
        restaurant.placeOrder(aigerimOrder);
        restaurant.placeOrder(timurOrder);
        restaurant.placeOrder(danaOrder);
        restaurant.placeOrder(armanOrder);

        step("The kitchen starts cooking for Aigerim, Timur and Dana");
        advance(aigerimOrder);
        advance(timurOrder);
        advance(danaOrder);

        step("Arman changes his mind and cancels");
        restaurant.cancelOrder(armanOrder.getId());

        step("The dishes for Aigerim, Timur and Dana are ready");
        advance(aigerimOrder);
        advance(timurOrder);
        advance(danaOrder);
    }

    private void showStrategy() {
        ConsoleFormat.section("5. STRATEGY - the payment method is chosen at runtime");
        step("Aigerim tries to pay with 5000 KZT in cash");
        pay(aigerimOrder, new CashPayment(5000));
        step("Not enough, so she switches to a card");
        pay(aigerimOrder, new CardPayment("1234 5678 9012 3456"));
        step("Timur pays by QR code");
        pay(timurOrder, new QrPayment("+7 701 555 1234"));
    }

    private void showFacade() {
        ConsoleFormat.section("6. FACADE - one class drives the whole workflow");
        System.out.println("  Every step above was a single RestaurantFacade call:");
        System.out.println("  createMeal, placeOrder, advanceOrder, payOrder, cancelOrder.");

        step("Completing the paid orders of Aigerim and Timur");
        advance(aigerimOrder);
        advance(timurOrder);

        step("Dana's order is ready but not paid yet - the facade blocks completion");
        try {
            advance(danaOrder);
        } catch (IllegalStateException e) {
            System.out.println("  ! " + e.getMessage());
        }
        pay(danaOrder, new CashPayment(4000));
        advance(danaOrder);

        step("Final state of all orders");
        for (Order order : restaurant.getOrders()) {
            ConsoleFormat.printOrder(order);
        }
    }

    private void tryBuild(String description, Order.Builder builder) {
        try {
            builder.build();
            System.out.printf("   %-24s -> built%n", description);
        } catch (IllegalStateException e) {
            System.out.printf("   %-24s -> rejected: %s%n", description, e.getMessage());
        }
    }

    private void advance(Order order) {
        restaurant.advanceOrder(order.getId());
    }

    private void pay(Order order, PaymentStrategy paymentStrategy) {
        boolean paid = restaurant.payOrder(order.getId(), paymentStrategy);
        System.out.println("  Result: " + (paid ? "paid" : "payment failed"));
    }

    private void printMeal(Meal meal) {
        System.out.printf("   %-50s %5d KZT%n", meal.getDescription(), meal.getPrice());
    }

    private void step(String text) {
        System.out.println();
        System.out.println("> " + text);
    }
}
