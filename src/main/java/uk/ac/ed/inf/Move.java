package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

/**
 * Move includes
 * orderNo — the eight-character order number for the pizza order which the drone is currently collecting or delivering5;
 * • a value (floating-point) fromLongitude — the longitude of the drone at the start of this move;
 * • a value (floating-point) fromLatitude — the latitude of the drone at the start of this move;
 * • a value (floating-point) angle — the angle of travel of the drone in this move6;
 * • a value (floating-point) toLongitude — the longitude of the drone at the end of this move;
 * • a value (floating-point) toLatitude — the latitude of the drone at the end of this move
 */
public class Move {

    public String orderNo;
    public double fromLongitude;
    public double fromLatitude;
    public double angle;
    public double toLongitude;
    public double toLatitude;


    public Move(String orderNo, LngLat from, LngLat to) {
        this.orderNo = orderNo;
        this.fromLongitude = from.lng();
        this.fromLatitude = from.lat();
        this.toLongitude = to.lng();
        this.toLatitude = to.lat();
        this.angle = getAngle(fromLongitude, fromLatitude, toLongitude, toLatitude);
    }

    private double getAngle(double fromLongitude, double fromLatitude, double toLongitude, double toLatitude){
        // find the angle between the 'from' point -> infinite east and the line between the two points
        double angle = Math.atan2(toLatitude - fromLatitude, toLongitude - fromLongitude);

        // if the 'to' point is to the left of the 'from' point, then the angle will be negative, so wrap around
        if (angle < 0){
            angle += 2*Math.PI;
        }
        // convert to degrees and round to the closest 22.5-degree angle
        angle = Math.round(Math.toDegrees(angle)/22.5)*22.5;
        return angle;
    }


}
