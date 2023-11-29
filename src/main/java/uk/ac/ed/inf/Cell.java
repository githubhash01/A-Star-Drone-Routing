package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import java.util.Objects;

/**
 * Cell class that represents a cell in the grid
 * - Modified from Tutorial
 * - Each cell has a longitude and latitude
 * - f = g + h (where g is cost to, and h is the heuristic or expected cost to goal)
 */

public class Cell {

    LngLat lngLat;
    double f, g, h; // A* algorithm value parameters
    Cell parent; // parent record: come from

    public Cell(LngLat lngLat) {
        this.lngLat = lngLat;
        parent = null;
        f = 0;
        g = 0;
        h = 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lngLat);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Cell other = (Cell) obj;
        return other.lngLat.equals(lngLat);
    }

}
