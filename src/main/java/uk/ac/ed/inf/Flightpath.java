package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

/**
 * Flightpath Class
 * -- A class for holding the flightpath data
 * -- Method for adding routes to the flightpath
 * -- Method for output of flightpath to json
 */

public class Flightpath {

    // initialise the flightpath List<Cell> as null

    public List<Cell> flightPath = new ArrayList<>();

    public void addOrderRoute(String OrderNo, List<Cell> route) {
        flightPath.addAll(route);
    }

    public void writeFlightpath() {
        // write the flightpath to the file
        // TODO
    }

    public void writeDroneFlightpath() {
        // write the drone flightpath to the file
        // check that the flightpath has been filled
        if (flightPath == null){
            throw new IllegalStateException("The flightpath has not been generated");
        }

        // use streams to convert List<Cell> to a List<Point> of coordinates directly
        List<Point> lineStringCoordinates = flightPath.stream().map(cell -> Point.fromLngLat(cell.lngLat.lng(), cell.lngLat.lat())).toList();

        LineString lineString = LineString.fromLngLats(lineStringCoordinates);

        // Create a Feature with the LineString geometry
        Feature feature = Feature.fromGeometry(lineString);

        // Create a FeatureCollection and add the Feature to it
        FeatureCollection featureCollection = FeatureCollection.fromFeature(feature);

        // Convert the FeatureCollection to a JSON string
        String jsonString = featureCollection.toJson();

        // Write the GeoJSON string to a file in folder "output" with the name "flightpath.geojson"
        byte[] data = jsonString.getBytes();
        Path p = Paths.get("src/main/java/uk/ac/ed/inf/resultfiles/flightpath.geojson");

        // if the file has contents, then delete them
        try {
            Files.deleteIfExists(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // write the new contents to the file
        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(p, CREATE, APPEND))) {
            out.write(data, 0, data.length);
        } catch (IOException x) {
            System.err.println(x);
        }
    }
}
