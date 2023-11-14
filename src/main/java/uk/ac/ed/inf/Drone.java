package uk.ac.ed.inf;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.Order;

import java.util.List;

/**
 * Drone class that will be used to deliver the orders
 *
 *
 */

public class Drone {

    // drone will have an order validator, a flightpath, deliveries, and a router

    private OrderValidator validator;
    private Flightpath flightpath;
    private Deliveries deliveries;
    private Router router;

    // a hashmap for restaurants and their routes to appleton tower

    public Drone(Router router, Flightpath flightpath, Deliveries deliveries) {
        this.router = router;
        this.flightpath = flightpath;
        this.deliveries = deliveries;
        this.validator = new OrderValidator();
    }

    /**
     * Deliver the order:
     * -- validate the order
     * -- route the order (if valid but not delivered)
     * -- update the flightpath and the deliveries
     */

    public void deliverOrder(Order validatedOrder){
        // if the order status is valid but not delivered then route it
        if (validatedOrder.getOrderStatus().equals(OrderStatus.VALID_BUT_NOT_DELIVERED)){
            List <Cell> route = router.getRoute(validatedOrder);
            flightpath.addOrderRoute(validatedOrder.getOrderNo(), route);
            validatedOrder.setOrderStatus(OrderStatus.DELIVERED);

        }
        // Update the Deliveries
        deliveries.addOrder(validatedOrder);
    }

}
