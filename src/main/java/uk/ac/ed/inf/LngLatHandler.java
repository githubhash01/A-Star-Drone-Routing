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
        // fast check if the region is the central region as special case where region is rectangular

        // else, assume the region is a simple polygon and use ray casting algorithm
        // if the number of intercepts is odd, then the point is inside the polygon
        return rayCastIntercepts(position, region) % 2 == 1;
    }

    @Override
    public LngLat nextPosition(LngLat startPosition, double angle) {

        // if angle = 999 - Drone Hovering
        if (angle == 999) {
            return startPosition;
        }
        // else assume angle is one of the 16 legal directions
        else {
            // calculate longitudinal and latitudinal components of movement in degrees
            double lngMovement = SystemConstants.DRONE_MOVE_DISTANCE * Math.cos(Math.toRadians(angle));
            double latMovement = SystemConstants.DRONE_MOVE_DISTANCE * Math.sin(Math.toRadians(angle));

            // return a new LngLat object with displacement added to startPosition
            return new LngLat(startPosition.lng() + lngMovement, startPosition.lat() + latMovement);
        }
    }

    // helper function for horizontal ray casting for isInRegion
    private int rayCastIntercepts(LngLat point, NamedRegion polygon) {
        int intercepts = 0;
        int max_longitude = 0; // sets furthest east point of the ray in the north sea :)

        double[][] ray = {{point.lng(), point.lat()}, {max_longitude, point.lat()}};
        int N = polygon.vertices().length;
        // iterate through for each pair i and i+1 including the last pair N-1 and 0
        for (int i = 0; i < N; i++) {
            double[][] edge = {{polygon.vertices()[i].lng(), polygon.vertices()[i].lat()}, {polygon.vertices()[(i + 1) % N].lng(), polygon.vertices()[(i + 1) % N].lat()}};
            if (lineIntersect(ray, edge)) {
                intercepts++;
            }
        }
        return intercepts;
    }

    // helper function for rayCast
    private boolean lineIntersect(double[][] ray, double[][] edge) {
        // calculate denominator of determinant formed by 2x2 matrix of edge and ray vectors
        double x1 = ray[0][0];
        double y1 = ray[0][1];
        double x2 = ray[1][0];
        double y2 = ray[1][1];
        double x3 = edge[0][0];
        double y3 = edge[0][1];
        double x4 = edge[1][0];
        double y4 = edge[1][1];

        double denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

        // if denominator is 0, lines are parallel or coincident
        return denominator != 0;

    }
}

