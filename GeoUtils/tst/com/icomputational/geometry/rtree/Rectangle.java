package com.icomputational.geometry.rtree;

import com.icomputational.geometry.BoundingBox;
import com.icomputational.geometry.Point;
import com.icomputational.geometry.Shape;

/**
 * A {@link Rectangle} represents a rectangle in 2-dimension.
 */
public class Rectangle extends Shape {
    private final double minX;
    private final double minY;
    private final double maxX;
    private final double maxY;

    private final Point centre;

    /**
     * Construct a rectangle.
     * @param minX the minimum value in X axis
     * @param minY the minimum value in Y axis
     * @param maxX the maximum value in X axis
     * @param maxY the maximum value in Y axis
     */
    public Rectangle(double minX, double minY, double maxX, double maxY) {
        if (!(minX < maxX)) {
            throw new IllegalArgumentException("min X " + minX + " should be less than max X " + maxX);
        }
        if (!(minY < maxY)) {
            throw new IllegalArgumentException("min Y " + minY + " should be less than max Y " + maxY);
        }
        if (Double.isInfinite(maxX) || Double.isInfinite(maxY)) {
            throw new IllegalArgumentException("infinite value is not supported");
        }

        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;

        centre = new Point((minX + maxX) / 2, (minY + maxY) / 2);
    }

    /**
     * Returns the width of this rectangle.
     */
    public double width() {
        return maxX - minX;
    }

    /**
     * Returns the height of this rectangle.
     */
    public double height() {
        return maxY - minY;
    }

    /**
     * Returns the central point of this rectangle.
     */
    public Point centre() {
        return centre;
    }

    /**
     * Get area of this rectangle.
     * @return the area of this rectangle.
     */
    public double area() {
        return (maxX - minX) * (maxY - minY);
    }

    @Override
    public BoundingBox boundingBox() {
        return new BoundingBox(minX, minY, maxX, maxY);
    }

    @Override
    public boolean contains(double x, double y) {
        return minX <= x && maxX >= x && minY <= y && maxY >= y;
    }

    @Override
    public boolean overlaps(BoundingBox bb) {
        return this.minX < bb.maxX() && this.maxX > bb.minX()
            && this.minY < bb.maxY() && this.maxY > bb.minY();
    }
}
