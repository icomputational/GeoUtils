package com.icomputational.geometry;

import java.util.Arrays;
import java.util.List;

/**
 * A {@link LinearRing} represents a linear ring as defined in OpenGIS.
 * It's a simple polygon defined in computational geometry.
 */
public class LinearRing {
    /**
     * Constant value denote clockwise order.
     * @see #clockwise()
     */
    public static final int CLOCKWISE = -1;
    
    /**
     * Constant value denote counter clockwise order.
     * @see #clockwise()
     */
    public static final int COUNTER_CLOCKWISE = 1;
    
    /**
     * Constant value denote vertices are colinear points.
     * @see #clockwise()
     */
    public static final int COLINEAR = 0;
    
    private final double[] ax;
    private final double[] ay;
    private final BoundingBox bb;
    
    public LinearRing(List<Point> vertices) {
        int cnt = vertices.size();
        if (cnt < 3) {
            throw new IllegalArgumentException("a polygon needs 3 vertices at least");
        }
        
        this.ax = new double[cnt];
        this.ay = new double[cnt];
        
        int i = 0;
        for (Point p : vertices) {
            this.ax[i] = p.x();
            this.ay[i] = p.y();
            ++i;
        }
        
        bb = computeBoundingBox();
    }
    
    /**
     * Returns the bounding box of this linear ring.
     */
    public BoundingBox boundingBox() {
        return bb;
    }
    
    private BoundingBox computeBoundingBox() {
        double minX = this.ax[0];
        double minY = this.ay[0];
        double maxX = minX;
        double maxY = minY;
        
        for (int i = 1; i < ax.length; ++i) {
            double x = ax[i];
            if (minX > x) {
                minX = x;
            } else if (maxX < x) {
                maxX = x;
            }

            double y = ay[i];
            if (minY > y) {
                minY = y;
            } else if (maxY < y) {
                maxY = y;
            }
        }
        
        return new BoundingBox(minX, minY, maxX, maxY);
    }
    
    /**
     * Returns the number of intersections with a ray from specified coordinate.
     * @see <a href="http://graphics.cs.ucdavis.edu/~okreylos/TAship/Spring2000/PointInPolygon.html">Point in polygon algorithm</a>
     */
    int intersectRay(double x, double y) {
        // Check if an edge intersect with a ray from specified coordinate.
        double x0 = ax[ax.length - 1];
        double y0 = ay[ay.length - 1];
        double x1, y1;
        int intersectCount = 0;
        for (int i = 0; i < ax.length; ++i, x0 = x1, y0 = y1) {
            x1 = ax[i];
            y1 = ay[i];
            if ((y0 < y && y1 < y) || (y0 >= y && y1 >= y) || (x0 < x && x1 < x)) {
                continue;
            }
            
            double a = y1 - y0;
            double b = x0 - x1;
            double c = x0 * y1 - x1 * y0;
            assert (a != 0 || b != 0);

            // calculate intersection x
            double ix = (c - b * y) / a;
            // check if the intersection is on the ray
            if (ix >= x && (x0 < x1 ? (ix >= x0 && ix <= x1) : (ix <= x0 && ix >= x1))) {
                intersectCount++;
            }
        }

        return intersectCount;
    }
    
    /**
     * Compute clockwise of vertices of this ring. <br/>
     * @return {@link #CLOCKWISE}, {@link #COUNTER_CLOCKWISE} or {@link #COLINEAR}.
     */
    public int clockwise() {
        double sum = 0;
        double lastX = ax[ax.length - 1];
        double lastY = ay[ay.length - 1];
        
        for (int i = 0; i < ax.length; ++i) {
            sum += lastX * ay[i] - ax[i] * lastY;
            lastX = ax[i];
            lastY = ay[i];
        }
        
        if (sum > 0) {
            return COUNTER_CLOCKWISE;
        } else if (sum < 0) {
            return CLOCKWISE;
        } else {
            return COLINEAR;
        }
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(ax) + Arrays.hashCode(ay);
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
        LinearRing other = (LinearRing) obj;
        return Arrays.equals(ax, other.ax) && Arrays.equals(ay, other.ay);
    }

    /**
     * Returns number of sides, as well as number of vertices.
     */
    public int numberOfSides() {
        return ax.length;
    }
    
    /**
     * Check if this linear ring overlaps specified bounding box.
     * @param other the bounding box to be checked.
     * @return true if this linear ring overlaps the bounding box.
     */
    public boolean overlaps(BoundingBox other) {
        if (other.contains(this.bb)) {
            return true;
        } else if (!other.overlaps(this.bb)) {
            return false;
        }
        
        // check if any edge of this ring intersects the bounding box
        if (intersects(other.minX(), other.minY(), other.maxX(), other.maxY())) {
            return true;
        }
        
        // check if the bounding box is inside of this ring.
        if (intersectRay(other.maxX(), other.maxY()) % 2 != 0) {
            return true;
        }
        return false;
    }
    
    /**
     * Check if this linear ring contains specified bounding box.
     * @param other the bounding box to be checked.
     * @return true if this ring contains the bounding box.
     */
    public boolean contains(BoundingBox other) {
        // check if the bounding box is inside of this ring
        if (intersectRay(other.maxX(), other.maxY()) % 2 == 0) {
            return false;
        }

        // check if any edge of this ring intersects the bounding box
        if (intersects(other.minX(), other.minY(), other.maxX(), other.maxY())) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Check if any vertex intersects the specified bounding box.
     */
    private boolean intersects(double minX, double minY, double maxX, double maxY) {
        double x0 = ax[ax.length - 1];
        double y0 = ay[ay.length - 1];
        double x1, y1;
        for (int i = 0; i < ax.length; i++, x0 = x1, y0 = y1) {
            x1 = ax[i];
            y1 = ay[i];

            if ((x0 < minX && x1 < minX) || (x0 > maxX && x1 > maxX) || (y0 < minY && y1 < minY)
                || (y0 > maxY && y1 > maxY)) {
                continue;
            }

            double a = y1 - y0;
            double b = x0 - x1;
            double c = x0 * y1 - x1 * y0;
            assert (a != 0 || b != 0);

            if (a != 0) {
                // calculate intersection x
                double ix = (c - b * minY) / a;
                if (ix >= minX && ix <= maxX && (x0 < x1 ? (ix >= x0 && ix <= x1) : (ix <= x0 && ix >= x1))) {
                    return true;
                }
                ix = (c - b * maxY) / a;
                if (ix >= minX && ix <= maxX && (x0 < x1 ? (ix >= x0 && ix <= x1) : (ix <= x0 && ix >= x1))) {
                    return true;
                }
            }

            if (b != 0) {
                // calculate intersection y
                double iy = (c - a * minX) / b;
                if (iy >= minY && iy <= maxY && (y0 < y1 ? (iy >= y0 && iy <= y1) : (iy <= y0 && iy >= y1))) {
                    return true;
                }
                iy = (c - a * maxX) / b;
                if (iy >= minY && iy <= maxY && (y0 < y1 ? (iy >= y0 && iy <= y1) : (iy <= y0 && iy >= y1))) {
                    return true;
                }
            }
        }
        return false;
    }
}
