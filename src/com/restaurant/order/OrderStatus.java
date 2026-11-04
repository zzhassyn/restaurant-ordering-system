package com.restaurant.order;

/** Lifecycle of an order: NEW -> PLACED -> PREPARING -> READY -> COMPLETED, or CANCELLED. */
public enum OrderStatus {

    NEW,
    PLACED,
    PREPARING,
    READY,
    COMPLETED,
    CANCELLED;

    public OrderStatus next() {
        return switch (this) {
            case NEW -> PLACED;
            case PLACED -> PREPARING;
            case PREPARING -> READY;
            case READY -> COMPLETED;
            case COMPLETED, CANCELLED -> throw new IllegalStateException("The order is already " + this);
        };
    }

    public boolean isCancellable() {
        return this == NEW || this == PLACED || this == PREPARING;
    }
}
