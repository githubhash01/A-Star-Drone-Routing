package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

import java.util.Objects;

public class Cell {

    LngLat lngLat;
    double f, g, h; // A* algorithm value parameters
    Cell parent; // parent record: come from
    Double dir; // the direction required to get from parent to this cell

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
