package com.icomputational.geometry;

/**
 * A {@link Projection} represents a projection to a 2-dimensional plane so that
 * we can create a simple R-tree index in 2-dimension.
 *
 * @param <C> the coordinate type.
 */
public interface Projection<C> {
    /**
     * Project a coordinate to a point on a 2-dimensional plane.
     * @param coordinate any coordinate.
     * @return a point on a 2-dimensional plane.
     */
    Point toPoint(C coordinate);

    /**
     * Reverse a point to a coordinate.
     * @param p a point on a 2-dimensional plane.
     * @return a coordinate.
     */
    C toCoordinate(Point p);
}