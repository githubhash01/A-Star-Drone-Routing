package uk.ac.ed.inf;

// Imports from the ILP starter code
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.constant.SystemConstants;

// Utility Imports
import java.time.DayOfWeek;
import java.util.Arrays;


public class OrderValidator implements OrderValidation {
    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {
        // 1. Card Number
        String cardNumber = orderToValidate.getCreditCardInformation().getCreditCardNumber();
        if (!validCardNumber(cardNumber)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
            return orderToValidate;
        }

        // 2. Expiry date
        String orderExpiryDate = orderToValidate.getCreditCardInformation().getCreditCardExpiry();
        String orderDate = orderToValidate.getOrderDate().toString();
        if (!validExpiry(orderExpiryDate, orderDate)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            return orderToValidate;
        }

        // 3. CVV Number
        String CVV = orderToValidate.getCreditCardInformation().getCvv();
        if (!validCVV(CVV)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
            return orderToValidate;
        }

        // 4. Total Price
        Pizza[] pizzas = orderToValidate.getPizzasInOrder();
        int priceTotalInPence = orderToValidate.getPriceTotalInPence();
        if (incorrectTotalPrice(pizzas, priceTotalInPence)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
            return orderToValidate;
        }

        // 5. Excessive Pizzas
        if (excessivePizza(pizzas)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
            return orderToValidate;
        }

        // array of pizza locations using the indices of the definedRestaurants array for each pizza in the order
        int[] pizzaLocations = findPizzaRestaurants(pizzas, definedRestaurants);

        // 6. Defined Pizzas
        if (undefinedPizzas(pizzaLocations)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
            return orderToValidate;
        }

        // 7. Multiple Restaurants
        if (multipleRestaurants(pizzaLocations)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
            return orderToValidate;
        }

        Restaurant restaurant = definedRestaurants[pizzaLocations[0]]; // by now we can safely assume that there is only one restaurant
        // 8. Restaurant Opening Hours
        if (restaurantClosed(restaurant, orderToValidate)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
            return orderToValidate;
        }

        // Otherwise the order is valid
        orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        return orderToValidate;
    }

    /**
     * HELPER FUNCTIONS
     */
    public boolean validCardNumber(String cardNumber) {
        // check if the cardNumber is length 16
        if (cardNumber.length() != 16) {
            return false;
        }
        // check if any characters in the cardNumber are not digits
        for (int i = 0; i < cardNumber.length(); i++) {
            if (!Character.isDigit(cardNumber.charAt(i))) {
                return false;
            }
        }
        // check if any of the characters in the cardNumber are less than 0
        for (int i = 0; i < cardNumber.length(); i++) {
            if (cardNumber.charAt(i) < '0') {
                return false;
            }
        }
        return true;
    }

    public boolean validExpiry(String expiryDate, String orderDate) {
        // check if the expiry date occurs or on the order date
        // orderDate is in YYYY-MM-DD format whereas expiryDate is in MM/YY format so parsing necessary
        String[] orderDateSplit = orderDate.split("-");
        int orderYear = Integer.parseInt(orderDateSplit[0]);
        int orderMonth = Integer.parseInt(orderDateSplit[1]);
        // extract the month and year from expiryDate
        String[] expiryDateSplit = expiryDate.split("/");
        int expiryYear = 2000 + Integer.parseInt(expiryDateSplit[1]); // add 2000 to the year
        int expiryMonth = Integer.parseInt(expiryDateSplit[0]);

        // check that the month is between 1 and 12
        if (expiryMonth < 1 || expiryMonth > 12) {
            return false;
        }
        // check that the year is between 2000 and 2099
        if (expiryYear < 2000 || expiryYear > 2099) {
            return false;
        }
        // check if the expiry date is not before or on the order date
        return expiryYear >= orderYear && (expiryYear != orderYear || expiryMonth > orderMonth);
    }

    public boolean validCVV(String CVV){
        // check if the CVV is length 3
        if (CVV.length() != 3) {
            return false;
        }
        // check if any characters in the CVV are not digits
        for (int i = 0; i < CVV.length(); i++) {
            if (!Character.isDigit(CVV.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean incorrectTotalPrice(Pizza[] pizzas, int priceTotalInPence){
        int calculatedTotal = 0;
        // iterate through the pizzas and add up the prices
        for (Pizza pizza : pizzas) {
            calculatedTotal += pizza.priceInPence();
        }
        // add the delivery charge form the system constants
        calculatedTotal += SystemConstants.ORDER_CHARGE_IN_PENCE;
        // check if the total price is equal to priceTotalInPence
        return calculatedTotal != priceTotalInPence;
    }

    public boolean excessivePizza(Pizza[] pizzas) {
        if (pizzas.length == 0) {
            // throw an error if there are no pizzas
            throw new IllegalArgumentException("There are no pizzas in the order");
        }
        // An order can have a minimum of one pizza, and a maximum of four.
        return pizzas.length > 4;
    }

    public int[] findPizzaRestaurants(Pizza[] pizzas, Restaurant[] restaurants){
        int P = pizzas.length;
        int R = restaurants.length;
        // defining the pizza locations to all be '-1' initially
        int[] pizzaLocations = new int[P];
        Arrays.fill(pizzaLocations, -1);

        // iterate through the pizzas for every restaurant
        for (int pizza_index=0; pizza_index<P; pizza_index++){
            for (int restaurant_index=0; restaurant_index<R; restaurant_index++){
                // iterate through the pizzas in the menu of the restaurant
                for (Pizza pizza : restaurants[restaurant_index].menu()) {
                    // if the pizza is found in the menu of the restaurant, then add restaurant index to pizzaLocations
                    if (pizzas[pizza_index].equals(pizza)) {
                        pizzaLocations[pizza_index] = restaurant_index;
                        break;
                    }
                    }
                }
            }
        return pizzaLocations;
    }

    // A pizza is considered 'defined' if it is listed in the menu of at least one restaurant.
    public boolean undefinedPizzas(int[] pizzaLocations) {
        // if there are any remaining negative index values in pizzaLocations, then there are undefined pizzas
        for (int pizzaLocation : pizzaLocations) {
            if (pizzaLocation < 0) {
                return true;
            }
        }
        return true;
    }

    public boolean multipleRestaurants(int[] pizzaLocations){
        // if the pizzaLocations array contains more than one unique value, then there are multiple restaurants
        int[] uniquePizzaLocations = Arrays.stream(pizzaLocations).distinct().toArray();
        return uniquePizzaLocations.length > 1;
    }

    public boolean restaurantClosed(Restaurant restaurant, Order order){
        // check if the restaurant is closed on the day of the order
        DayOfWeek orderDay = order.getOrderDate().getDayOfWeek();
        DayOfWeek[] openingDays = restaurant.openingDays();
        return !Arrays.asList(openingDays).contains(orderDay);
    }
}
