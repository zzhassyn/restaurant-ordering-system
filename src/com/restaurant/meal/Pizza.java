package com.restaurant.meal;

// Concrete Product
public class Pizza implements Meal {

    @Override
    public String getDescription() {
        return "Margherita pizza";
    }

    @Override
    public int getPrice() {
        return 3500;
    }
}
