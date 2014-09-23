package com.icomputational.geometry;

/**
 * @author icomputational
 * A {@link Point} represents a point in a 2-dimension plane.
 */
public class Point {
    private final double x;
    private final double y;
    
    /**
     * Construct from specified coordinate.
     * @param x the X coordinate.
     * @param y the Y coordinate.
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Returns the x coordinate of this point.
     */
    public double x() {
        return x;
    }

    /**
     * Returns the y coordinate of this point.
     */
    public double y() {
        return y;
    }

    /**
     * Calculate the distance to specified point.
     * @param p another point.
     * @return the distance to the point.
     */
    public double distance(Point p) {
        return distance(p.x, p.y);
    }
    
    /**
     * Calculate the distance to specified coordinate.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return the distance.
     */
    public double distance(double x, double y) {
        double dx = this.x - x;
        double dy = this.y - y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Returns true if both X and Y coordinate of this point is a valid number.
     */
    public boolean isValid() {
        return !Double.isNaN(x) && !Double.isNaN(y);
    }
    
    /**
     * Test if coordinate of this point between the specified two points, exclusively.
     * @param p1 first point
     * @param p2 second point
     * @return true if the coordinate of this point between the coordinate of specified two point in any axis.
     */
    public boolean between(Point p1, Point p2) {
        return ((p1.x < p2.x) ? (this.x > p1.x && this.x < p2.x) : (this.x > p2.x && this.x < p1.x)) &&
                ((p1.y < p2.y) ? (this.y > p1.y && this.y < p2.y) : (this.y > p2.y && this.y < p1.y));
    }
    
    /**
     * Calculate an interpolation point with specified target point and proportion.
     * @param proportion the relative proportion of the interpolation position, 0 for left, 1 for right.
     * @return an interpolation point.
     */
    public Point interpolate(Point to, double proportion) {
        if (proportion == 0D || this.equals(to)) {
            return this;
        } else if (proportion == 1D) {
            return to;
        }

        return new Point(x + (to.x - x) * proportion, y + (to.y - y) * proportion);
    }
    
    /**
     * Check if both x and y of this point are almost zero consider the computation deviation.
     * @return true if this point is almost zero.
     */
    public boolean almostZero() {
        return Geometry.almostZero(x) && Geometry.almostZero(y);
    }

    /**
     * Check if this point is identical to specified point.
     */
    public boolean isIdentical(Point p) {
        if (this == p) {
            return true;
        }

        return Geometry.almostEquals(x, p.x) && Geometry.almostEquals(y, p.y);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long tmp = Double.doubleToLongBits(x);
        result = prime * result + (int) (tmp ^ (tmp >>> 32));
        tmp = Double.doubleToLongBits(y);
        result = prime * result + (int) (tmp ^ (tmp >>> 32));
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
        Point other = (Point)obj;
        if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
            return false;
        if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "Point (" + x + ", " + y + ")";
    }
}
