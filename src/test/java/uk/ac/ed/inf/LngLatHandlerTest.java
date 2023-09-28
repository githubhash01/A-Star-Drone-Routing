package uk.ac.ed.inf;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import com.google.gson.*;


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

    public void testingSimpleRectangle()
    {
        // test the isInRegion method
        // create a NamedRegion object
        NamedRegion namedRegion = new NamedRegion("simple rectangle", new LngLat[]{new LngLat(0, 0), new LngLat(0, 1), new LngLat(1, 1), new LngLat(1, 0)});
        // create a LngLat object
        LngLat lngLat = new LngLat(0.5, 0.5);
        // assert true as the point is inside the polygon
        assertTrue(lngLatHandler.isInRegion(lngLat, namedRegion));

    }

    public void testingGeorgeSquare(){
        // creating a NamedRegion object for George Square
        NamedRegion georgeSquare = new NamedRegion("George Square Area",
                new LngLat[]{
                        new LngLat(-3.190578818321228, 55.94402412577528),
                        new LngLat(-3.1899887323379517, 55.94284650540911),
                        new LngLat(-3.187097311019897, 55.94328811724263),
                        new LngLat(-3.187682032585144, 55.944477740393744),
                        new LngLat(-3.190578818321228, 55.94402412577528)});

        // creating a LngLat object at a point inside George Square
        LngLat insideGeorgeSquare = new LngLat(-3.189, 55.943);
        // assert true as the point is inside the polygon
        assertTrue(lngLatHandler.isInRegion(insideGeorgeSquare, georgeSquare));

        // defining a point that is just outside George Square
        LngLat justOutsideGeorgeSquare = new LngLat(-3.1901,55.943);
        // assert false as the point is outside the polygon
        assertFalse(lngLatHandler.isInRegion(justOutsideGeorgeSquare, georgeSquare));
    }

    public void testingBayesCenter(){
        // creating a NamedRegion object for Bayes Center
        NamedRegion bayesCenter = new NamedRegion("Bayes Central Area",
                new LngLat[]{
                        new LngLat(-3.1876927614212036, 55.94520696732767),
                        new LngLat(-3.187555968761444, 55.9449621408666),
                        new LngLat(-3.186981976032257, 55.94505676722831),
                        new LngLat(-3.1872327625751495, 55.94536993377657),
                        new LngLat(-3.1874459981918335, 55.9453361389472),
                        new LngLat(-3.1873735785484314, 55.94519344934259),
                        new LngLat(-3.1875935196876526, 55.94515665035927),
                        new LngLat(-3.187624365091324, 55.94521973430925),
                        new LngLat(-3.1876927614212036, 55.94520696732767)});

        // creating a number of LngLat objects at points outside Bayes Center
        LngLat outsideBayesCenter = new LngLat(-3.1876, 55.9452);
        // \left(-3.1874,\ 55.9452\right)
        LngLat outsideBayesCenter2 = new LngLat(-3.1874, 55.9452);
        // \left(-3.187,\ 55.9452\right)
        LngLat outsideBayesCenter3 = new LngLat(-3.187, 55.9452);
        // \left(-3.187,\ 55.945\right)
        LngLat outsideBayesCenter4 = new LngLat(-3.187, 55.945);
        // \left(-3.1874,\ 55.945344\right)
        LngLat outsideBayesCenter5 = new LngLat(-3.1874, 55.945344);

        // assert false for all the points as they are outside the polygon
        assertFalse(lngLatHandler.isInRegion(outsideBayesCenter, bayesCenter));
        assertFalse(lngLatHandler.isInRegion(outsideBayesCenter2, bayesCenter));
        assertFalse(lngLatHandler.isInRegion(outsideBayesCenter3, bayesCenter));
        assertFalse(lngLatHandler.isInRegion(outsideBayesCenter4, bayesCenter));
        assertFalse(lngLatHandler.isInRegion(outsideBayesCenter5, bayesCenter));

        // creating LngLat objects for points inside Bayes Center
        // \left(-3.1874,\ 55.94534343\right)
        LngLat insideBayesCenter = new LngLat(-3.1874, 55.945343428);
        assertTrue(lngLatHandler.isInRegion(insideBayesCenter, bayesCenter));
    }

    }