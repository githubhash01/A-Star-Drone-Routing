package uk.ac.ed.inf;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * System tests that run the app and then test the output files to see if they meet requirements
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
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
    public void testHover()
    {
        assertTrue( true );
    }

    //
    public void testDeliveriesInOrder(){

    }

    /**
     * Test that the drone flight path is feasible
     * 1. All moves are within the possible move distance
     * 2. All angles are within the possible angle range
     * 3. The drone starts and ends at appleton
     * 4. The drone does not fly through no-fly zones
     */
    public void testDroneFlightPathFeasible(){

    }


}
