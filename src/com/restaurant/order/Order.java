package com.restaurant.order;

import com.restaurant.meal.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Product of the Builder. An order has several optional parts (table number,
 * delivery address, notes) that depend on its type, so instead of a constructor
 * with many parameters it is assembled step by step with {@link Order.Builder}.
 */
public class Order {

    private static int nextId = 1;

    private final int id;
    private final String customerName;
    private final OrderType orderType;
    private final int tableNumber;
    private final String deliveryAddress;
    private final List<Meal> meals;
    private final String notes;

    private Order(Builder builder) {
        this.id = nextId++;
        this.customerName = builder.customerName;
        this.orderType = builder.orderType;
        this.tableNumber = builder.tableNumber;
        this.deliveryAddress = builder.deliveryAddress;
        this.meals = List.copyOf(builder.meals);
        this.notes = builder.notes;
    }

    public int getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public String getNotes() {
        return notes;
    }

    public int getTotalPrice() {
        int total = 0;
        for (Meal meal : meals) {
            total += meal.getPrice();
        }
        return total;
    }

    /** Short human-readable destination, e.g. "Dine-in, table 5". */
    public String describeDestination() {
        return switch (orderType) {
            case DINE_IN -> orderType.getLabel() + ", table " + tableNumber;
            case TAKEAWAY -> orderType.getLabel();
            case DELIVERY -> orderType.getLabel() + " to " + deliveryAddress;
        };
    }

    // Builder
    public static class Builder {

        private String customerName;
        private OrderType orderType = OrderType.DINE_IN;
        private int tableNumber;
        private String deliveryAddress = "";
        private final List<Meal> meals = new ArrayList<>();
        private String notes = "";

        public Builder setCustomerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public Builder setOrderType(OrderType orderType) {
            this.orderType = Objects.requireNonNull(orderType, "orderType");
            return this;
        }

        public Builder setTableNumber(int tableNumber) {
            this.tableNumber = tableNumber;
            return this;
        }

        public Builder setDeliveryAddress(String deliveryAddress) {
            this.deliveryAddress = Objects.requireNonNull(deliveryAddress, "deliveryAddress").trim();
            return this;
        }

        public Builder addMeal(Meal meal) {
            meals.add(Objects.requireNonNull(meal, "meal"));
            return this;
        }

        public Builder setNotes(String notes) {
            this.notes = Objects.requireNonNull(notes, "notes").trim();
            return this;
        }

        public Order build() {
            validate();
            return new Order(this);
        }

        private void validate() {
            if (customerName == null || customerName.isBlank()) {
                throw new IllegalStateException("Customer name is required");
            }
            if (meals.isEmpty()) {
                throw new IllegalStateException("An order must contain at least one meal");
            }
            if (orderType == OrderType.DINE_IN && tableNumber <= 0) {
                throw new IllegalStateException("A dine-in order needs a table number");
            }
            if (orderType == OrderType.DELIVERY && deliveryAddress.isEmpty()) {
                throw new IllegalStateException("A delivery order needs an address");
            }
        }
    }
}
