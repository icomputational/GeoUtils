package com.icomputational.geometry;

/**
 * A {@link Vector} represents a vector on a plane.
 */
public class Vector {
    private final double dx;
    private final double dy;
    private final double length;

    public Vector(Point from, Point to) {
        this.dx = to.x() - from.x();
        this.dy = to.y() - from.y();
        length = Math.sqrt(dx * dx + dy * dy);
    }

    public Vector(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
        length = Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Construct a vector with known value.
     */
    private Vector(double dx, double dy, double length) {
        this.dx = dx;
        this.dy = dy;
        this.length = length;
    }

    /**
     * Returns the component at X axis.
     */
    public double dx() {
        return dx;
    }

    /**
     * Returns the component at Y axis.
     */
    public double dy() {
        return dy;
    }

    /**
     * Returns the length of a vector.
     */
    public double length() {
        return length;
    }

    /**
     * Calculate cross product with specified vector.
     * @return cross product.
     */
    public double cross(Vector v) {
        return dx * v.dy - v.dx * dy;
    }

    /**
     * Add this vector with specified vector.
     * @return a result vector.
     */
    public Vector add(Vector v) {
        double x = this.dx + v.dx;
        double y = this.dy + v.dy;
        return new Vector(x, y);
    }

    /**
     * Reverse the direction of this vector.
     */
    public Vector reverse() {
        return new Vector(-dx, -dy, length);
    }

    /**
     * Normalize this vector to a unit vector.
     * @return a unit vector, null if this vector is a zero vector.
     */
    public Vector normalize() {
        if (length == 1D) {
            return this;
        } else if (length <= 0D) {
            // an arbitrary vector
            return this;
        }

        return new Vector(dx / length, dy / length, 1D);
    }

    /**
     * Rotate this vector for 90 degree clockwise.
     * @return a new vector.
     */
    public Vector turnRight() {
        return new Vector(dy, -dx, length);
    }

    /**
     * Rotate this vector for 90 degree counter clockwise.
     * @return a new vector.
     */
    public Vector turnLeft() {
        return new Vector(-dy, dx, length);
    }

    /**
     * Resize by multiplying with a number value.
     */
    public Vector multiply(double value) {
        double len = length * value;
        if (value < 0) {
            len = -len;
        }
        return new Vector(dx * value, dy * value, len);
    }
}

