package com.restaurant.topping;

import com.restaurant.meal.Meal;

import java.util.function.UnaryOperator;

/**
 * Toppings a customer can choose. Each one knows which decorator wraps the meal,
 * so callers can add toppings by choice instead of naming decorator classes.
 */
public enum Topping {

    EXTRA_CHEESE("Extra cheese", ExtraCheese.PRICE, ExtraCheese::new),
    BACON("Bacon", Bacon.PRICE, Bacon::new),
    JALAPENO("Jalapeno", Jalapeno.PRICE, Jalapeno::new),
    MUSHROOMS("Mushrooms", Mushrooms.PRICE, Mushrooms::new),
    EXTRA_SAUCE("Extra sauce", ExtraSauce.PRICE, ExtraSauce::new);

    private final String label;
    private final int price;
    private final UnaryOperator<Meal> decorator;

    Topping(String label, int price, UnaryOperator<Meal> decorator) {
        this.label = label;
        this.price = price;
        this.decorator = decorator;
    }

    public String getLabel() {
        return label;
    }

    public int getPrice() {
        return price;
    }

    public Meal applyTo(Meal meal) {
        return decorator.apply(meal);
    }
}
