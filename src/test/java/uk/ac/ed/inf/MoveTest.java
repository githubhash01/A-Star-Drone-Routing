package uk.ac.ed.inf;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ed.inf.ilp.data.LngLat;

public class MoveTest
        extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MoveTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testAngle()
    {
        // test the angle calculation

        LngLatHandler lngLatHandler = new LngLatHandler();
        LngLat from = new LngLat(-3.188396, 55.944425);
        for (double angle = 0; angle < 360; angle += 22.5){
            LngLat to = lngLatHandler.nextPosition(from, angle);
            // create a move
            Move move = new Move("12345678", from, to);
            // get the angle
            double calculatedAngle = move.angle;
            assertEquals(angle, calculatedAngle);
        }
    }
}
