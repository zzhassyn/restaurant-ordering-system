package com.restaurant.topping;

import com.restaurant.meal.Meal;

// Concrete Decorator
public class ExtraSauce extends ToppingDecorator {

    public static final int PRICE = 250;

    public ExtraSauce(Meal meal) {
        super(meal);
    }

    @Override
    public String getDescription() {
        return meal.getDescription() + ", extra sauce";
    }

    @Override
    public int getPrice() {
        return meal.getPrice() + PRICE;
    }
}
