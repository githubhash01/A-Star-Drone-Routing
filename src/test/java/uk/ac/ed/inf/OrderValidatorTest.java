package uk.ac.ed.inf;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

// importing order
import uk.ac.ed.inf.ilp.data.Order;

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

    // creating an order object
    Order order = new Order();

    // testing OrderValidationCode possible errors

    public void testingCardNumberInvalid(){
        // testing card number is valid
        // card number must be a string containing only digits and be 16 digits long (no spaces)

    }
    public void testingExpiryDateInvalid(){

    }

    public void testingCVVInvalid(){

    }

    public void testingTotalIncorrect(){

    }

    public void testingPizzaUndefined(){

    }

    // Restaurant Related Checks
    public void testingPizzaFromMultipleRestaurants(){

    }

    public void testingRestaurantClosed(){

    }

}
