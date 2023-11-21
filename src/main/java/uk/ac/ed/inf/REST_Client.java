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
    private static final String BASE = "https://ilp-rest.azurewebsites.net";
    // Endpoints
    private static final String CENTRAL_AREA_URL = BASE + "/centralArea";
    public static final String NO_FLY_ZONES_URL = BASE + "/noFlyZones";
    public static final String RESTAURANT_URL = BASE + "/restaurants";
    public static final String ORDERS = BASE + "/orders";

    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());


    public boolean isAlive(){
        try {
            String isAlive =  mapper.readValue(new URL(BASE), String.class);
            return isAlive.equals("true");
        } catch (IOException e) {
            return false;
        }
    }
    public Restaurant[] fetchRestaurants() {
        try {
            return mapper.readValue(new URL(RESTAURANT_URL), Restaurant[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public NamedRegion fetchCentralArea() {
        try {
            return mapper.readValue(new URL(CENTRAL_AREA_URL), NamedRegion.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public NamedRegion[] fetchNoFlyZones() {
        try {
            return mapper.readValue(new URL(NO_FLY_ZONES_URL), NamedRegion[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Order[] fetchOrders(LocalDate date) {

        try {
            return mapper.readValue(new URL(ORDERS + "/" + date.toString()), Order[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
