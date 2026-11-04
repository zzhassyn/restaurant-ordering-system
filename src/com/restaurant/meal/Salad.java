package com.restaurant.meal;

// Concrete Product
public class Salad implements Meal {

    @Override
    public String getDescription() {
        return "Caesar salad";
    }

    @Override
    public int getPrice() {
        return 2200;
    }
}
