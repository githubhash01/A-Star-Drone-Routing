package uk.ac.ed.inf;

// import collections
import java.util.ArrayList;
import java.util.Collections;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.data.Order;

import java.util.HashMap;
import java.util.List;

import static uk.ac.ed.inf.Utils.getRestaurant;

public class Router {

    private final NamedRegion[] noFlyZones;
    private final NamedRegion centralArea;
    private final LngLat appleton;
    private final Restaurant[] restaurants;

    private final HashMap<Restaurant, List<Cell>> restaurantRoutes = new HashMap<>();


    // constructor
    public Router(NamedRegion[] noFlyZones, NamedRegion centralArea, LngLat appleton, Restaurant[] restaurants){
        this.noFlyZones = noFlyZones;
        this.centralArea = centralArea;
        this.appleton = appleton;
        this.restaurants = restaurants;
    }

    // get the route from the restaurant to appleton tower
    public List<Cell> getRoute(Order order){

        Restaurant restaurant = getRestaurant(restaurants, order);
        List<Cell> route;
        // if the route has already been calculated then return it
        if (restaurantRoutes.containsKey(restaurant)){
            route = restaurantRoutes.get(restaurant);
            return route;
        }
        else {
            assert restaurant != null;
            // print the restaurant
            Cell start = new Cell(restaurant.location());
            Cell end = new Cell(appleton);
            // get the route from the restaurant to appleton tower
            route = A_Star.runA_Star(noFlyZones, centralArea, start, end);
            // reverse the route so that it is from the restaurant to appleton tower

            assert route != null;
            List<Cell> reversedRoute = new ArrayList<>(route);
            Collections.reverse(reversedRoute);
            // append the route from the restaurant back to appleton tower
            reversedRoute.addAll(route);
            // add the full route to the hashmap
            restaurantRoutes.put(restaurant, reversedRoute);
            return reversedRoute;

        }

    }


    // get the restaurants
    public Restaurant[] getRestaurants(){
        return restaurants;
    }
}
