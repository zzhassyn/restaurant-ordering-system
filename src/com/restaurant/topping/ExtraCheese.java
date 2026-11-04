package com.restaurant.topping;

import com.restaurant.meal.Meal;

// Concrete Decorator
public class ExtraCheese extends ToppingDecorator {

    public static final int PRICE = 400;

    public ExtraCheese(Meal meal) {
        super(meal);
    }

    @Override
    public String getDescription() {
        return meal.getDescription() + ", extra cheese";
    }

    @Override
    public int getPrice() {
        return meal.getPrice() + PRICE;
    }
}
