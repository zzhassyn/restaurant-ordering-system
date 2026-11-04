package com.restaurant.factory;

import com.restaurant.meal.Meal;
import com.restaurant.meal.Salad;

// Concrete Creator
public class SaladFactory implements MealFactory {

    @Override
    public Meal createMeal() {
        return new Salad();
    }
}
