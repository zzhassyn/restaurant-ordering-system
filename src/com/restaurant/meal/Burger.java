package com.restaurant.meal;

// Concrete Product
public class Burger implements Meal {

    @Override
    public String getDescription() {
        return "Beef burger";
    }

    @Override
    public int getPrice() {
        return 2500;
    }
}
