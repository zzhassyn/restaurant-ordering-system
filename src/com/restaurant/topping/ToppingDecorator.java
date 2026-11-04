package com.restaurant.topping;

import com.restaurant.meal.Meal;

/**
 * Base Decorator. It is-a Meal (same type as the dish it wraps) and has-a Meal
 * (the wrapped dish), so toppings can be stacked in any order at runtime
 * without creating a subclass for every combination.
 */
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
