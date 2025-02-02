package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * RoutePlanner:
 - Takes data on central area, no-fly-zones, appleton tower
 - Plans the route for the drone to pick up and deliver an order from a restaurant
 - Builds the full pick-up and delivery route by starting with the pickup route and then reversing it
 - If the restaurant is inside the central area, then the drone goes directly to it from appleton tower
 - Otherwise routes in 2 stages, routes out of central and then to the restaurant
 - Caches the full route for each restaurant to avoid recalculating it for later orders from the same restaurant
 - Additional functions for planning the most direct route to the central area if all no-fly-zones are inside
   the central area, in which case the drone goes directly to the closest point
 */

public class RoutePlanner {

    private final LngLatHandler lnglatHandler;
    private final NamedRegion[] noFlyZones;
    private final NamedRegion centralArea;
    private final Cell appleton;
    private final boolean noFlyZonesAllCentral;
    private final HashMap<Restaurant, List<Cell>> savedRoutes = new HashMap<>();

    public RoutePlanner(NamedRegion[] noFlyZones, NamedRegion centralArea, LngLat appleton){
        this.noFlyZones = noFlyZones;
        this.centralArea = centralArea;
        this.appleton = new Cell(appleton); // convert to cell
        this.lnglatHandler = new LngLatHandler();
        this.noFlyZonesAllCentral = noFlyZonesAllCentral();

    }

    // get the full route (pickup and delivery) for the restaurant
    public List<Cell> getRoute(Restaurant restaurant){
        // if the route is saved in the cache, then return it
        if (savedRoutes.containsKey(restaurant)){
            return savedRoutes.get(restaurant);
        }
        // get the pick-up route from appleton to the restaurant
        List<Cell> pickUpRoute = getPickUp(restaurant);
        // get the round trip (pickup and delivery) route
        List<Cell> roundTrip = buildRoundTrip(pickUpRoute);
        // add appleton to the round trip as a hover
        roundTrip.add(appleton);
        // cache the route
        savedRoutes.put(restaurant, roundTrip);
        // return route
        return roundTrip;
    }

    // build the round trip (pickup and delivery) route from just the pickup route
    public List<Cell> buildRoundTrip(List<Cell> pickUpRoute){
        List<Cell> delivery = getDelivery(pickUpRoute);
        pickUpRoute.addAll(delivery);
        return pickUpRoute;
    }

    // reverses the pickup route to get the delivery route
    public List<Cell> getDelivery(List<Cell> pickUpRoute){
        List<Cell> delivery = new ArrayList<>(pickUpRoute);
        Collections.reverse(delivery);
        return delivery;
    }

    // gets the route from appleton tower to the restaurant
    public List<Cell> getPickUp(Restaurant restaurant){
        Cell restaurant_loc = new Cell(restaurant.location());
        // if the restaurant is in the central area, then go directly to appleton
        if (inCentralArea(restaurant)){
            return A_Star.runA_Star(noFlyZones, centralArea, appleton, restaurant_loc, false, true);
        }
        // otherwise drone must exit the central area, and then go to the restaurant
        Cell exitPoint;
        if (noFlyZonesAllCentral){
            exitPoint = new Cell(getRegionClosestPoint(centralArea, restaurant_loc));
        }
        else {
            exitPoint = restaurant_loc;
        }
        // get the route to exit the central area
        List<Cell> exitRoute = A_Star.runA_Star(noFlyZones, centralArea, appleton, exitPoint, true, false);
        // get the last value of the exitRoute to use as the start point for the route to the restaurant
        exitPoint = new Cell(exitRoute.get(exitRoute.size()-1).lngLat);
        // go to the restaurant from the exit point
        List<Cell> restaurantRoute = A_Star.runA_Star(noFlyZones, centralArea, exitPoint, restaurant_loc, false, false);
        // remove the first cell from the restaurant route, to avoid duplicates
        restaurantRoute.remove(0);
        // combine the two routes
        exitRoute.addAll(restaurantRoute);
        return exitRoute;
    }


    public boolean inCentralArea(Restaurant restaurant){
        Cell restaurant_loc = new Cell(restaurant.location());
        return lnglatHandler.isInRegion(restaurant_loc.lngLat, centralArea);
    }

    /**
     * Additional functions for planning the most direct route to the central area, in the case that all no-fly-zones
     * are inside the central area (i.e. the drone can go directly to the closest point on the edge of the central area)
     * N.B to disable this feature, set noFlyZonesAllCentral to false
     */

    // checks that all the no-fly-zones are inside the central area
    private boolean noFlyZonesAllCentral(){
        for (NamedRegion noFlyZone : noFlyZones) {
            // go through each point in the no-fly-zone
            for (LngLat point : noFlyZone.vertices()) {
                // if any point is outside the central area, then at least one no-fly-zone is outside the central area
                if (!lnglatHandler.isInRegion(point, centralArea)){
                    return false;
                }
            }
        }
        return true;
    }

    // finds the closest point on the edge of the named region to the cell
    private LngLat closestOnEdge(LngLat A, LngLat B, LngLat P){

        // convert the LngLat to array
        double[] A_ = {A.lng(), A.lat()};
        double[] B_ = {B.lng(), B.lat()};
        double[] P_ = {P.lng(), P.lat()};

        // u = B - A
        double[] u = {B_[0]-A_[0], B_[1]-A_[1]};
        // v = P - a
        double[] v = {P_[0]-A_[0], P_[1]-A_[1]};

        // t = (v.u) / (v.v)
        double t = (v[0]*u[0] + v[1]*u[1]) /  (u[0]*u[0] + u[1]*u[1]);

        // if t is less than 0, then the closest point is A
        if (t <= 0) {
            return A;
        }
        // if t is greater than 1, then the closest point is B
        else if (t >= 1) {
            return B;
        }
        else{
            // otherwise, the closest point is A + t*u
            double[] closestPoint = {A_[0] + t*u[0], A_[1] + t*u[1]};
            return new LngLat(closestPoint[0], closestPoint[1]);
        }
    }

    // finds the closest point on the edge of the named region to the cell
    private LngLat getRegionClosestPoint(NamedRegion centralArea, Cell point) {
        /*
         * Goes through every edge of the named region
         * For each edge, finds the closest point on the edge to the cell
         * Find the distance between the cell, and the closest point
         * Returns the closest point from all the edges
         */
        LngLat P = new LngLat(point.lngLat.lng(), point.lngLat.lat());
        double shortestDistance = Double.MAX_VALUE;
        LngLat closestPoint = null;
        int N = centralArea.vertices().length;

        for (int i = 0; i < N; i++){
            LngLat A = centralArea.vertices()[i];
            LngLat B = centralArea.vertices()[(i+1)%N];
            // find the closest point on the edge to the cell
            LngLat edgeClosest = closestOnEdge(A, B, P);
            // find the distance between the cell and the closest point
            double distance = lnglatHandler.distanceTo(P, edgeClosest);
            // if the distance is smaller than the shortest distance, then update the shortest distance
            if (distance < shortestDistance){
                shortestDistance = distance;
                closestPoint = edgeClosest;
            }
        }
        return closestPoint;
    }
}