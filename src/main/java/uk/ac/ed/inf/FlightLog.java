package uk.ac.ed.inf;

import org.geojson.FeatureCollection;
import org.geojson.Feature;
import org.geojson.LineString;
import org.geojson.LngLatAlt;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ed.inf.ilp.data.Order;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * FlightLog Class
    - A class for holding the flightpath data: deliveries, flightpath and drone flightpath (geojson)
    - Method for adding routes to the flightpath
    - Method for adding of deliveries to the deliveries list
    - Method for outputting each json file
 */

public class FlightLog {
    public List<Delivery> deliveries = new ArrayList<>();
    public List<Move> flightPath = new ArrayList<>();
    public List<Cell> droneFlightPath = new ArrayList<>();
    private final String deliveries_file_path;
    private final String flightpath_file_path;
    private final String drone_flightpath_file_path;

    public FlightLog(LocalDate date) {
        String base_url = "src/main/java/uk/ac/ed/inf/resultfiles/";
        this.deliveries_file_path = base_url + "deliveries-" + date + ".json";
        this.flightpath_file_path = base_url + "flightpath-" + date + ".json";
        this.drone_flightpath_file_path = base_url + "drone-"+ date + ".geojson";
    }

    public void logOrder(Order order){
        Delivery delivery = new Delivery(order.getOrderNo(), order.getOrderStatus().toString(), order.getOrderValidationCode().toString(), order.getPriceTotalInPence());
        deliveries.add(delivery);
    }

    public void logRoute(String orderNo, List<Cell> route) {
        droneFlightPath.addAll(route);
        for(int i =0; i<route.size()-1; i++){
            Move move = new Move(orderNo, route.get(i).lngLat, route.get(i+1).lngLat);
            flightPath.add(move);
        }
    }

    public void writeDeliveries() throws JsonProcessingException{
        // Takes the list of deliveries and makes it a list of JSon records
        ObjectMapper mapper = new ObjectMapper();
        // Convert list to JSON string
        String json = mapper.writeValueAsString(deliveries);
        // Then writes the json records to the file
        writeJSON(json, this.deliveries_file_path);
    }

    public void writeFlightpath() throws JsonProcessingException {
        // Takes the list of moves and makes it a list of JSon records
        ObjectMapper mapper = new ObjectMapper();
        // Convert list to JSON string
        String json = mapper.writeValueAsString(flightPath);
        // Then writes the json records to the file
        writeJSON(json, this.flightpath_file_path);
    }

    // write the drone flightpath to the file
    public void writeDroneFlightpath() {

        // check that the flightpath has been filled
        if (droneFlightPath == null){
            throw new IllegalStateException("The flightpath has not been generated");
        }

        String flightpathJson = "";
        // For every cell in the flightpath, add the LngLat to the LineString
        LineString lineString = new LineString();

        for (Cell cell : droneFlightPath) {
            LngLatAlt lngLatAlt = new LngLatAlt(cell.lngLat.lng(), cell.lngLat.lat());
            lineString.add(lngLatAlt);
        }
        try{
            // Create a Feature with Geometry Linestring
            Feature feature = new Feature();
            feature.setGeometry(lineString);
            // Create a FeatureCollection and add the Feature to it
            FeatureCollection featureCollection = new FeatureCollection();
            featureCollection.add(feature);
            // Use the Jackson ObjectMapper to map the Feature Collection to a JSON string
            ObjectMapper mapper = new ObjectMapper();
            flightpathJson = mapper.writeValueAsString(featureCollection);
        }
        catch (JsonProcessingException e) {
            System.out.println("Error converting drone path to geoJson");
        }
        // write the json string to the file
        writeJSON(flightpathJson, this.drone_flightpath_file_path);
    }

    private void writeJSON(String json, String filePath) {
        try {
            FileWriter file = new FileWriter(filePath);
            file.write(json);
            file.close();
        } catch (IOException e) {
            System.out.println("Error writing JSON to file");
        }
    }

}
