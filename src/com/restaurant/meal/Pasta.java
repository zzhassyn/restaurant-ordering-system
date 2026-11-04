package com.restaurant.meal;

// Concrete Product
public class Pasta implements Meal {

    @Override
    public String getDescription() {
        return "Pasta carbonara";
    }

    @Override
    public int getPrice() {
        return 3200;
    }
}
