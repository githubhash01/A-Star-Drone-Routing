package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;
import uk.ac.ed.inf.ilp.data.Pizza;

public class OrderValidator2 implements OrderValidation{
    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {

        // Process the Payment

        // Process the Order Specifications
        Pizza[] pizzas = orderToValidate.getPizzasInOrder();

        int calculatedTotal = 0;
        int pizzasInOrder = pizzas.length;
        int definedPizzasCount = 0;
        int restaurantsInOrder = 0;
        boolean restaurantClosedToday = false;






        // TOTAL INCORRECT
        if (calculatedTotal != 0){
            orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
            return orderToValidate;
        }

        // PIZZA UNDEFINED
        if (definedPizzasCount < pizzasInOrder){
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
            return orderToValidate;
        }

        // MULTIPLE RESTAURANTS
        if (restaurantsInOrder > 1){
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
            return orderToValidate;
        }

        // RESTAURANT CLOSED
        if (restaurantClosedToday){
            orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
            return orderToValidate;
        }

        return null;
    }
}
