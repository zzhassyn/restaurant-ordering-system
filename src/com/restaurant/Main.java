package com.restaurant;

import com.restaurant.facade.RestaurantFacade;
import com.restaurant.ui.ConsoleApp;
import com.restaurant.ui.DemoScenario;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        RestaurantFacade restaurant = new RestaurantFacade();
        if (args.length > 0 && args[0].equals("--demo")) {
            new DemoScenario(restaurant).run();
        } else {
            new ConsoleApp(restaurant, new Scanner(System.in)).run();
        }
    }
}
