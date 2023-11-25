package uk.ac.ed.inf;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.*;
// import the order validator


import java.time.LocalDate;

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

    /**
     * Rigourous Test :-)
    public void testFull()
    {
        // Set the local date to 2023-09-01
        // First we get the restaurants and orders using the rest client
        REST_Client rest_client = new REST_Client();


        // create a flightpath and a deliveries object to store the flightpath and the deliveries of the day
        DroneFlightPath droneFlightPath = new DroneFlightPath();
        Deliveries deliveries = new Deliveries();

        Restaurant[] restaurants = rest_client.fetchRestaurants();
        NamedRegion[] noFlyZones = rest_client.fetchNoFlyZones();
        NamedRegion centralArea = rest_client.fetchCentralArea();

        // Create the orders manually so that each order has a pizza from a different restaurant
        Order[] orders = new Order[4];

        // Create credit card details
        CreditCardInformation creditCardInformation = new CreditCardInformation();

        // Set the date to today
        LocalDate date = LocalDate.now();
        for (int i =0; i<4; i++){
            // get the first pizza on the menu of the ith restaurant
            Pizza[] pizza = {restaurants[i].menu()[0]};
            // create a new order with the pizza
            orders[i] = new Order("orderNo",date, 1000, pizza, creditCardInformation);
            // set the order status to 'VALID_BUT_NOT_DELIVERED'
            orders[i].setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
        }

        LngLat appleton = new LngLat(-3.1869, 55.9445);

        // create a drone and a router for the drone
        Router router = new Router(noFlyZones, centralArea, appleton);
        Drone drone = new Drone(router, droneFlightPath, deliveries);

        // just get the first two orders
        Order[] restrictedOrders = {orders[1]};;
        // let the drone deliver the all the orders of the day
        for (Order order : orders){
            // get the drone to deliver the order
            drone.deliverOrder(order, restaurants[0]);
        }

        // write the flightpath and the deliveries to the json files
        droneFlightPath.writeDroneFlightpath();

        // Then we print the deliveries
        System.out.println(deliveries);
    }
     */
}
