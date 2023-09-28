package uk.ac.ed.inf;

import com.google.gson.Gson;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * A very simple client to GET JSON data from a remote server
 */
public class ServerClient {
    String baseUrl = "https://ilp-rest.azurewebsites.net";

    // Endpoints
    String centralArea = "/centralArea";
    String noFlyZones = "/noFlyZones";
    String restaurants = "/restaurants";
    String orders = "/orders";
    String test = "/test";

}
