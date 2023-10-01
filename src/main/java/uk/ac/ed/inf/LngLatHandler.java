package uk.ac.ed.inf;

/*
A class that implements the LngLatHandler interface
 */

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;
import uk.ac.ed.inf.ilp.constant.SystemConstants;

public class LngLatHandler implements LngLatHandling {
    @Override
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        // euclidean distance formula: sqrt((x1-x2)^2 + (y1-y2)^2)
        return Math.sqrt(Math.pow((startPosition.lng() - endPosition.lng()), 2) + Math.pow((startPosition.lat() - endPosition.lat()), 2));
    }

    @Override
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        // if the distance is strictly less than 'DRONE_IS_CLOSE_DISTANCE' (1.5E-4) then return true
        return distanceTo(startPosition, otherPosition) < SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }

    @Override
    public boolean isInRegion(LngLat position, NamedRegion region) {

        // TODO - implement a fast check for central region based on the fact that it is a rectangle
        int max_east = 180; // effectively infinity for the purposes of this algorithm in the context of Edinburgh
        int intercepts = 0;

        double[][] ray = {{position.lng(), position.lat()}, {max_east, position.lat()}};
        int N = region.vertices().length;
        // iterate through for each pair i and i+1 including the last pair N-1 and 0, counting intercepts
        for (int i = 0; i < N; i++) {
            double[][] edge = {{region.vertices()[i].lng(), region.vertices()[i].lat()}, {region.vertices()[(i + 1) % N].lng(), region.vertices()[(i + 1) % N].lat()}};
            if (lineIntersect(ray, edge)) {
                intercepts++;
            }
        }
        return intercepts % 2 == 1; // if odd number of intercepts, then point is inside polygon
    }

    @Override
    public LngLat nextPosition(LngLat startPosition, double angle) {

        // if angle = 999 - Drone Hovering
        if (angle == 999) {
            return startPosition;
        }
        // else check angle is not one of the 16 legal directions, and throw an error
        else if (angle % 22.5 != 0 || angle < 0 || angle >= 360) {
            throw new IllegalArgumentException("Angle must be one of the 16 tertiary compass directions");
        }
        else {
            // calculate longitudinal and latitudinal components of movement in degrees
            double lngMovement = SystemConstants.DRONE_MOVE_DISTANCE * Math.cos(Math.toRadians(angle));
            double latMovement = SystemConstants.DRONE_MOVE_DISTANCE * Math.sin(Math.toRadians(angle));

            // return a new LngLat object with displacement added to startPosition
            return new LngLat(startPosition.lng() + lngMovement, startPosition.lat() + latMovement);
        }
    }

    // helper function for inRegion to test if a ray intersects an edge
    public boolean lineIntersect(double[][] ray, double[][] edge)
    {
        // calculate denominator of determinant formed by 2x2 matrix of edge and ray vectors
        double ray_origin_lng = ray[0][0];
        double ray_origin_lat = ray[0][1];
        double ray_final_lng = ray[1][0];
        double ray_final_lat = ray[1][1]; // should always be the same as ray_origin_lat

        double edge_start_lng = edge[0][0];
        double edge_start_lat = edge[0][1];
        double edge_final_lng = edge[1][0];
        double edge_final_lat = edge[1][1];

        // If entire edge is above the ray - intersection impossible
        if (edge_start_lat > ray_origin_lat && edge_final_lat > ray_origin_lat) {
            return false;
        }
        // If entire edge is below the ray - intersection impossible
        if (edge_start_lat < ray_origin_lat && edge_final_lat < ray_origin_lat) {
            return false;
        }
        // If entire edge is to the left of the ray - intersection impossible
        if (edge_start_lng < ray_origin_lng && edge_final_lng < ray_origin_lng) {
            return false;
        }
        // if denominator is 0, lines are parallel or coincident - so no intersection
        double denominator = (ray_origin_lng - ray_final_lng) * (edge_start_lat - edge_final_lat) - (ray_origin_lat - ray_final_lat) * (edge_start_lng - edge_final_lng);
        if (denominator == 0) {
            return false;
        }
        // finding the point of intersection
        // finally check that the intercept is to the right of the ray origin
        double a = (ray_origin_lng * ray_final_lat - ray_origin_lat * ray_final_lng);
        double b = (edge_start_lng * edge_final_lat - edge_start_lat * edge_final_lng);
        double Px = (a * (edge_start_lng - edge_final_lng) - (ray_origin_lng - ray_final_lng) * b) / denominator;
        return !(Px < ray_origin_lng);

    }
}

