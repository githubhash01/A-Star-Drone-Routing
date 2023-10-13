package uk.ac.ed.inf;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

// importing order
import uk.ac.ed.inf.ilp.data.*;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class OrderValidatorTest
        extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public OrderValidatorTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( OrderValidatorTest.class );
    }

    public void testingCardNumberInvalid(){
        // card number must be a string, with exactly 16 digits
        Order orderCardNumberShort = new Order();
        orderCardNumberShort.setCreditCardInformation(
                new CreditCardInformation(
                        "1212",
                        "12/12",
                        "222"
                )
        );
        OrderValidator orderValidator = new OrderValidator();
        Order validatedOrder = orderValidator.validateOrder(orderCardNumberShort, null);

        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, validatedOrder.getOrderValidationCode());

        // card number too long
        Order orderCardNumberLong = new Order();
        orderCardNumberLong.setCreditCardInformation(
                new CreditCardInformation(
                        "12121212121212121",
                        "12/12",
                        "222"
                )
        );
        OrderValidator orderValidator2 = new OrderValidator();
        Order validatedOrder2 = orderValidator2.validateOrder(orderCardNumberLong, null);
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, validatedOrder2.getOrderValidationCode());

        // card number is not entirely digits
        Order orderCardNumberAlpha = new Order();
        orderCardNumberAlpha.setCreditCardInformation(
                new CreditCardInformation(
                        "1212a121212121212",
                        "12/12",
                        "222"
                )
        );
        OrderValidator orderValidator3 = new OrderValidator();
        Order validatedOrder3 = orderValidator3.validateOrder(orderCardNumberAlpha, null);
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, validatedOrder3.getOrderValidationCode());

    }
    public void testingExpiryDateInvalid(){
        // creating an order with an expiry date before the order date
        Order orderExpiryBeforeOrder = new Order();
        orderExpiryBeforeOrder.setOrderDate(LocalDate.of(2023, 9, 1));
        orderExpiryBeforeOrder.setCreditCardInformation(
                new CreditCardInformation(
                        "1212121212121212",
                        "12/22",
                        "222"
                )
        );
        OrderValidator orderValidator = new OrderValidator();
        Order validatedOrder = orderValidator.validateOrder(orderExpiryBeforeOrder, null);
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, validatedOrder.getOrderValidationCode());

        Order orderExpirySameMonthBeforeOrder = new Order();
        orderExpirySameMonthBeforeOrder.setOrderDate(LocalDate.of(2023, 9, 1));
        orderExpirySameMonthBeforeOrder.setCreditCardInformation(
                new CreditCardInformation(
                        "1212121212121212",
                        "08/23",
                        "222"
                )
        );
        OrderValidator orderValidator2 = new OrderValidator();
        Order validatedOrder2 = orderValidator2.validateOrder(orderExpirySameMonthBeforeOrder, null);
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, validatedOrder2.getOrderValidationCode());
    }

    public void testingCVVInvalid(){
        // making an order with CVV that is invalid
        Order orderCVVInvalid = new Order();
        orderCVVInvalid.setOrderDate(LocalDate.of(2023, 9, 1));
        orderCVVInvalid.setCreditCardInformation(
                new CreditCardInformation(
                        "1212121212121212",
                        "09/28",
                        "22"
                )
        );
        OrderValidator orderValidator = new OrderValidator();
        Order validatedOrder = orderValidator.validateOrder(orderCVVInvalid, null);
        assertEquals(OrderValidationCode.CVV_INVALID, validatedOrder.getOrderValidationCode());
    }

    public void testingTotalIncorrect(){
        Order orderTotalIncorrect = new Order();
        orderTotalIncorrect.setOrderDate(LocalDate.of(2023, 9, 1));
        orderTotalIncorrect.setCreditCardInformation(
                new CreditCardInformation(
                        "1212121212121212",
                        "09/28",
                        "222"
                )
        );
        // generating pizza
        Pizza pizza1 = new Pizza("Pizza A", 1000);
        Pizza pizza2 = new Pizza("Pizza B", 2000);
        Pizza pizza3 = new Pizza("Pizza C", 3000);
        Pizza pizza4 = new Pizza("Pizza D", 4000);
        orderTotalIncorrect.setPizzasInOrder(new Pizza[]{pizza1, pizza2, pizza3, pizza4});
        orderTotalIncorrect.setPriceTotalInPence(-1 + SystemConstants.ORDER_CHARGE_IN_PENCE);
        OrderValidator orderValidator = new OrderValidator();
        Order validatedOrder = orderValidator.validateOrder(orderTotalIncorrect, null);
        assertEquals(OrderValidationCode.TOTAL_INCORRECT, validatedOrder.getOrderValidationCode());

    }

    public void testingPizzaRestaurant(){
        // creating an order where one of the pizzas is undefined
        Order orderPizzaUndefined = new Order();
        orderPizzaUndefined.setOrderDate(LocalDate.of(2023, 10, 12));
        orderPizzaUndefined.setCreditCardInformation(
                new CreditCardInformation(
                        "1212121212121212",
                        "09/28",
                        "222"
                )
        );
        // generating pizza
        Pizza pizza1 = new Pizza("Pizza A", 1000);
        Pizza pizza2 = new Pizza("Pizza B", 2000);
        Pizza pizza3 = new Pizza("Pizza C", 3000);
        Pizza pizza4 = new Pizza("Pizza D", 4000);

        orderPizzaUndefined.setPizzasInOrder(new Pizza[]{pizza1, pizza2});
        LngLat restaurantLocation = new LngLat(-3.184319, 55.942617);
        DayOfWeek[] openingDays = new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY};
        Restaurant restaurant1 = new Restaurant("Restaurant A", restaurantLocation, openingDays, new Pizza[]{pizza3, pizza2});
        Restaurant restaurant2 = new Restaurant("Restaurant B", restaurantLocation, openingDays, new Pizza[]{pizza1, pizza4});

        Restaurant[] restaurants = new Restaurant[]{restaurant2, restaurant1};
        orderPizzaUndefined.setPriceTotalInPence(3000 + SystemConstants.ORDER_CHARGE_IN_PENCE);
        OrderValidator orderValidator = new OrderValidator();
        Order validatedOrder = orderValidator.validateOrder(orderPizzaUndefined, restaurants);
        assertEquals(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, validatedOrder.getOrderValidationCode());

    }

}
