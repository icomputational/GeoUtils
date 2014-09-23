package com.icomputational.geometry;

/**
 * A {@link Shape} is an immutable shape on a 2-dimension plane.
 */
public abstract class Shape {
    /**
     * Returns the bounding box of this shape.
     */
    public abstract BoundingBox boundingBox();
    
    /**
     * Check if this shape contains specified point.
     * @param p a point.
     * @return true if the point is contained by this shape.
     */
    public boolean contains(Point p) {
        return contains(p.x(), p.y());
    }
    
    /**
     * Check if this shape contains specified coordinate.
     * @param x the X coordinate.
     * @param y the Y coordinate.
     * @return true if the coordinate is contained by this shape.
     */
    public abstract boolean contains(double x, double y);
    
    /**
     * Check if this shape overlaps specified bounding box.
     * @param bb a bounding box.
     * @return true if this polygon overlaps the bounding box. 
     */
    public abstract boolean overlaps(BoundingBox bb);
}
