package com.restaurant.factory;

import com.restaurant.meal.Burger;
import com.restaurant.meal.Meal;

// Concrete Creator
public class BurgerFactory implements MealFactory {

    @Override
    public Meal createMeal() {
        return new Burger();
    }
}
