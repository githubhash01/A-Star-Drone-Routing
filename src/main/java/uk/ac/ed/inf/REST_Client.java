package uk.ac.ed.inf;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
/**
 * A very simple client to GET JSON data from a remote server
 */

public class REST_Client {
    private final String BASE;
    // Endpoints

    private final String ISALIVE;
    private final String CENTRAL_AREA_URL;
    public final String NO_FLY_ZONES_URL;
    public final String RESTAURANT_URL;
    public final String ORDERS;

    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public REST_Client(String url) {
        	BASE = url;
            ISALIVE = url + "/isAlive";
            CENTRAL_AREA_URL = url + "/centralArea";
            NO_FLY_ZONES_URL = url + "/noFlyZones";
            RESTAURANT_URL = url + "/restaurants";
            ORDERS = url + "/orders";
    }

    public boolean isAlive(){
        try {
            String isAlive =  mapper.readValue(new URL(ISALIVE), String.class);
            return isAlive.equals("true");
        } catch (IOException e) {
            System.out.println("Error checking if server is alive");
            return false;
        }
    }

    public Restaurant[] fetchRestaurants() {
        try {
            return mapper.readValue(new URL(RESTAURANT_URL), Restaurant[].class);
        } catch (IOException e) {
            System.out.println("Error fetching restaurants");
            throw new RuntimeException(e);
        }
    }

    public NamedRegion fetchCentralArea() {
        try {
            return mapper.readValue(new URL(CENTRAL_AREA_URL), NamedRegion.class);
        } catch (IOException e) {
            System.out.println("Error fetching central area");
            throw new RuntimeException(e);
        }
    }

    public NamedRegion[] fetchNoFlyZones() {
        try {
            return mapper.readValue(new URL(NO_FLY_ZONES_URL), NamedRegion[].class);
        } catch (IOException e) {
            System.out.println("Error fetching no fly zones");
            throw new RuntimeException(e);
        }
    }

    public Order[] fetchOrders(LocalDate date) {

        try {
            return mapper.readValue(new URL(ORDERS + "/" + date.toString()), Order[].class);
        } catch (IOException e) {
            System.out.println("Error fetching orders");
            throw new RuntimeException(e);
        }

    }

}
