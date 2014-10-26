package com.icomputational.geometry.util;

import com.icomputational.geometry.Line;
import com.icomputational.geometry.Point;


/**
 * A {@link Segment} represents a line segment in a 2-dimension plane.
 */
class Segment extends Line {
    private final Point left;
    private final Point right;

    /**
     * Construct a line segment from two end points.
     */
    public Segment(Point p1, Point p2) {
        super(p1, p2);
        if (p1.x() < p2.x() || (p1.x() == p2.x() && p1.y() <= p2.y())) {
            left = p1;
            right = p2;
        } else {
            left = p2;
            right = p1;
        }
    }

    /**
     * Returns the point which X coordinate is less than another point,
     * or Y coordinate is less than another point when their X coordinate is equal.
     */
    public Point left() {
        return left;
    }

    /**
     * Returns the point which X coordinate is greater than another point,
     * or Y coordinate is greater than another point when their X coordinate is equal.
     */
    public Point right() {
        return right;
    }

    @Override
    public Point intersect(Line other) {
        Point ip = super.intersect(other);
        if (ip == null) {
            return null;
        }

        if (between(ip.x(), ip.y())) {
            return ip;
        } else {
            return null;
        }
    }

    /**
     * Calculate the interaction point with a line segment.
     * @param segment another line segment.
     * @return a point, null if two line are in parallel or the intersection point is out of line segment.
     */
    public Point intersect(Segment segment) {
        Point ip = super.intersect(segment);
        if (ip == null) {
            return null;
        }

        if (between(ip.x(), ip.y()) && segment.between(ip.x(), ip.y())) {
            return ip;
        } else {
            return null;
        }
    }

    /**
     * Check if the specified point is an end point of this segment.
     */
    public boolean isEndPoint(Point p) {
        return p.equals(left) || p.equals(right);
    }

    /**
     * Check if this segment joined with another segment.
     */
    public boolean isJoined(Segment segment) {
        return left.equals(segment.left) || left.equals(segment.right)
            || right.equals(segment.left) || right.equals(segment.right);
    }

    @Override
    public boolean contains(double x, double y) {
        return super.contains(x, y) && between(x, y);
    }

    private boolean between(double x, double y) {
        return x >= left.x() && x <= right.x() && between(y, left.y(), right.y());
    }

    private boolean between(double v, double a1, double a2) {
        if (a1 < a2) {
            return v >= a1 && v <= a2;
        } else {
            return v >= a2 && v <= a1;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + left.hashCode();
        result = prime * result + right.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Segment other = (Segment) obj;
        return left.equals(other.left) && right.equals(other.right);
    }

    @Override
    public String toString() {
        return "Segment [left=" + left + ", right=" + right + "]";
    }
}
