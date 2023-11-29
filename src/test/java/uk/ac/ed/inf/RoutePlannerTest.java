package uk.ac.ed.inf;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
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
        // Set the local date to 2023-09-01
        String url = "https://ilp-rest.azurewebsites.net";
        String date = "2023-09-01";

        // Create a flightlog
        FlightLog flightLog = new FlightLog(LocalDate.parse(date));
        // Get the data from REST service
        REST_Client restClient = new REST_Client(url);
        NamedRegion[] noFlyZones = restClient.fetchNoFlyZones();
        NamedRegion centralArea = restClient.fetchCentralArea();
        RoutePlanner routePlanner = new RoutePlanner(noFlyZones, centralArea, new LngLat(-3.1869, 55.9445));

        // Create a restaurant that is far away from the central area
        LngLat farAwayLocation = new LngLat(-3.392473, 56.146233);
        // opening days are Monday to Friday
        DayOfWeek[] openingDays = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY};
        Pizza[] menu = {new Pizza("Margherita", 10)};
        Restaurant restaurant = new Restaurant("name", farAwayLocation, openingDays, menu);

        // Get the route from the route planner
        List<Cell> route = routePlanner.getRoute(restaurant);
        // test that the route is not null
        assertNotNull(route);
        // add the route to the flight log
        flightLog.logRoute("0", route);
        // get the flightlog to output the route
        flightLog.writeDroneFlightpath();

    }

}
