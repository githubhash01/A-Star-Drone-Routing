package uk.ac.ed.inf;

// import
import org.junit.Test;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.List;


public class A_Star_Test {


    @Test
    public void testA_StarInside(){
        // Get the no fly zones and central area from REST service
        String url = "https://ilp-rest.azurewebsites.net";
        REST_Client restClient = new REST_Client(url);
        NamedRegion[] noFlyZones = restClient.fetchNoFlyZones();
        NamedRegion centralArea = restClient.fetchCentralArea();
        // make the start appleton tower
        Cell start = new Cell(new LngLat(-3.186874, 55.944494));

        Cell restaurant_loc = new Cell(new LngLat(-3.1913, 	55.9455));

        List<Cell> path = A_Star.runA_Star(noFlyZones, centralArea, start, restaurant_loc, false, true);
        System.out.println(path);
    }
    @Test
    public void testAStarOutside(){
        // Get the no fly zones and central area from REST service
        String url = "https://ilp-rest.azurewebsites.net";
        REST_Client restClient = new REST_Client(url);
        NamedRegion[] noFlyZones = restClient.fetchNoFlyZones();
        NamedRegion centralArea = restClient.fetchCentralArea();
        // [lng=-3.1925740000000036, lat=55.944494]
        Cell start = new Cell(new LngLat(-3.1925740000000036, 55.944494));

        Cell restaurant_loc = new Cell(new LngLat(-3.2025, 55.9433));

        List<Cell> path = A_Star.runA_Star(noFlyZones, centralArea, start, restaurant_loc, false, false);
        System.out.println(path);
    }
}
