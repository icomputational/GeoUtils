package com.icomputational.geometry;

/**
 * A {@link Line} represents a line on a 2-dimension plane.
 */
public class Line {
    // variable to define the formula ax + by = c which denote a line
    private final double a;
    private final double b;
    private final double c;

    /**
     * Get a new vertical line instance.
     * @param x the x coordinate of the vertical line.
     */
    public static Line verticalLine(double x) {
        return new Line(1D, 0D, x);
    }

    /**
     * Get a new horizontal line instance.
     * @param y the y coordinate of the horizontal line.
     */
    public static Line horizontalLine(double y) {
        return new Line(0D, 1D, y);
    }
    
    /**
     * Construct a line from a formula ax + by = c.
     */
    public Line(double a, double b, double c) {
        if (a == 0D && b == 0D) {
            throw new IllegalArgumentException("both a and b are 0");
        }
        this.a = a;
        this.b = b;
        this.c = c;
    }
    
    /**
     * Construct a line from two points.
     * @throws IllegalArgumentException if two points are same.
     */
    public Line(Point p1, Point p2) {
        if (p1.equals(p2)) {
            throw new IllegalArgumentException("two points are same");
        }
        a = p2.y() - p1.y();
        b = p1.x() - p2.x();
        c = p1.x() * p2.y() - p2.x() * p1.y();
    }
    
    /**
     * Calculate the intersection point with another line.
     * @param other a line
     * @return a point, null if two line are in parallel.
     */
    public Point intersect(Line other) {
        double v1 = this.a * other.b - this.b * other.a;
        if (Geometry.almostZero(v1)) {
            // they are parallel
            return null;
        }

        double x = (this.c * other.b - this.b * other.c) / v1;
        double y = (this.a * other.c - this.c * other.a) / v1;
        if (Double.isInfinite(x) || Double.isInfinite(y)) {
            return null;
        }

        return new Point(x, y);
    }

    /**
     * Get x value for specified y coordinate on this line.
     * @param y y coordinate value.
     * @return x value, Double.NaN this line is a horizontal line.
     */
    public double getX(double y) {
        if (a == 0D) {
            return Double.NaN;
        }

        return (c - b * y) / a;
    }

    /**
     * Get y value for specified x on this line.
     * @param x x coordinate value.
     * @return y value, Double.NaN if this line is a vertical line.
     */
    public double getY(double x) {
        if (b == 0D) {
            return Double.NaN;
        }

        return (c - a * x) / b;
    }

    /**
     * If this line is vertical line.
     */
    public boolean isVertical() {
        return Geometry.almostZero(b);
    }

    /**
     * If this line is horizontal line.
     */
    public boolean isHorizontal() {
        return Geometry.almostZero(a);
    }

    /**
     * Check if the specified point on this line.
     * @param p a point.
     * @return true if the point found on this line.
     */
    public boolean contains(Point p) {
        return contains(p.x(), p.y());
    }

    /**
     * Check if specified coordinate is on this line.
     */
    public boolean contains(double x, double y) {
        return Geometry.almostEquals(a * x + b * y, c);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(a);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(b);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(c);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        Line other = (Line) obj;
        if (Double.doubleToLongBits(a) != Double.doubleToLongBits(other.a))
            return false;
        if (Double.doubleToLongBits(b) != Double.doubleToLongBits(other.b))
            return false;
        if (Double.doubleToLongBits(c) != Double.doubleToLongBits(other.c))
            return false;
        return true;
    }

    /**
     * Check if this line is identical with another line.
     */
    public boolean isIdentical(Line line) {
        if (this.equals(line)) {
            return true;
        }

        return Geometry.almostEquals(a * line.b, line.a * b) && Geometry.almostEquals(a * line.c, line.a * c)
            && Geometry.almostEquals(b * line.c, line.b * c);
    }   
}
