package uk.ac.ed.inf;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ed.inf.ilp.data.*;
// import the order validator


import javax.naming.Name;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class RoutePlannerTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public RoutePlannerTest(String testName )
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

    public void testFarAwayRestaurant() {
        String url = "https://ilp-rest.azurewebsites.net";
        String date = "2000-01-01"; // date doesn't matter for this test

        FlightLog flightLog = new FlightLog(LocalDate.parse(date));
        // Get the data from REST service
        REST_Client restClient = new REST_Client(url);
        NamedRegion[] noFlyZones = restClient.fetchNoFlyZones();
        NamedRegion centralArea = restClient.fetchCentralArea();
        LngLat appleton = new LngLat(-3.186874, 55.944494);
        RoutePlanner routePlanner = new RoutePlanner(noFlyZones, centralArea, appleton);

        LngLat farAwayLocation = new LngLat(-3.128203143916, 55.895846113595);
        DayOfWeek[] openingDays = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY};
        Pizza[] menu = {new Pizza("Margherita", 10)};
        Restaurant restaurant = new Restaurant("name", farAwayLocation, openingDays, menu);

        List<Cell> route = routePlanner.getRoute(restaurant);
        assertNotNull(route);
        // add the route to the flight log
        flightLog.logRoute("0", route);
        // get the flightlog to output the route
        flightLog.writeDroneFlightpath();

    }

}
