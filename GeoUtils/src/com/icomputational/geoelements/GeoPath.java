/**
 * 
 */
package com.icomputational.geoelements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author icomputational
 * The {@link GeoPath} represents a geographic path, which consists of a list of coordinate.
 * This class uses an integer array to keep the coordinates to reduce memory footprint.
 */
public class GeoPath implements Iterable<Coordinate> {
    /**
     * Parse from coordinates in string.
     * @param coords coordinates in string.
     * @return a geographic path instance, null if failed.
     * @see Coordinate#parse(String)
     */
    public static GeoPath parse(String... coords) {
        GeoPath path = new GeoPath(coords.length * 2);
        for (int i = 0; i < coords.length; i++) {
            Coordinate coordinate = Coordinate.parse(coords[i]);
            if (coordinate == null) {
                return null;
            }

            path.set(i, coordinate);
        }

        return path;
    }

    private final int[] data;

    class IteratorImpl implements Iterator<Coordinate> {
        private int current;

        @Override
        public boolean hasNext() {
            return current < size();
        }

        @Override
        public Coordinate next() {
            if (current < size()) {
                return get(current++);
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private GeoPath(int size) {
        this.data = new int[size];
    }

    /**
     * Construct from raw data.
     * <b>Note: NO validation will be performed for raw data.</b>
     */
    public GeoPath(int[] rawData) {
        if (rawData.length % 2 != 0) {
            throw new IllegalArgumentException("The length of raw data should be multiple of 2");
        }

        this.data = copyOf(rawData);
    }

    /**
     * Construct from raw data.
     * <b>Note: NO validation will be performed for raw data.</b>
     */
    public GeoPath(double[] rawData) {
        if (rawData.length % 2 != 0) {
            throw new IllegalArgumentException("The length of raw data should be multiple of 2");
        }

        this.data = new int[rawData.length];
        for (int i = 0; i < rawData.length; i++) {
            data[i] = Coordinate.toDecimal(rawData[i]);
        }
    }

    /**
     * Construct from a list of coordinate.
     */
    public GeoPath(List<Coordinate> coordinates) {
        this(coordinates.toArray(new Coordinate[coordinates.size()]));
    }

    /**
     * Construct from an array of coordinates.
     */
    public GeoPath(Coordinate... coordinates) {
        this.data = new int[coordinates.length * 2];
        for (int i = 0; i < coordinates.length; i++) {
            set(i, coordinates[i]);
        }
    }

    private void set(int index, Coordinate coordinate) {
        int pos = index * 2;
        data[pos] = coordinate.decimalLongitude();
        data[pos + 1] = coordinate.decimalLatitude();
    }

    /**
     * Get the coordinate at specified index.
     */
    public Coordinate get(int index) {
        int pos = index * 2;
        return Coordinate.fromDecimal(data[pos], data[pos + 1]);
    }

    /**
     * Returns the coordinate at the middle index.
     * @return a coordinate at the middle or middle - 1.
     */
    public Coordinate middle() {
        // index = (size() + 1) / 2 - 1
        int pos = ((size() + 1) / 2 - 1) * 2;
        return Coordinate.fromDecimal(data[pos], data[pos + 1]);
    }

    /**
     * Convert the path to a list of coordinates.
     */
    public List<Coordinate> toCoordinates() {
        List<Coordinate> coords = new ArrayList<Coordinate>(data.length / 2);
        int size = size();
        for (int i = 0; i < size; i++) {
            coords.add(get(i));
        }
        return coords;
    }

    /**
     * Reverse the order of coordinates of this path.
     * @return a new path contains all coordinates in reversed order.
     */
    public GeoPath reverse() {
        GeoPath path = new GeoPath(data.length);
        for (int i = 0; i < data.length; i += 2) {
            int j = data.length - i - 2;
            path.data[j] = this.data[i];
            path.data[j + 1] = this.data[i + 1];
        }
        return path;
    }

    /**
     * Returns a copy of raw data.
     */
    public int[] intArray() {
        return copyOf(data);
    }

    /**
     * Create a copy of specified int array.
     */
    private int[] copyOf(int[] array) {
        if (array == null) {
            return null;
        }

        int[] copy = new int[array.length];
        if (array.length > 0) {
            System.arraycopy(array, 0, copy, 0, array.length);
        }
        return copy;
    }

    @Override
    public Iterator<Coordinate> iterator() {
        return new IteratorImpl();
    }

    /**
     * Returns the number of coordinates of this path.
     */
    public int size() {
        return data.length / 2;
    }

    /**
     * Returns true if this path has no coordinate.
     */
    public boolean isEmpty() {
        return data.length == 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GeoPath other = (GeoPath) obj;
        if (!Arrays.equals(data, other.data))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "GeoPath [data=" + Arrays.toString(data) + "]";
    }

}
