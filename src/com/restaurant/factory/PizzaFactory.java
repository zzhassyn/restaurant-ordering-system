package com.restaurant.factory;

import com.restaurant.meal.Meal;
import com.restaurant.meal.Pizza;

// Concrete Creator
public class PizzaFactory implements MealFactory {

    @Override
    public Meal createMeal() {
        return new Pizza();
    }
}
