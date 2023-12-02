package uk.ac.ed.inf;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.LngLat;
import java.util.*;

/**
 * A* search algorithm:
 * - Modified from tutorial
 * - The findShortestPath method contains flags 'breakOnExitCentral' and 'stayInCentral' that determine the behaviour
 *   of the algorithm when dealing with the central area
 * - Two heuristic functions, euclidean distance (for use inside central area), and expected moves
 *   (Euclidean distance / drone move distance) for faster routing outside the central area
 */

public class A_Star {

    public static LngLatHandler lngLatHandler = new LngLatHandler();
    // an array with all 16 possible directions the drone can move
    private static final double[] DIRS = {0, 22.5, 45, 67.5, 90, 112.5, 135, 157.5, 180, 202.5, 225, 247.5, 270, 292.5, 315, 337.5};

    // global-defined variables for the search
    static PriorityQueue<Cell> openSet;     // frontier
    static HashSet<Cell> openSetHash;       // frontier (for efficient lookup)
    static HashSet<Cell> closedSet;         // visited
    static List<Cell> path;                 // resulting path

    public static boolean findShortestPath(Cell start, Cell goal, NamedRegion[] noFlyZones, NamedRegion centralArea, Boolean breakOnExitCentral, Boolean stayInCentral){
        // add start to the queue first
        openSet.add(start);
        // add start to the hash
        openSetHash.add(start);

        // we are routing outside the central area if both breakOnExitCentral and stayInCentral are false
        boolean routingOutside = !breakOnExitCentral && !stayInCentral;

        // once there is an element in the queue, then keep running
        while (!openSet.isEmpty()){
            // get the cell with the lowest cost
            Cell current = openSet.poll();
            // remove the cell from the hash
            openSetHash.remove(current);
            // mark the cell to be visited
            closedSet.add(current);


            // if the goal is to break on exiting central, return the path when we exit the central area
            if (breakOnExitCentral){
                if (!lngLatHandler.isInRegion(current.lngLat, centralArea)){
                    path = new ArrayList<>();
                    while (current != null) {
                        path.add(current);
                        current = current.parent;
                    }
                    Collections.reverse(path);
                    return true;
                }
            }

            // otherwise, we want to go directly to the goal, and only exit then
            if (lngLatHandler.isCloseTo(current.lngLat, goal.lngLat)) {
                path = new ArrayList<>();
                while (current != null) {
                    path.add(current);
                    current = current.parent;
                }
                Collections.reverse(path);
                return true;
            }

            // search neighbours
            for(double dir : DIRS){
                // get the next location
                LngLat nextLngLat = lngLatHandler.nextPosition(current.lngLat, dir);
                Cell existing_neighbor = findNeighbor(nextLngLat);

                // if we are supposed to stay in the central area, then it is illegal to exit
                boolean illegalExit = false;
                if (stayInCentral){
                    if (!lngLatHandler.isInRegion(nextLngLat, centralArea)){
                        illegalExit = true;
                    }
                }
                boolean isInNoFlyZone = false;
                if (noFlyZones != null) {
                    for (NamedRegion noFlyZone : noFlyZones) {
                        if (lngLatHandler.isInRegion(nextLngLat, noFlyZone)) {
                            isInNoFlyZone = true;
                            break;
                        }
                    }
                }
                //boolean exitingCenter = !lngLatHandler.isInRegion(nextLngLat, centralArea) && !outsideCentral;
                boolean visited = closedSet.contains(existing_neighbor);


                if (!isInNoFlyZone && !visited && !illegalExit) {
                    // the g value of the next cell is the current cell's g value plus the distance between the two cells
                    double tentativeG = current.g + SystemConstants.DRONE_MOVE_DISTANCE;
                    // if the neighbor is not in the frontier, then add it to the frontier
                    if(existing_neighbor != null){
                        // Check if this path is better than any previously generated path to the neighbor
                        if(tentativeG < existing_neighbor.g){
                            // update cost, parent information
                            updateCell(goal, current, tentativeG, existing_neighbor, routingOutside);
                        }
                    }
                    else{
                        // or directly add this cell to the frontier
                        Cell neighbor = new Cell(nextLngLat);
                        updateCell(goal, current, tentativeG, neighbor, routingOutside);
                        openSet.add(neighbor);
                        openSetHash.add(neighbor);
                    }
                }
            }
        }
        // No path found
        return false;
    }

    private static void updateCell(Cell goal, Cell current, double tentativeG, Cell existing_neighbor, boolean routingOutsideCentral) {
        existing_neighbor.parent = current;
        existing_neighbor.g = tentativeG;

        if (routingOutsideCentral){
            existing_neighbor.h = outsideCentralHeuristic(existing_neighbor, goal);
        }
        else{
            existing_neighbor.h = euclideanHeuristic(existing_neighbor, goal);
        }
        existing_neighbor.f = existing_neighbor.g + existing_neighbor.h;
    }

    // finds the next cell to visit using the openSet
    public static Cell findNeighbor(LngLat lngLat){
        if(openSetHash.isEmpty()){
            return null;
        }

        Iterator<Cell> iterator = openSetHash.iterator();

        Cell find = null;
        while (iterator.hasNext()) {
            Cell next = iterator.next();
            if(next.lngLat.equals(lngLat)){
                find = next;
                break;
            }
        }
        return find;
    }

    // Heuristic function that uses Euclidean distance
    public static double euclideanHeuristic(Cell a, Cell b) {
        return lngLatHandler.distanceTo(a.lngLat, b.lngLat);
    }

    // Heuristic function that uses expected moves (Euclidean distance / drone move distance)
    public static double outsideCentralHeuristic(Cell a, Cell b) {
        return lngLatHandler.distanceTo(a.lngLat, b.lngLat) / SystemConstants.DRONE_MOVE_DISTANCE;
    }


    public static List<Cell> runA_Star(NamedRegion[] noFlyZones, NamedRegion centralArea, Cell start, Cell goal, Boolean breakOnExitCentral, Boolean stayInCentral){
        // initialize the global variable
        openSet = new PriorityQueue<>(Comparator.comparingDouble(c -> c.f));
        openSetHash = new HashSet<>();
        closedSet = new HashSet<>();
        path = new ArrayList<>();

        if(findShortestPath(start, goal, noFlyZones, centralArea, breakOnExitCentral, stayInCentral)) {
            return path;
        }
        else{
            // run time exception
            throw new RuntimeException("No path found");
        }
    }
}