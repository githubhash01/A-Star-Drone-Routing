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

    /**
     * Tests if a point is inside a region by using a ray casting method
     * For each edge of the region, a ray is cast from the point in the positive x direction
     * If the final number of intercepts between ray and all edges is odd, then the point is inside the region
     */
    @Override
    public boolean isInRegion(LngLat position, NamedRegion region) {
        double epsilon = 1E-10;
        boolean inside = false;
        int N = region.vertices().length;

        // go through the region.vertices() and get pair of points that form an edge
        for (int i = 0; i < N; i++){
            LngLat A = region.vertices()[i];
            LngLat B = region.vertices()[(i+1)%N];

            // orientate the points so that A is below B
            LngLat[] AB = orientate(A, B);
            A = AB[0];
            B = AB[1];

            // if the point is out of bounds, then intercept impossible, so just continue
            if (outOfBounds(position, A, B)){
                continue;
            }

            // now check the harder case where the point is between the A and B (in both x and y direction)
            double m_edge = gradient(A, B);
            double m_point = gradient(A, position);

            // if the point is to the left of both A, B then an intercept is guaranteed (except if the edge is horizontal)
            if (position.lng() < Math.min(A.lng(), B.lng())){
                // ray method breaks down if both ray and edge are horizontal
                // shift the point up, and try again - because epsilon tiny, recursive call will be max 1 for any realistic region
                if (m_edge == 0 && m_point == 0){
                    return isInRegion(new LngLat(position.lng(), position.lat() + epsilon), region);
                }
                // otherwise intercept guaranteed
                inside = !inside;
                continue;
            }

            // if the gradients are equal, then the point lies on the border which is considered inside the region
            if (Double.compare(m_edge, m_point) == 0){
                inside = true;
                break;
            }

            // if the point is above the line, then the ray intercepts the edge, so flip the inside boolean
            if (m_point > m_edge){
                inside = !inside;
            }

        }
        // if there were an odd number of intercepts then inside will be true
        return inside;
    }

    @Override
    public LngLat nextPosition(LngLat startPosition, double angle) {

        // if angle = 999 - Drone Hovering
        if (angle == 999) {
            return startPosition;
        }
        // check if angle is not one of the 16 legal directions, and throw an error
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

    /**
     * Helper functions for isInRegion
     */

    private LngLat[] orientate(LngLat A, LngLat B){
        // A must always have a y value less than B
        if (A.lat() > B.lat()){
            LngLat temp = A;
            A = B;
            B = temp;
        }
        return new LngLat[]{A, B};
    }

    // if a point is out of bounds for an intercept, we can skip the edge
    private boolean outOfBounds(LngLat point, LngLat A, LngLat B){
        // if the point is above the entire line
        if (point.lat() > B.lat()){
            return true;
        }
        // if the point is below the entire line
        if (point.lat() < A.lat()){
            return true;
        }
        // if the point is the right of the entire line
        return point.lng() > Math.max(A.lng(), B.lng());
    }

    public double gradient(LngLat A, LngLat B){
        // catch divide by zero error where the line is vertical and the gradient is infinite/undefined
        if (A.lng() == B.lng()){
            return Double.POSITIVE_INFINITY;
        }
        // quickly find when the line is horizontal
        if (A.lat() == B.lat()){
            return 0;
        }
        // calculate the gradient of the line
        return (B.lat() - A.lat()) / (B.lng() - A.lng());
    }
}

