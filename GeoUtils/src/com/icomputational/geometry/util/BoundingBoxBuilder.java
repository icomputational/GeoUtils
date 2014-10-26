package com.icomputational.geometry.util;

import com.icomputational.geometry.BoundingBox;

/**
 * A {@link BoundingBoxBuilder} provides a builder for {@link BoundingBox}.
 */
public class BoundingBoxBuilder {
    protected double minX;
    protected double minY;
    protected double maxX;
    protected double maxY;

    public BoundingBoxBuilder(BoundingBox bb) {
        minX = bb.minX();
        minY = bb.minY();
        maxX = bb.maxX();
        maxY = bb.maxY();
    }

    /**
     * Convert the data to a bounding box.
     */
    public BoundingBox toBoundingBox() {
        return new BoundingBox(minX, minY, maxX, maxY);
    }


    /**
     * Add a bounding box to this builder.
     */
    public void add(BoundingBox bb) {
        if (minX > bb.minX())
            minX = bb.minX();
        if (maxX < bb.maxX())
            maxX = bb.maxX();
        if (minY > bb.minY())
            minY = bb.minY();
        if (maxY < bb.maxY())
            maxY = bb.maxY();
    }

    /**
     * Returns the width of this bounding box.
     */
    public double width() {
        return maxX - minX;
    }

    /**
     * Returns the height of this bounding box.
     */
    public double height() {
        return maxY - minY;
    }

    /**
     * Get area of this bounding box.
     * @return the area of this bounding box.
     */
    public double area() {
        return (maxX - minX) * (maxY - minY);
    }
}
