package uk.ac.ed.inf;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

/**
 * A file like AppTest to test the LngLatHandler class
 */
public class LngLatHandlerTest extends TestCase{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    // before running all tests create a new LngLatHandler object
    public LngLatHandler lngLatHandler = new LngLatHandler();

    public LngLatHandlerTest( String testName )
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
    public void testDistanceTo()
    {
        // test the distanceTo method
        // startPosition and endPosition
        LngLat startPosition = new LngLat(0, 0);
        LngLat endPosition = new LngLat(3, 4);
        // assert true if the distance between the two points is 5
        assertEquals(5.0, lngLatHandler.distanceTo(startPosition, endPosition));

    }
    public void testingIsCloseTo()
    {
        // test the isCloseTo method
        // startPosition and endPosition
        LngLat startPosition = new LngLat(0, 0);
        LngLat endPosition = new LngLat(0, 1.49E-4);
        // assert true as the distance between the two points is less than 1.5E-4
        assertTrue(lngLatHandler.isCloseTo(startPosition, endPosition));
        LngLat endPosition2 = new LngLat(0, 1.51E-4);
        // assert false as the distance between the two points is greater than 1.5E-4
        assertFalse(lngLatHandler.isCloseTo(startPosition, endPosition2));
    }

    public void testingLineIntercepts()
    {
        // two lines that don't intercept

        // create two LngLat objects
        LngLat lngLat1 = new LngLat(0.5, 0.5);
        LngLat lngLat2 = new LngLat(2.0, 0.5);

        LngLat lngLat3 = new LngLat(0.0, 0.0);
        LngLat lngLat4 = new LngLat(0.0, 1.0);
        // assert false as the two lines don't intersect
        double[][] ray = {{lngLat1.lng(), lngLat1.lat()}, {lngLat2.lng(), lngLat2.lat()}};
        double[][] edge = {{lngLat3.lng(), lngLat3.lat()}, {lngLat4.lng(), lngLat4.lat()}};

        assertFalse(lngLatHandler.lineIntersect(ray, edge));

        // checking line from centre of square to top far right and line from top of square to bottom right intercept

        LngLat lngLat5 = new LngLat(1.0, 1.0);
        LngLat lngLat6 = new LngLat(1.0, 0.0);
        // assert true as the two lines intersect
        assertTrue(lngLatHandler.lineIntersect(new double[][]{{lngLat1.lng(), lngLat1.lat()}, {lngLat2.lng(), lngLat2.lat()}}, new double[][]{{lngLat5.lng(), lngLat5.lat()}, {lngLat6.lng(), lngLat6.lat()}}));

    }

    public void testingRayCasting()
    {
        // test the isInRegion method
        // create a NamedRegion object
        NamedRegion namedRegion = new NamedRegion("simple rectangle", new LngLat[]{new LngLat(0, 0), new LngLat(0, 1), new LngLat(1, 1), new LngLat(1, 0)});
        // create a LngLat object
        LngLat lngLat = new LngLat(0.5, 0.5);
        // assert true as the point is inside the polygon
        assertEquals(1, lngLatHandler.rayCastIntercepts(lngLat, namedRegion, 2) % 2);

    }

    // testing easy in region for a simple polygon with 4 vertices and a point inside, on the edge and outside
    public void inRegion(){

    }

    // testing in region for a simple polygon with 10 vertices and a point inside, on the edge, and outside
    public void harderInRegion(){

    }


}
// TODO:
/**
 * Check if I have understood inRegion and the Region class correctly
 * ask if my implementation is on the right lines (no pun intended)
 */