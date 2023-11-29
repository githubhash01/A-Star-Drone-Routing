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
    - Builds the full pick-up and delivery route by starting with the delivery route and then reversing it
    - Builds routes in 2 stages, routes to central (if the restaurant is outside the central area) and then to appleton
    - Caches the full route for each restaurant to avoid recalculating it for later orders from the same restaurant
    - Additional functions for planning the most direct route to the central area if all no-fly-zones are inside
      the central area, in which case the drone goes directly to the closest point
 */

public class RoutePlanner {

    private final LngLatHandler lnglatHandler;
    private final NamedRegion[] noFlyZones;
    private final NamedRegion centralArea;
    private final LngLat appleton;

    private final boolean noFlyZonesAllCentral;

    private final HashMap<Restaurant, List<Cell>> restaurantRoutes = new HashMap<>();

    public RoutePlanner(NamedRegion[] noFlyZones, NamedRegion centralArea, LngLat appleton){
        this.noFlyZones = noFlyZones;
        this.centralArea = centralArea;
        this.appleton = appleton;
        this.lnglatHandler = new LngLatHandler();
        this.noFlyZonesAllCentral = noFlyZonesAllCentral();

    }

    // get the route from the restaurant to appleton tower
    public List<Cell> getRoute(Restaurant restaurant){

        // if the route is saved in the cache, then return it
        if (restaurantRoutes.containsKey(restaurant)){
            return restaurantRoutes.get(restaurant);
        }
        // get the route from the restaurant to appleton tower
        List<Cell> routeToAT = getPickupRoute(restaurant);
        // get the full pickup and delivery (round-trip) route
        List<Cell> roundTrip = getRoundTrip(routeToAT);
        // cache the route
        restaurantRoutes.put(restaurant, roundTrip);

        return roundTrip;

    }

    // gets the route from the restaurant to appleton tower
    public List<Cell> getPickupRoute(Restaurant restaurant){

        Cell restaurant_loc = new Cell(restaurant.location());
        boolean isInCentral = lnglatHandler.isInRegion(restaurant_loc.lngLat, centralArea);

        // if the restaurant is inside the central area, then go directly to appleton
        if (isInCentral){
            return getRouteToAT(restaurant_loc);
        }

        // otherwise first go to the central area, and from there go to appleton
        else {
            List<Cell> routeToCenter = getRouteToCenter(restaurant_loc);
            // get the center start from the final point of the route to the center
            Cell center = new Cell(routeToCenter.get(routeToCenter.size()-1).lngLat);
            // remove the last cell from the route to center, to avoid duplicates
            routeToCenter.remove(routeToCenter.size()-1);
            List<Cell> routeToAppleton = getRouteToAT(center);
            routeToCenter.addAll(routeToAppleton);
            return routeToCenter;
        }
    }

    // input the route from the restaurant to appleton tower and return the full route
    // appleton -> restaurant -> appleton
    public List<Cell> getRoundTrip(List<Cell> route){
        List<Cell> reversedRoute = new ArrayList<>(route);
        Collections.reverse(reversedRoute);
        reversedRoute.addAll(route);
        return reversedRoute;
    }

    // gets the route from a restaurant to the central area using A*
    public List<Cell> getRouteToCenter(Cell restaurant){
        if (noFlyZonesAllCentral){
            Cell closestPoint = new Cell(getRegionClosestPoint(centralArea, restaurant));
            return A_Star.runA_Star(noFlyZones, centralArea, restaurant, closestPoint, true);
        }
        else {
            Cell appleton = new Cell(this.appleton);
            return A_Star.runA_Star(noFlyZones, centralArea, restaurant, appleton, true);
        }
    }

    // get the route from a central area location to appleton tower using A*
    public List<Cell> getRouteToAT(Cell restaurant){
        Cell appleton = new Cell(this.appleton);
        return A_Star.runA_Star(noFlyZones, centralArea, restaurant, appleton, false);
    }


    /**
     * Additional functions for planning the most direct route to the central area
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

        // convert the LngLat to double array
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
            // otherwise the closest point is A + t*u
            double[] closestPoint = {A_[0] + t*u[0], A_[1] + t*u[1]};
            return new LngLat(closestPoint[0], closestPoint[1]);
        }
    }

    // finds the closest point on the edge of the named region to the cell
    private LngLat getRegionClosestPoint(NamedRegion centralArea, Cell point) {
        /*
         * Goes through every edge of the named region
         * For each edge, finds the closest point on the edge to the cell
         * Find the distance between the cell and the closest point
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

