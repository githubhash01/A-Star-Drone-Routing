package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;

public class Utils {

    // loop through all the restaurants and find the one that matches the order
    public static Restaurant getRestaurant(Restaurant[] restaurants, Order order) {
        // get a pizza from the order
        Pizza pizza = order.getPizzasInOrder()[0];
        // go through all the restaurants and find the one that matches the pizza
        for (Restaurant restaurant : restaurants) {
            for (Pizza restaurantPizza : restaurant.menu()) {
                if (pizza.equals(restaurantPizza)){
                    return restaurant;
                }
            }
        }

        // fail gracefully by printing something to the console
        System.out.println("No restaurant found for the order: " + order.getOrderNo());
        return null;
    }
}
