package uk.ac.ed.inf;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.LngLat;
import java.util.*;


public class A_Star {

    public static LngLatHandler lngLatHandler = new LngLatHandler();
    // an array with possible movement directions in degrees ranging from 0-360 in 22.5 degree increments

    // TODO change this to 16 directions

    private static final double[] RESTRICTED_DIRS = {0, 45, 90, 135, 180, 225, 270, 315};
    private static final double[] DIRS = {0,22.5, 45, 67.5, 90, 112.5, 135, 157.5, 180, 202.5, 225, 247.5, 270, 292.5, 315, 337.5};

    // global defined variables for the search
    static PriorityQueue<Cell> openSet;     // frontier
    static HashSet<Cell> closedSet;         // visited
    static List<Cell> path;                 // resulting path

    static Cell centralStart; //

    // A* search algorithm for getting inside the central area from outside the central area
    public static boolean findPathToCentral(NamedRegion[] noFlyZones, NamedRegion centralArea,  Cell start, Cell goal){
        // add start to the queue first
        openSet.add(start);

        // once there is element in the queue, then keep running
        while (!openSet.isEmpty()){

            // get the cell with the lowest cost
            Cell current = openSet.poll();

            /**
             * If the cell is inside the central area then we have found the path to the central area
             * Early exit
             */

            if (lngLatHandler.isInRegion(current.lngLat, centralArea)) {
                centralStart = current;
                path = new ArrayList<>();
                while (current != null) {
                    path.add(current);
                    current = current.parent;
                }
                Collections.reverse(path);
                return true;
            }
            // mark the cell to be visited
            closedSet.add(current);

            // search neighbours i.e. all possible directions in all angles
            for(double dir : DIRS){
                // get the next location
                LngLat nextLngLat = lngLatHandler.nextPosition(current.lngLat, dir);

                // check to see if the next location is in any of the no-fly zones
                boolean isInNoFlyZone = false;
                if (noFlyZones != null) {
                    for (NamedRegion noFlyZone : noFlyZones) {
                        if (lngLatHandler.isInRegion(nextLngLat, noFlyZone)) {
                            isInNoFlyZone = true;
                            break;
                        }
                    }
                }
                // if the next location is in a no-fly zone, then skip it
                if(isInNoFlyZone || closedSet.contains(new Cell(nextLngLat))) {
                    continue;
                }

                // otherwise
                else{
                    // the g value of the next cell is the current cell's g value plus the distance between the two cells
                    double tentativeG = current.g + lngLatHandler.distanceTo(current.lngLat, nextLngLat);
                    // find the cell if it is in the frontier but not visited to see if cost updating is needed
                    Cell existing_neighbor = findNeighbor(nextLngLat);

                    // if the neighbor is not in the frontier, then add it to the frontier
                    if(existing_neighbor != null){
                        // Check if this path is better than any previously generated path to the neighbor
                        if(tentativeG < existing_neighbor.g){
                            // update cost, parent information
                            existing_neighbor.parent = current;
                            existing_neighbor.dir = dir;
                            existing_neighbor.g = tentativeG;
                            existing_neighbor.h = heuristic(existing_neighbor, goal);
                            existing_neighbor.f = existing_neighbor.g + existing_neighbor.h;
                        }
                    }
                    else{
                        // or directly add this cell to the frontier
                        Cell neighbor = new Cell(nextLngLat);
                        neighbor.parent = current;
                        neighbor.dir = dir;
                        neighbor.g = tentativeG;
                        neighbor.h = heuristic(neighbor, goal);
                        neighbor.f = neighbor.g + neighbor.h;
                        openSet.add(neighbor);
                    }

                }

            }

        }
        // No path found
        return false;

    }

    // A* search algorithm for inside the central area
    public static boolean findShortestPath(NamedRegion[] noFlyZones, NamedRegion centralArea,  Cell start, Cell goal){
        // add start to the queue first
        openSet.add(start);

        // once there is element in the queue, then keep running
        while (!openSet.isEmpty()){

            // get the cell with the lowest cost
            Cell current = openSet.poll();

            // mark the cell to be visited
            closedSet.add(current);
            // find the goal: early exit
            if(lngLatHandler.isCloseTo(current.lngLat, goal.lngLat)){
            // Reconstruct the path: trace by find the parent cell
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

                // check to see if the next location is in any of the no-fly zones
                boolean isInNoFlyZone = false;
                if (noFlyZones != null) {
                    for (NamedRegion noFlyZone : noFlyZones) {
                        if (lngLatHandler.isInRegion(nextLngLat, noFlyZone)) {
                            isInNoFlyZone = true;
                            break;
                        }
                    }
                }
                // if the next location is in a no-fly zone, then skip it
                if(isInNoFlyZone || closedSet.contains(new Cell(nextLngLat))) {
                    continue;
                }

                // otherwise
                else{
                    // the g value of the next cell is the current cell's g value plus the distance between the two cells
                    double tentativeG = current.g + lngLatHandler.distanceTo(current.lngLat, nextLngLat);
                    // find the cell if it is in the frontier but not visited to see if cost updating is needed
                    Cell existing_neighbor = findNeighbor(nextLngLat);

                    // if the neighbor is not in the frontier, then add it to the frontier
                    if(existing_neighbor != null){
                        // Check if this path is better than any previously generated path to the neighbor
                        if(tentativeG < existing_neighbor.g){
                            // update cost, parent information
                            existing_neighbor.parent = current;
                            existing_neighbor.dir = dir;
                            existing_neighbor.g = tentativeG;
                            existing_neighbor.h = heuristic(existing_neighbor, goal);
                            existing_neighbor.f = existing_neighbor.g + existing_neighbor.h;
                        }
                    }
                    else{
                        // or directly add this cell to the frontier
                        Cell neighbor = new Cell(nextLngLat);
                        neighbor.parent = current;
                        neighbor.dir = dir;
                        neighbor.g = tentativeG;
                        neighbor.h = heuristic(neighbor, goal);
                        neighbor.f = neighbor.g + neighbor.h;
                        openSet.add(neighbor);
                    }

                }

            }

        }
        // No path found
        return false;

    }

    // finds the next cell to visit using the openSet
    public static Cell findNeighbor(LngLat lngLat){
        if(openSet.isEmpty()){
            return null;
        }

        Iterator<Cell> iterator = openSet.iterator();

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
    public static double heuristic(Cell a, Cell b) {
        return lngLatHandler.distanceTo(a.lngLat, b.lngLat);
    }

    public static List<Cell> runA_Star(NamedRegion[] noFlyZones, NamedRegion centralArea, Cell start, Cell goal){
        // initialize the global variable
        openSet = new PriorityQueue<>(Comparator.comparingDouble(c -> c.f));
        closedSet = new HashSet<>();
        /*
        // Run A* search
        if(findShortestPath(noFlyZones, centralArea, start, goal)){
            // print the path by going through the path list and printing the lngLat
            return path;
        }
        else{
            System.out.println("No path found");
        }
        return null;
        */

        // first find the path to the central area
        if(findPathToCentral(noFlyZones, centralArea, start, goal)){
            // print something to show we are doing something here
            System.out.println("Found path to central area");
            openSet = new PriorityQueue<>(Comparator.comparingDouble(c -> c.f));
            closedSet = new HashSet<>();
            //path = new ArrayList<>();

        }
        else{
            System.out.println("No path found");
        }

        // now find the path to the goal starting from the central area
        if(findShortestPath(noFlyZones, centralArea, centralStart, goal)){
            // print the path by going through the path list and printing the lngLat
            return path;
            /*
            pathToCentral.addAll(path);
            return pathToCentral;

             */
        }
        else{
            System.out.println("No path found");
        }
        return null;
    }
}


