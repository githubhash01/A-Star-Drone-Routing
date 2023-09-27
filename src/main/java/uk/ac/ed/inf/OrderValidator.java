package uk.ac.ed.inf;

// importing OrderValidation interface
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.Pizza;
// import system constants
import uk.ac.ed.inf.ilp.constant.SystemConstants;


public class OrderValidator implements OrderValidation {
    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {
        // 1. Card Number
        String cardNumber = orderToValidate.getCreditCardInformation().getCreditCardNumber();
        if (!validCardNumber(cardNumber)) {
            // update the order validation status
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
            return orderToValidate;
        }

        // 2. Expiry date
        String orderExpiryDate = orderToValidate.getCreditCardInformation().getCreditCardExpiry();
        String orderDate = orderToValidate.getOrderDate().toString();
        if (!validExpiry(orderExpiryDate, orderDate)) {
            // update the order validation status
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            return orderToValidate;
        }

        // 3. CVV Number
        String CVV = orderToValidate.getCreditCardInformation().getCvv();
        if (!validCVV(CVV)) {
            // update the order validation status
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
            return orderToValidate;
        }

        // 4. Total Price
        Pizza[] pizzas = orderToValidate.getPizzasInOrder();
        int priceTotalInPence = orderToValidate.getPriceTotalInPence();
        if (!correctTotalPrice(pizzas, priceTotalInPence)) {
            // update the order validation status
            orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
            return orderToValidate;
        }
        // 5. Defined Pizzas

        // 6. Excessive Pizza

        // 7. Multiple Restaurants

        // 8. Restaurant Opening Hours


        // update the order validation status
        orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        return orderToValidate;
    }

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

    public boolean correctTotalPrice(Pizza[] pizzas,  int priceTotalInPence){
        int calculatedTotal = 0;
        // iterate through the pizzas and add up the prices
        for (Pizza pizza : pizzas) {
            calculatedTotal += pizza.priceInPence();
        }
        // add the delivery charge form the system constants
        calculatedTotal += SystemConstants.ORDER_CHARGE_IN_PENCE;
        // check if the total price is equal to priceTotalInPence
        return calculatedTotal == priceTotalInPence;
    }
}