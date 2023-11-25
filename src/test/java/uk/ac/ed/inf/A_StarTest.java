package uk.ac.ed.inf;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class A_StarTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public A_StarTest( String testName )
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
    /*
    public void testGetClosestPoint()
    {
        //
        LngLatHandler lngLatHandler = new LngLatHandler();
        // get the central area
        REST_Client restClient = new REST_Client();
        NamedRegion centralArea = restClient.fetchCentralArea();
        NamedRegion[] noFlyZones = restClient.fetchNoFlyZones();

        LngLat start = new LngLat(-3.2025, 55.9433);
        Cell startCell = new Cell(start);
        //end point is the closest point on the central area
        //LngLat end = A_Star.getRegionClosestPoint(centralArea, startCell);
        System.out.println(end);
        Cell endCell = new Cell(end);
        // check that end cell is in the central area
        assertTrue(lngLatHandler.isInRegion(end, centralArea));
        // run A* algorithm
        List<Cell> shortestPath = A_Star.runA_Star(noFlyZones, centralArea, startCell, endCell);

        FlightLog flightLog = new FlightLog(LocalDate.parse("2002-10-16"));

        flightLog.logRoute("s", shortestPath);
        // print the shortest path
        flightLog.writeDroneFlightpath();

    }

     */
}