package com.restaurant.topping;

import com.restaurant.meal.Meal;


public abstract class ToppingDecorator implements Meal {

    protected final Meal meal;

    protected ToppingDecorator(Meal meal) {
        this.meal = meal;
    }

    @Override
    public String getDescription() {
        return meal.getDescription();
    }

    @Override
    public int getPrice() {
        return meal.getPrice();
    }
}
