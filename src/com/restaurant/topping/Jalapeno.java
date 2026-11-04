package com.restaurant.topping;

import com.restaurant.meal.Meal;

// Concrete Decorator
public class Jalapeno extends ToppingDecorator {

    public static final int PRICE = 300;

    public Jalapeno(Meal meal) {
        super(meal);
    }

    @Override
    public String getDescription() {
        return meal.getDescription() + ", jalapeno";
    }

    @Override
    public int getPrice() {
        return meal.getPrice() + PRICE;
    }
}
