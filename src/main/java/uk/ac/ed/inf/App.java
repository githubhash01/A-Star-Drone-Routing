package uk.ac.ed.inf;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.*;
import java.time.LocalDate;
import java.util.List;

/**
 * App class where PizzaDronz can be run from
    - validates the input parameters (date and url)
    - initialises the flight log to store flightpath, list of deliveries for the day
    - fetches the data from the REST service (orders, no-fly zones, central area and restaurants)
    - delivers the orders (using the OrderValidator class to validate the orders and the
      RoutePlanner class to route the valid orders)
    - outputs the flight logs (using the FlightLog class already instantiated)
 */

public class App 
{

    static private final LngLat APPLETON = new LngLat(-3.186874, 55.944494);
    static Order[] orders;
    static private NamedRegion[] noFlyZones;
    static private NamedRegion centralArea;
    static Restaurant[] restaurants;
    static FlightLog flightLog;
    static private LocalDate date;
    static private REST_Client restClient;

    public static void main(String[] args){

        // validate the input parameters
        validateInputParameters(args);
        // initialise the flight log
        flightLog = new FlightLog(date);
        // get the data from the REST service
        fetchRESTData();
        // deliver the orders
        deliverOrders();
        // output the flight logs
        outputFlightLogs();
        // exit with success
        System.out.println("Drone routing complete");
    }

    private static void fetchRESTData(){

        noFlyZones = restClient.fetchNoFlyZones();
        centralArea = restClient.fetchCentralArea();
        restaurants = restClient.fetchRestaurants();
        orders = restClient.fetchOrders(date);
    }

    private static void deliverOrders(){
        OrderValidator orderValidator = new OrderValidator();
        RoutePlanner routePlanner = new RoutePlanner(noFlyZones, centralArea, APPLETON);
        for (Order order : orders){
            // validate the order
            Order validatedOrder = orderValidator.validateOrder(order, restaurants);
            // get the restaurant that matches the order
            Restaurant restaurant = orderValidator.getRestaurant(restaurants, validatedOrder);
            // if the order status is valid but not delivered then route it
            if (validatedOrder.getOrderStatus().equals(OrderStatus.VALID_BUT_NOT_DELIVERED)){
                List<Cell> route = routePlanner.getRoute(restaurant);
                // Order has now been 'delivered' so update the status
                validatedOrder.setOrderStatus(OrderStatus.DELIVERED);
                // Log the route taken to delivery the order
                flightLog.logRoute(validatedOrder.getOrderNo(), route);
            }
            // update the deliveries with the order whether it was delivered or not
            flightLog.logOrder(validatedOrder);
        }
    }

    // writes the flight log as the 3 json output files
    private static void outputFlightLogs(){
        try {
            flightLog.writeDeliveries();
            flightLog.writeFlightpath();
            flightLog.writeDroneFlightpath();
        } catch (JsonProcessingException e) {
            System.out.println("Error writing flight logs");
            System.exit(1);
        }
    }

    // prevents illegal arguments being passed to the program
    private static void validateInputParameters(String[] args){
        // only accept 2 arguments
        if (args.length != 2){
            System.out.println("Error: Incorrect number of arguments");
            System.exit(1);
        }

        date = LocalDate.parse(args[0]);
        String url = args[1];

        // if the date occurs before 2023-09-01 or strictly after 2024-01-28 then exit with an error
        if (date.isBefore(LocalDate.parse("2023-09-01")) || date.isAfter(LocalDate.parse("2024-01-28"))){
            System.out.println("Error: Date must be between 2023-09-01 and 2024-01-28");
            System.exit(1);
        }
        // attempt the 'isAlive' check on the url
        restClient = new REST_Client(url);
        if (!restClient.isAlive()){
            System.out.println("Error: could not reach the server");
            System.exit(1);
        }

    }


}
