package uk.ac.ed.inf;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.LngLat;
import java.util.*;

/**
 * A* search algorithm:
 * - Modified from tutorial
 * - Algorithm avoids no-fly zones and uses Euclidean distance as a heuristic, or expected moves (outside central area)
 * - The findShortestPath method contains a flag 'outsideCentral' which when set to true, will break the execution when
 *   the central area is reached
 * - The outsideCentral flag also changes the heuristic function to expected moves
 *   (Euclidean distance / drone move distance) for faster routing
 */

public class A_Star {

    public static LngLatHandler lngLatHandler = new LngLatHandler();
    // an array with all 16 possible directions the drone can move
    private static final double[] DIRS = {0, 22.5, 45, 67.5, 90, 112.5, 135, 157.5, 180, 202.5, 225, 247.5, 270, 292.5, 315, 337.5};

    // global defined variables for the search
    static PriorityQueue<Cell> openSet;     // frontier
    static HashSet<Cell> openSetHash;       // frontier (for efficient lookup)
    static HashSet<Cell> closedSet;         // visited
    static List<Cell> path;                 // resulting path

    public static boolean findShortestPath(NamedRegion[] noFlyZones, NamedRegion centralArea,  Cell start, Cell goal, Boolean outsideCentral){
        // add start to the queue first
        openSet.add(start);
        // add start to the hash
        openSetHash.add(start);

        // once there is element in the queue, then keep running
        while (!openSet.isEmpty()){
            // get the cell with the lowest cost
            Cell current = openSet.poll();
            // remove the cell from the hash
            openSetHash.remove(current);
            // mark the cell to be visited
            closedSet.add(current);
            // if we want to break on center, then check if we are in the center
            if (outsideCentral){
                if (lngLatHandler.isInRegion(current.lngLat, centralArea)){
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

            // search neighbours i.e. all possible directions in all angles
            for(double dir : DIRS){
                // get the next location
                LngLat nextLngLat = lngLatHandler.nextPosition(current.lngLat, dir);
                Cell existing_neighbor = findNeighbor(nextLngLat);

                boolean isInNoFlyZone = false;
                if (noFlyZones != null) {
                    for (NamedRegion noFlyZone : noFlyZones) {
                        if (lngLatHandler.isInRegion(nextLngLat, noFlyZone)) {
                            isInNoFlyZone = true;
                            break;
                        }
                    }
                }
                boolean exitingCenter = !lngLatHandler.isInRegion(nextLngLat, centralArea) && !outsideCentral;
                boolean visited = closedSet.contains(existing_neighbor);

                // if the next location is in a not a no-fly-zone and not visited and not attempting to exit the center
                if (!isInNoFlyZone && !visited && !exitingCenter) {
                    // the g value of the next cell is the current cell's g value plus the distance between the two cells
                    double tentativeG = current.g + SystemConstants.DRONE_MOVE_DISTANCE;
                    // if the neighbor is not in the frontier, then add it to the frontier
                    if(existing_neighbor != null){
                        // Check if this path is better than any previously generated path to the neighbor
                        if(tentativeG < existing_neighbor.g){
                            // update cost, parent information
                            updateCell(goal, outsideCentral, current, tentativeG, existing_neighbor);
                        }
                    }
                    else{
                        // or directly add this cell to the frontier
                        Cell neighbor = new Cell(nextLngLat);
                        updateCell(goal, outsideCentral, current, tentativeG, neighbor);
                        openSet.add(neighbor);
                        openSetHash.add(neighbor);
                    }
                }
            }
        }
        // No path found
        return false;
    }

    private static void updateCell(Cell goal, Boolean breakOnCenter, Cell current, double tentativeG, Cell existing_neighbor) {
        existing_neighbor.parent = current;
        existing_neighbor.g = tentativeG;
        if (breakOnCenter){
            existing_neighbor.h = expectedMovesHeuristic(existing_neighbor, goal);
        }
        else {
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

    // Heuristic function that uses Euclidean distance (for use in central area)
    public static double euclideanHeuristic(Cell a, Cell b) {
        // number of moves is the distance divided by drone move distance, to the nearest move distance
        return lngLatHandler.distanceTo(a.lngLat, b.lngLat);
    }

    // Heuristic function that uses expected moves (for use outside central area)
    public static double expectedMovesHeuristic(Cell a, Cell b){
        return lngLatHandler.distanceTo(a.lngLat, b.lngLat) / SystemConstants.DRONE_MOVE_DISTANCE;
    }

    public static List<Cell> runA_Star(NamedRegion[] noFlyZones, NamedRegion centralArea, Cell start, Cell goal, Boolean breakOnCenter){
        // initialize the global variable
        openSet = new PriorityQueue<>(Comparator.comparingDouble(c -> c.f));
        openSetHash = new HashSet<>();
        closedSet = new HashSet<>();

        if(findShortestPath(noFlyZones, centralArea, start, goal, breakOnCenter)) {
            return path;
        }
        else{
            System.out.println("No path found");
            return null;
        }
    }
}

