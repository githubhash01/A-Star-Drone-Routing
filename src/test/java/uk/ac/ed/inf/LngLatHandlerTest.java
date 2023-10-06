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

    public void testDistanceTo()
    {
        LngLat startPosition = new LngLat(0, 0);
        LngLat endPosition = new LngLat(3, 4);
        assertEquals(5.0, lngLatHandler.distanceTo(startPosition, endPosition));

    }
    public void testingIsCloseTo()
    {
        LngLat startPosition = new LngLat(0, 0);
        LngLat endPosition = new LngLat(0, 1.49E-4);
        assertTrue(lngLatHandler.isCloseTo(startPosition, endPosition));
        LngLat endPosition2 = new LngLat(0, 1.51E-4);
        assertFalse(lngLatHandler.isCloseTo(startPosition, endPosition2));
    }

    public void testingSimpleRectangle()
    {
        NamedRegion namedRegion = new NamedRegion("simple rectangle", new LngLat[]{new LngLat(0, 0), new LngLat(0, 1), new LngLat(1, 1), new LngLat(1, 0)});
        LngLat lngLat = new LngLat(0.5, 0.5);
        assertTrue(lngLatHandler.isInRegion(lngLat, namedRegion));

    }

    public void testingGeorgeSquare(){
        NamedRegion georgeSquare = new NamedRegion("George Square Area",
                new LngLat[]{
                        new LngLat(-3.190578818321228, 55.94402412577528),
                        new LngLat(-3.1899887323379517, 55.94284650540911),
                        new LngLat(-3.187097311019897, 55.94328811724263),
                        new LngLat(-3.187682032585144, 55.944477740393744),
                        new LngLat(-3.190578818321228, 55.94402412577528)});

        LngLat insideGeorgeSquare = new LngLat(-3.189, 55.943);
        assertTrue(lngLatHandler.isInRegion(insideGeorgeSquare, georgeSquare));

        LngLat justOutsideGeorgeSquare = new LngLat(-3.1901,55.943);
        assertFalse(lngLatHandler.isInRegion(justOutsideGeorgeSquare, georgeSquare));
    }

    public void testingBayesCenter(){
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

        LngLat outsideBayesCenter = new LngLat(-3.1876, 55.9452);
        LngLat outsideBayesCenter2 = new LngLat(-3.1874, 55.9452);
        LngLat outsideBayesCenter3 = new LngLat(-3.187, 55.9452);
        LngLat outsideBayesCenter4 = new LngLat(-3.187, 55.945);
        LngLat outsideBayesCenter5 = new LngLat(-3.1874, 55.945344);

        assertFalse(lngLatHandler.isInRegion(outsideBayesCenter, bayesCenter));
        assertFalse(lngLatHandler.isInRegion(outsideBayesCenter2, bayesCenter));
        assertFalse(lngLatHandler.isInRegion(outsideBayesCenter3, bayesCenter));
        assertFalse(lngLatHandler.isInRegion(outsideBayesCenter4, bayesCenter));
        assertFalse(lngLatHandler.isInRegion(outsideBayesCenter5, bayesCenter));

        LngLat insideBayesCenter = new LngLat(-3.1874, 55.945343428);
        assertTrue(lngLatHandler.isInRegion(insideBayesCenter, bayesCenter));
    }

    public void testingEdgeCases(){
        NamedRegion square = new NamedRegion("square",
                new LngLat[]{
                        new LngLat(0, 0),
                        new LngLat(0, 1),
                        new LngLat(1, 1),
                        new LngLat(1, 0.5),
                        new LngLat(1.5, 0.5),
                        new LngLat(1.5, 1),
                        new LngLat(2, 1),
                        new LngLat(2, 0.5),
                        new LngLat(2.5, 0.5),
                        new LngLat(2.5, 0),
                        new LngLat(0, 0)});

        LngLat outsideSquare = new LngLat(-1, -1);
        assertFalse(lngLatHandler.isInRegion(outsideSquare, square));

        LngLat insideSquare = new LngLat(0.5, 0.5);
        assertTrue(lngLatHandler.isInRegion(insideSquare, square));

        LngLat vertexSquare = new LngLat(0.5, 0);
        assertTrue(lngLatHandler.isInRegion(vertexSquare, square));

        LngLat topEdgeSquare = new LngLat(0.5, 1);
        assertTrue(lngLatHandler.isInRegion(topEdgeSquare, square));

        LngLat cornerSquare = new LngLat(1, 0.5);
        assertTrue(lngLatHandler.isInRegion(cornerSquare, square));

        LngLat verticalEdgeSquare = new LngLat(1.5, 0.25);
        assertTrue(lngLatHandler.isInRegion(verticalEdgeSquare, square));

        LngLat leftVerticalEdgeSquare = new LngLat(0, 0.51);
        assertTrue(lngLatHandler.isInRegion(leftVerticalEdgeSquare, square));

        LngLat rightVerticalEdgeSquare = new LngLat(1.5, 0.2);
        assertTrue(lngLatHandler.isInRegion(rightVerticalEdgeSquare, square));

        LngLat leftOfSquare = new LngLat(-1, 0.5);
        assertFalse(lngLatHandler.isInRegion(leftOfSquare, square));

        LngLat insideSquare2 = new LngLat(0.5, 0.5);
        assertTrue(lngLatHandler.isInRegion(insideSquare2, square));

        LngLat lastPoint = new LngLat(1.5, 0.5);
        assertTrue(lngLatHandler.isInRegion(lastPoint, square));
    }
    }