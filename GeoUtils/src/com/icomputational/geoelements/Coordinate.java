package com.icomputational.geoelements;

/**
 * @author icomputational
 * A {@link Coordinate} represents an immutable geographic coordinate on sphere 
 */
public class Coordinate implements Comparable<Coordinate> {
    private static final int MIN_LONGITUDE = -180;
    private static final int MAX_LONGITUDE = 180;
    private static final int MIN_LATITUDE = -90;
    private static final int MAX_LATITUDE = 90;
    
    private static final int MIN_INT_LONGITUDE = -180000000;
    private static final int MAX_INT_LONGITUDE = 180000000;
    private static final int MIN_INT_LATITUDE = -90000000;
    private static final int MAX_INT_LATITUDE = 90000000;
    
    private static double DECIMAL_EQUAL_THRESHOLD = 1E-6;
    
    private final double longitude;
    private final double latitude;
    
    /**
     * Construct a geography coordinate from decimal degrees (6 decimal places).
     * @see <a href="http://en.wikipedia.org/wiki/Decimal_degrees">Decimal Degrees</a>
     * @param lng the decimal degrees for longitude. 
     * @param lat the decimal degrees for latitude.
     */
    public static Coordinate fromDecimal(int lng, int lat) {
        if (lng < MIN_INT_LONGITUDE || lng > MAX_INT_LONGITUDE || lat < MIN_INT_LATITUDE || lat > MAX_INT_LATITUDE) {
            return null;
        }
        
        return new Coordinate(fromDecimal(lng), fromDecimal(lat));
    }
    
    /**
     * Parse coordinate from a string.
     * @param text string representation in the form: &lt;longitude&gt;,&lt;latitude&gt;
     * @return a coordinate instance, null if failed.
     */
    public static Coordinate parse(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        
        String[] pair = text.split(",");
        if (pair.length != 2) {
            return null;
        }
        
        try {
            final Double longitude = Double.parseDouble(pair[0]);
            final Double latitude = Double.parseDouble(pair[1]);
            Coordinate coord = new Coordinate(longitude, latitude);
            return coord.isValid() ? coord : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Construct a geography coordinate from longitude and latitude.
     * @param longitude the longitude.
     * @param latitude the latitude
     */
    public Coordinate(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
 
    /**
     * Check if this is a valid coordinate.
     */
    public boolean isValid() {
        return longitude >= MIN_LONGITUDE && longitude <= MAX_LONGITUDE && latitude >= MIN_LATITUDE
            && latitude <= MAX_LATITUDE;
    }
    
    /**
     * Get the longitude in radians.
     */
    public double longitudeRadians() {
        return Math.toRadians(longitude);
    }

    /**
     * Get longitude in decimal degrees.
     * @see #fromDecimal(int, int)
     */
    public int decimalLongitude() {
        return toDecimal(longitude);
    }

    /**
     * Get longitude of this coordinate.
     */
    public double longitude() {
        return longitude;
    }

    /**
     * Get the latitude in radians.
     */
    public double latitudeRadians() {
        return Math.toRadians(latitude);
    }

    /**
     * Get latitude in decimal degrees.
     * @see #fromDecimal(int, int)
     */
    public int decimalLatitude() {
        return toDecimal(latitude);
    }  
    
    /**
     * Get latitude of this coordinate.
     */
    public double latitude() {
        return latitude;
    }

    static final int toDecimal(double degree) {
        if (degree < 0) {
            return (int) (degree * 1E6 - 0.5);
        } else {
            return (int) (degree * 1E6 + 0.5);
        }
    }

    static final double fromDecimal(int decimal) {
        return (decimal * 1E-6);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = prime * result + (int) (temp ^(temp >>> 32));
        return result;
    }
    
    /**
     * Check if this coordinate is equal to specified coordinate in decimal representation.
     * The coordinates have same decimal representation if deviation of both longitude and latitude are less than 0.000001.
     * @param coord the coordinate to be compared.
     * @return true if two coordinates have same decimal representation.
     */
    public boolean decimallyEquals(Coordinate coord) {
        return Math.abs(this.longitude - coord.longitude) < DECIMAL_EQUAL_THRESHOLD
            && Math.abs(this.latitude - coord.latitude) < DECIMAL_EQUAL_THRESHOLD;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        
        if (obj == null)
            return false;
        
        if (getClass() != obj.getClass())
            return false;
        
        Coordinate other = (Coordinate)obj;
        if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
            return false;
        
        if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
            return false;
        
        return true;
    }
    
    @Override
    public int compareTo(Coordinate other) {
        int cmp = Double.compare(longitude, other.longitude);
        if (cmp == 0) {
            cmp = Double.compare(latitude, other.latitude);
        }
        return cmp;
    }
    
    /**
     * Returns string representation of this coordinate in the form: &lt;longitude&gt;,&lt;latitude&gt;
     */
    @Override
    public String toString() {
        return longitude + "," + latitude;
    }

}
