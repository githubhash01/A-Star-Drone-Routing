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
    public static final String  RESTAURANT_URL=  BASE + "/restaurants";
    public static final String ORDERS = BASE + "/orders";

    public static void fetchData() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        // getting the restaurants
        try {
            var restaurants = mapper.readValue(new URL(RESTAURANT_URL), Restaurant[].class);
            System.out.println("read all restaurants");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // getting the central area
        try {
            var centralArea = mapper.readValue(new URL(CENTRAL_AREA_URL), NamedRegion.class);
            System.out.println("read central area");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // getting no-fly zones
        try {
            var noFlyZones = mapper.readValue(new URL(NO_FLY_ZONES_URL), NamedRegion[].class);
            System.out.println("read no fly zones");
            for (NamedRegion noFlyZone : noFlyZones) {
                System.out.println(noFlyZone.name());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // getting the orders for a given day

        // insantiating a localdate object
        LocalDate date = LocalDate.of(2023, 9, 1);
        String dateAsString = date.toString();

        try{
            var orders = mapper.readValue(new URL(ORDERS + "/" + dateAsString), Order[].class);
            System.out.println("read orders");
            // print the credit card information for each order
            for (Order order : orders) {
                System.out.println(order.getCreditCardInformation());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        fetchData();
    }
}
