package uk.ac.ed.inf;

// import the CentralRegionVertexOrder class from the ilp project
import uk.ac.ed.inf.ilp.data.*;
import uk.ac.ed.inf.ilp.constant.OrderStatus;

import javax.xml.validation.Validator;
import java.time.LocalDate;

/**
 * The main class of the project that contains the main method
 */
public class App 
{


    public static void main(String[] args)
    {
        // TODO allow the user to specify the date from the command line
        // in the mean time, hard code the date to 2023-09-01
        LocalDate date = LocalDate.parse("2023-09-02");

        // hard code the appleton tower location
        LngLat appleton = new LngLat(-3.1869, 55.9445);

        // create a flightpath and a deliveries object to store the flightpath and the deliveries of the day
        Flightpath flightpath = new Flightpath();
        Deliveries deliveries = new Deliveries();

        // fetch the orders, no-fly zones, central area and restaurants from the REST service
        REST_Client restClient = new REST_Client();
        Order[] orders = restClient.fetchOrders(date);
        OrderValidator orderValidator = new OrderValidator();

        NamedRegion[] noFlyZones = restClient.fetchNoFlyZones();
        NamedRegion centralArea = restClient.fetchCentralArea();
        Restaurant[] restaurants = restClient.fetchRestaurants();

        // create a drone and a router for the drone
        Router router = new Router(noFlyZones, centralArea, appleton, restaurants);
        Drone drone = new Drone(router, flightpath, deliveries);

        // let the drone deliver the all the orders of the day
        for (Order order : orders){
            // validate the order
            Order validatedOrder = orderValidator.validateOrder(order, restaurants);
            // get the drone to deliver the validated order
            drone.deliverOrder(validatedOrder);
        }

        // write the flightpath and the deliveries to the json files
        flightpath.writeDroneFlightpath();

    }
}
