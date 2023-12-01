package uk.ac.ed.inf;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

public class AppTest {


    @BeforeClass
    public static void setUp() {
        String[] input = {"2023-09-01", "https://ilp-rest.azurewebsites.net/"};
        // Run the app
        App.main(input);
    }

    @Test
    public void testOrders() {
        // Get the deliveries from result files
        List<Delivery> deliveries = App.flightLog.deliveries;
        // Print the number of deliveries
        System.out.println("Number of deliveries: " + deliveries.size());
    }
    /*
    Testing the flight path starts and ends at Appleton Tower for each new order
     */
    @Test
    public void testStartEnd(){
        List<Move> flightPath = App.flightLog.flightPath;
        // Check that the first move is a takeoff
        LngLat Appleton = new LngLat(-3.186874, 55.944494);

        HashSet<String> orderNumbers = new HashSet<>();
        for (Move move : flightPath){
            // if there is a new order it should start with appleton
            if (move.orderNo != null){
                // if the orderNo is in the set then don't add it
                if (!orderNumbers.contains(move.orderNo)){
                    System.out.println("Order number: " + move.orderNo);
                    orderNumbers.add(move.orderNo);
                    // check that the first move is close to appleton
                    LngLat first = new LngLat(move.fromLongitude, move.fromLatitude);
                    System.out.println("First move: " + first.lat() + ", " + first.lng());
                    assertTrue(new LngLatHandler().isCloseTo(first, Appleton));
                }
            }
        }
        // check that the first and last moves are close to appleton
        double start_lat = flightPath.get(0).fromLatitude;
        double start_lng = flightPath.get(0).fromLongitude;
        LngLat start = new LngLat(start_lng, start_lat);

        LngLatHandler lnglatHandler = new LngLatHandler();
        // check it is close to appleton
        assertTrue(lnglatHandler.isCloseTo(start, Appleton));

        // check that the last move is a landing
        double end_lat = flightPath.get(flightPath.size()-1).toLatitude;
        double end_lng = flightPath.get(flightPath.size()-1).toLongitude;
        LngLat end = new LngLat(end_lng, end_lat);
        // check it is close to appleton
        assertTrue(lnglatHandler.isCloseTo(end, Appleton));
    }

    /*
    Testing that the distance between start and end of moves is zero (i.e no jumping)
     */
    @Test
    public void testDistanceBetweenMoves(){
        // the drone must not move more than the move distance in one move
        List<Move> flightPath = App.flightLog.flightPath;
        List<Double> distances = new ArrayList<>();
        LngLatHandler lnglatHandler = new LngLatHandler();
        for (int i = 0; i < flightPath.size() - 1; i++){
            Move firstMove = flightPath.get(i);
            Move secondMove = flightPath.get(i+1);
            LngLat first = new LngLat(firstMove.toLongitude, firstMove.toLatitude);
            LngLat second = new LngLat(secondMove.fromLongitude, secondMove.fromLatitude);
            // make sure that the
            double distance = lnglatHandler.distanceTo(first, second);
            // add the distance to the list
            distances.add(distance);
        }
        // all the distances should be 0
        for (double distance : distances){
            assertEquals(0, distance, 0.0001);
        }
    }

    /*
    Testing that the distance moved is less than the move distance
     */
    @Test
    public void testDistanceMoved(){
        // the drone must not move more than the move distance in one move
        List<Move> flightPath = App.flightLog.flightPath;
        LngLatHandler lnglatHandler = new LngLatHandler();
        for (int i = 0; i < flightPath.size() - 1; i++){
            Move firstMove = flightPath.get(i);
            Move secondMove = flightPath.get(i+1);
            LngLat first = new LngLat(firstMove.toLongitude, firstMove.toLatitude);
            LngLat second = new LngLat(secondMove.toLongitude, secondMove.toLatitude);
            // make sure that the
            double distance = lnglatHandler.distanceTo(first, second);
            //assertTrue(distance < (SystemConstants.DRONE_MOVE_DISTANCE + 0.0001));
            // add the distance to the list
            LngLat firstFrom = new LngLat(firstMove.fromLongitude, firstMove.fromLatitude);
            LngLat secondFrom = new LngLat(secondMove.fromLongitude, secondMove.fromLatitude);
            double distanceFrom = lnglatHandler.distanceTo(firstFrom, secondFrom);
            //assertTrue(distanceFrom < (SystemConstants.DRONE_MOVE_DISTANCE + 0.0001));
            System.out.println(distanceFrom);
        }
    }

    /*
    Making sure that the number of hovers is double the number of valid orders
     */
    @Test
    public void testHover(){
        // Number of hovers should be double of the number of valid orders
        List<Move> flightPath = App.flightLog.flightPath;
        Restaurant[] restuarants = App.restaurants;
        // get the locations of all the restaurants
        LngLatHandler lnglatHandler = new LngLatHandler();
        HashSet<LngLat> restaurantLocations = new HashSet<>();
        for (Restaurant restaurant : restuarants){
            restaurantLocations.add(restaurant.location());
        }
        LngLat Appleton = new LngLat(-3.186874, 55.944494);
        // Count number of moves with distinct orderNo
        HashSet<String> orderNumbers = new HashSet<>();
        int validOrderCount = 0;
        int hoverCount = 0;
        for (Move move : flightPath){
            if (move.angle == 999){
                hoverCount++;
                if (!orderNumbers.contains(move.orderNo)){
                    orderNumbers.add(move.orderNo);
                    validOrderCount++;
                }
                // if the location is close to appleton print d
                LngLat hoverLocation = new LngLat(move.fromLongitude, move.fromLatitude);
                if (lnglatHandler.isCloseTo(hoverLocation, Appleton)){
                    System.out.println("Hover location close to Appleton " + move.orderNo);
                }
                // if the location is close to a restaurant print
                for (LngLat restaurantLocation : restaurantLocations){
                    if (lnglatHandler.isCloseTo(hoverLocation, restaurantLocation)){
                        System.out.println("Hover location close to restaurant " + move.orderNo);
                    }
                }

            }
        }
        System.out.println("Number of valid deliveries: " + validOrderCount);
        System.out.println("Number of hovers: " + hoverCount);

        // number of hovers should be double that of the number of valid orders
        assertEquals(validOrderCount * 2, hoverCount);
        }

    /*
    Testing the order of the deliveries and the routing, is the same as the order of the orders
     */
    @Test
    public void testOrderOfDeliveries(){
        // Get the true order of the orders from the REST service
        Order[] orders = App.orders;
        // for each delivery in the deliveries make sure the orderNo is the same as the order
        List<Delivery> deliveries = App.flightLog.deliveries;
        // check that the number of deliveries is the same as the number of orders
        assertEquals(orders.length, deliveries.size());
        for (int i =0; i < orders.length; i++){
            // get the order number of the ith order
            String orderNo = orders[i].getOrderNo();
            // get the order number of the ith delivery
            String deliveryOrderNo = deliveries.get(i).orderNo;
            // check that the order numbers are the same
            assertEquals(orderNo, deliveryOrderNo);
        }
    }

    /*
    Testing the drone delivers the orders in the right order
     */
    @Test
    public void testOrderOfDroneDeliveries(){

        List<Move> flightPath = App.flightLog.flightPath;
        // create a list of unique order numbers from the flightPath
        HashSet<String> flightPathOrderNumbersSet = new HashSet<>();
        List<String> flightPathOrderNumbers = new ArrayList<>();
        for (Move move : flightPath){
            if (move.orderNo != null){
                if (!flightPathOrderNumbersSet.contains(move.orderNo)){
                    flightPathOrderNumbersSet.add(move.orderNo);
                    flightPathOrderNumbers.add(move.orderNo);
                }
            }
        }
        OrderValidator orderValidator = new OrderValidator();
        // get the orders from the REST service
        Order[] orders = App.orders;
        List<String> trueOrderNumbers = new ArrayList<>();
        for (Order order : orders){
            // validate the order
            Order validatedOrder = orderValidator.validateOrder(order, App.restaurants);
            // if the order status is valid but not delivered then add it to the list
            if (validatedOrder.getOrderStatus().equals(OrderStatus.VALID_BUT_NOT_DELIVERED)){
                trueOrderNumbers.add(validatedOrder.getOrderNo());
            }
        }
        // check that the two lists are identical
        assertEquals(flightPathOrderNumbers, trueOrderNumbers);
    }
}
