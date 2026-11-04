package com.restaurant.meal;

/**
 * Product of the Factory Method: every dish on the menu implements this interface,
 * so the rest of the system works with meals without knowing their concrete classes.
 */
public interface Meal {

    String getDescription();

    /** Price in tenge (KZT). */
    int getPrice();
}
