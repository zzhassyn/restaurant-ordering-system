package com.restaurant.topping;

import com.restaurant.meal.Meal;

// Concrete Decorator
public class Mushrooms extends ToppingDecorator {

    public static final int PRICE = 450;

    public Mushrooms(Meal meal) {
        super(meal);
    }

    @Override
    public String getDescription() {
        return meal.getDescription() + ", mushrooms";
    }

    @Override
    public int getPrice() {
        return meal.getPrice() + PRICE;
    }
}
