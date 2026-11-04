package com.restaurant.factory;

import com.restaurant.meal.Meal;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The restaurant menu: maps each menu number to the factory that creates that dish.
 * Clients choose a dish by its number and never call a meal constructor themselves.
 */
public class Menu {

    private final Map<Integer, MealFactory> factories = new LinkedHashMap<>();

    public Menu() {
        factories.put(1, new BurgerFactory());
        factories.put(2, new PizzaFactory());
        factories.put(3, new PastaFactory());
        factories.put(4, new SaladFactory());
    }

    public Meal createMeal(int menuNumber) {
        MealFactory factory = factories.get(menuNumber);
        if (factory == null) {
            throw new IllegalArgumentException("There is no dish with menu number " + menuNumber);
        }
        return factory.createMeal();
    }

    public Set<Integer> getMenuNumbers() {
        return Collections.unmodifiableSet(factories.keySet());
    }
}
