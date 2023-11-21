package uk.ac.ed.inf;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.util.List;

// TODO - write a comment for this class
/**
 * Drone class that will be used to deliver the orders
 *
 *
 */

public class Drone {

    private final FlightLog flightLog;

    private final Router router;

    // a hashmap for restaurants and their routes to appleton tower

    public Drone(Router router, FlightLog flightLog) {
        this.router = router;
        this.flightLog = flightLog;
    }

    // TODO - write a comment for this method
    public void deliverOrder(Order validatedOrder, Restaurant restaurant){

        // if the order status is valid but not delivered then route it
        if (validatedOrder.getOrderStatus().equals(OrderStatus.VALID_BUT_NOT_DELIVERED)){
            List <Cell> route = router.getRoute(restaurant);
            // Order has now been 'delivered' so update the status
            validatedOrder.setOrderStatus(OrderStatus.DELIVERED);
            // Log the route and the order
            flightLog.logRoute(validatedOrder.getOrderNo(), route);
        }
        // Update the deliveries
        flightLog.logOrder(validatedOrder);
    }

}
