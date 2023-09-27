package uk.ac.ed.inf;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

// importing order
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.Pizza;

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

    // testing OrderValidationCode possible errors

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
        // order validator returns an order with a defined order validation code
        Order validatedOrder = orderValidator.validateOrder(orderCardNumberShort, null);

        // assert that the order validation code is CARD_NUMBER_INVALID
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
        // order validator returns an order with a defined order validation code
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
        // order validator returns an order with a defined order validation code
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
        // order validator returns an order with a defined order validation code
        Order validatedOrder = orderValidator.validateOrder(orderExpiryBeforeOrder, null);
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, validatedOrder.getOrderValidationCode());

        // testing an expiry date in the same month as the order date, but before the order date
        Order orderExpirySameMonthBeforeOrder = new Order();
        orderExpirySameMonthBeforeOrder.setOrderDate(LocalDate.of(2023, 9, 1));
        orderExpirySameMonthBeforeOrder.setCreditCardInformation(
                new CreditCardInformation(
                        "1212121212121212",
                        "09/23",
                        "222"
                )
        );
        OrderValidator orderValidator2 = new OrderValidator();
        // order validator returns an order with a defined order validation code
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
        // order validator returns an order with a defined order validation code
        Order validatedOrder = orderValidator.validateOrder(orderCVVInvalid, null);
        assertEquals(OrderValidationCode.CVV_INVALID, validatedOrder.getOrderValidationCode());
    }

    public void testingTotalIncorrect(){
        // making sure the total is greater than deliver fee
        // generating order
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
        // adding pizza to order
        orderTotalIncorrect.setPizzasInOrder(new Pizza[]{pizza1, pizza2, pizza3, pizza4});
        // setting total price
        orderTotalIncorrect.setPriceTotalInPence(-1 + SystemConstants.ORDER_CHARGE_IN_PENCE);
        // generating order validator
        OrderValidator orderValidator = new OrderValidator();
        // order validator returns an order with a defined order validation code
        Order validatedOrder = orderValidator.validateOrder(orderTotalIncorrect, null);
        assertEquals(OrderValidationCode.TOTAL_INCORRECT, validatedOrder.getOrderValidationCode());
        // testing that total of all pizza costs + delivery fee = total price

    }

    public void testingPizzaUndefined(){

    }

    // Restaurant Related Checks
    public void testingPizzaFromMultipleRestaurants(){

    }

    public void testingRestaurantClosed(){

    }

    public void testingGoodOrder(){

    }

}
