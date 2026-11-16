package com.restaurant.facade;

import com.restaurant.factory.Menu;
import com.restaurant.meal.Meal;
import com.restaurant.observer.CustomerNotifier;
import com.restaurant.observer.KitchenDisplay;
import com.restaurant.order.Order;
import com.restaurant.order.OrderStatus;
import com.restaurant.payment.Checkout;
import com.restaurant.payment.PaymentStrategy;
import com.restaurant.topping.Topping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class RestaurantFacade {

    private final Menu menu = new Menu();
    private final KitchenDisplay kitchenDisplay = new KitchenDisplay();
    private final Checkout checkout = new Checkout();
    private final Map<Integer, Order> orders = new LinkedHashMap<>();

    public Map<Integer, Meal> getMenuItems() {
        Map<Integer, Meal> items = new LinkedHashMap<>();
        for (int number : menu.getMenuNumbers()) {
            items.put(number, menu.createMeal(number));
        }
        return items;
    }

    public List<Topping> getToppings() {
        return List.of(Topping.values());
    }

    public Meal createMeal(int menuNumber, List<Topping> toppings) {
        Meal meal = menu.createMeal(menuNumber);
        for (Topping topping : toppings) {
            meal = topping.applyTo(meal);
        }
        return meal;
    }

    public Order placeOrder(Order order) {
        if (orders.containsKey(order.getId())) {
            throw new IllegalStateException("Order #" + order.getId() + " is already placed");
        }
        order.addObserver(kitchenDisplay);
        order.addObserver(new CustomerNotifier(order.getCustomerName()));
        orders.put(order.getId(), order);
        order.place();
        return order;
    }

    public void advanceOrder(int orderId) {
        Order order = findOrder(orderId);
        if (order.getStatus() == OrderStatus.READY && !order.isPaid()) {
            throw new IllegalStateException("Order #" + orderId + " must be paid before it can be completed");
        }
        order.advanceStatus();
    }

    public void cancelOrder(int orderId) {
        findOrder(orderId).cancel();
    }

    public boolean payOrder(int orderId, PaymentStrategy paymentStrategy) {
        Order order = findOrder(orderId);
        if (!order.isAwaitingPayment()) {
            throw new IllegalStateException("Order #" + orderId + " is already paid or cancelled");
        }
        checkout.setPaymentStrategy(paymentStrategy);
        boolean successful = checkout.processPayment(order.getTotalPrice());
        if (successful) {
            order.markAsPaid();
        }
        return successful;
    }

    public Order findOrder(int orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order #" + orderId + " was not found");
        }
        return order;
    }

    public List<Order> getOrders() {
        return List.copyOf(orders.values());
    }
}
