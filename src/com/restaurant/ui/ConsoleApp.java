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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Interactive console client. All restaurant logic goes through {@link RestaurantFacade};
 * this class only reads input and prints results.
 */
public class ConsoleApp {

    private static final String LINE = "------------------------------------------------------------";

    private final RestaurantFacade restaurant;
    private final Scanner scanner;

    public ConsoleApp(RestaurantFacade restaurant, Scanner scanner) {
        this.restaurant = restaurant;
        this.scanner = scanner;
    }

    public void run() {
        System.out.println("=== Restaurant Ordering System ===");
        try {
            boolean running = true;
            while (running) {
                printMainMenu();
                running = handle(readInt("Choose an option: "));
            }
        } catch (NoSuchElementException e) {
            System.out.println();
            System.out.println("Input closed.");
        }
        System.out.println("Goodbye!");
    }

    private void printMainMenu() {
        System.out.println(LINE);
        System.out.println(" 1. Show menu");
        System.out.println(" 2. New order");
        System.out.println(" 3. Show orders");
        System.out.println(" 4. Advance order status (kitchen)");
        System.out.println(" 5. Pay for an order");
        System.out.println(" 6. Cancel an order");
        System.out.println(" 7. Run demo scenario");
        System.out.println(" 0. Exit");
    }

    private boolean handle(int option) {
        try {
            switch (option) {
                case 1 -> ConsoleFormat.printMenu(restaurant);
                case 2 -> createOrder();
                case 3 -> printOrders();
                case 4 -> restaurant.advanceOrder(readInt("Order number: "));
                case 5 -> payForOrder();
                case 6 -> restaurant.cancelOrder(readInt("Order number: "));
                case 7 -> new DemoScenario(restaurant).run();
                case 0 -> {
                    return false;
                }
                default -> System.out.println("  ! Unknown option " + option);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("  ! " + e.getMessage());
        }
        return true;
    }

    private void createOrder() {
        Order.Builder builder = new Order.Builder()
                .setCustomerName(readNonBlank("Customer name: "));
        OrderType orderType = readOrderType();
        builder.setOrderType(orderType);
        if (orderType == OrderType.DINE_IN) {
            builder.setTableNumber(readInt("Table number: ", 1, 99));
        } else if (orderType == OrderType.DELIVERY) {
            builder.setDeliveryAddress(readNonBlank("Delivery address: "));
        }
        addMeals(builder);
        builder.setNotes(readLine("Notes for the kitchen (Enter to skip): "));
        restaurant.placeOrder(builder.build());
    }

    private OrderType readOrderType() {
        OrderType[] types = OrderType.values();
        for (int i = 0; i < types.length; i++) {
            System.out.printf("   %d. %s%n", i + 1, types[i].getLabel());
        }
        return types[readInt("Order type: ", 1, types.length) - 1];
    }

    private void addMeals(Order.Builder builder) {
        Map<Integer, Meal> menuItems = restaurant.getMenuItems();
        ConsoleFormat.printMenu(restaurant);
        while (true) {
            int number = readInt("Dish number (0 to finish): ");
            if (number == 0) {
                return;
            }
            if (!menuItems.containsKey(number)) {
                System.out.println("  ! There is no dish with menu number " + number);
                continue;
            }
            Meal meal = restaurant.createMeal(number, readToppings());
            builder.addMeal(meal);
            System.out.printf("  + %s - %d KZT%n", meal.getDescription(), meal.getPrice());
        }
    }

    private List<Topping> readToppings() {
        while (true) {
            String input = readLine("Topping numbers, comma-separated (Enter for none): ");
            try {
                return parseToppings(input);
            } catch (IllegalArgumentException e) {
                System.out.println("  ! " + e.getMessage());
            }
        }
    }

    private List<Topping> parseToppings(String input) {
        List<Topping> available = restaurant.getToppings();
        List<Topping> chosen = new ArrayList<>();
        if (input.isBlank()) {
            return chosen;
        }
        for (String part : input.split(",")) {
            int number = parseNumber(part.trim());
            if (number < 1 || number > available.size()) {
                throw new IllegalArgumentException("There is no topping with number " + number);
            }
            chosen.add(available.get(number - 1));
        }
        return chosen;
    }

    private void printOrders() {
        List<Order> orders = restaurant.getOrders();
        if (orders.isEmpty()) {
            System.out.println("  No orders yet");
        }
        for (Order order : orders) {
            ConsoleFormat.printOrder(order);
        }
    }

    private void payForOrder() {
        Order order = restaurant.findOrder(readInt("Order number: "));
        if (!order.isAwaitingPayment()) {
            System.out.println("  ! Order #" + order.getId() + " is already paid or cancelled");
            return;
        }
        System.out.printf("  Total for order #%d: %d KZT%n", order.getId(), order.getTotalPrice());
        boolean paid = restaurant.payOrder(order.getId(), readPaymentMethod());
        System.out.println(paid ? "  Payment successful" : "  Payment failed, try another method");
    }

    private PaymentStrategy readPaymentMethod() {
        System.out.println("   1. Cash");
        System.out.println("   2. Card");
        System.out.println("   3. QR code");
        return switch (readInt("Payment method: ", 1, 3)) {
            case 1 -> new CashPayment(readInt("Cash given (KZT): ", 0, Integer.MAX_VALUE));
            case 2 -> new CardPayment(readLine("Card number (16 digits): "));
            default -> new QrPayment(readLine("Phone number: "));
        };
    }

    private int readInt(String prompt) {
        while (true) {
            try {
                return parseNumber(readLine(prompt).trim());
            } catch (IllegalArgumentException e) {
                System.out.println("  ! " + e.getMessage());
            }
        }
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.printf("  ! Please enter a number from %d to %d%n", min, max);
        }
    }

    private String readNonBlank(String prompt) {
        while (true) {
            String value = readLine(prompt).trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("  ! This field cannot be empty");
        }
    }

    /** Throws NoSuchElementException when the input stream is closed. */
    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static int parseNumber(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + text + "' is not a number");
        }
    }
}
