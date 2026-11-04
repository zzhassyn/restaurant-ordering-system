package com.restaurant.factory;

import com.restaurant.meal.Meal;

/**
 * Creator of the Factory Method. Each concrete factory decides which meal to
 * instantiate, so adding a new dish does not change the code that orders it.
 */
public interface MealFactory {

    Meal createMeal();
}
