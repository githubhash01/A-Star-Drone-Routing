package uk.ac.ed.inf;

// import the CentralRegionVertexOrder class from the ilp project
import com.fasterxml.jackson.core.JsonProcessingException;
import uk.ac.ed.inf.ilp.data.*;

import java.time.LocalDate;

/**
 * The main class of the project that contains the main method
 */
public class App 
{

    public static void main(String[] args) throws JsonProcessingException {
        // TODO allow the user to specify the date from the command line
        // in the mean time, hard code the date to 2023-09-01
        LocalDate date = LocalDate.parse("2023-11-15");

        // hard code the appleton tower location TODO - make this come from constant
        LngLat appleton = new LngLat(-3.1869, 55.9445);

        // create a flight log
        FlightLog flightLog = new FlightLog();

        // fetch the orders, no-fly zones, central area and restaurants from the REST service
        REST_Client restClient = new REST_Client();
        Order[] orders = restClient.fetchOrders(date);

        NamedRegion[] noFlyZones = restClient.fetchNoFlyZones();
        NamedRegion centralArea = restClient.fetchCentralArea();
        Restaurant[] restaurants = restClient.fetchRestaurants();

        // create a drone and a router for the drone
        Router router = new Router(noFlyZones, centralArea, appleton);
        Drone drone = new Drone(router, flightLog);

        // create an order validator
        OrderValidator orderValidator = new OrderValidator();

        // let the drone deliver the all the orders of the day
        for (Order order : orders){
            // validate the order
            Order validatedOrder = orderValidator.validateOrder(order, restaurants);
            // get the restaurant that matches the order
            Restaurant restaurant = orderValidator.getRestaurant(restaurants, validatedOrder);
            // get the drone to deliver the validated order from the restaurant
            drone.deliverOrder(validatedOrder, restaurant);
        }

        // write the flightpath and the deliveries to the json files
        flightLog.writeDroneFlightpath();
        flightLog.writeFlightpath();
        flightLog.writeDeliveries();

    }
}
