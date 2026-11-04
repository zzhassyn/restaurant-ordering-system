package com.restaurant.topping;

import com.restaurant.meal.Meal;

// Concrete Decorator
public class Bacon extends ToppingDecorator {

    public static final int PRICE = 600;

    public Bacon(Meal meal) {
        super(meal);
    }

    @Override
    public String getDescription() {
        return meal.getDescription() + ", bacon";
    }

    @Override
    public int getPrice() {
        return meal.getPrice() + PRICE;
    }
}
