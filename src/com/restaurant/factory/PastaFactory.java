package com.restaurant.factory;

import com.restaurant.meal.Meal;
import com.restaurant.meal.Pasta;

// Concrete Creator
public class PastaFactory implements MealFactory {

    @Override
    public Meal createMeal() {
        return new Pasta();
    }
}
