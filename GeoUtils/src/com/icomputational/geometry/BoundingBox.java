package com.icomputational.geometry;

/**
 * @author icomputational
 * A {@link BoundingBox} represents an immutable bounding box for shapes.
 */
public class BoundingBox {
	private final double minX;
	private final double minY;
	private final double maxX;
	private final double maxY;
	private final Point centre;
	
    /**
     * Construct a bounding box.
     * @param minX the minimum value in x coordinate.
     * @param minY the minimum value in y coordinate
     * @param maxX the maximum value in x coordinate
     * @param maxY the maximum value in y coordinate
     */
    public BoundingBox(double minX, double minY, double maxX, double maxY) {
        if (!(minX < maxX)) {
            throw new IllegalArgumentException("min X " + minX + " should be less than max X " + maxX);
        }
        if (!(minY < maxY)) {
            throw new IllegalArgumentException("min Y " + minY + " should be less than max Y " + maxY);
        }
        if (Double.isInfinite(minX) || Double.isInfinite(maxX) || Double.isInfinite(minY) || Double.isInfinite(maxY)) {
            throw new IllegalArgumentException("infinite value is not supported");
        }

        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;

        centre = new Point((minX + maxX) / 2, (minY + maxY) / 2);
    }
    
    /**
     * Returns the minimum X.
     */
    public double minX() {
        return minX;
    }

    /**
     * Returns the minimum Y.
     */
    public double minY() {
        return minY;
    }

    /**
     * Returns the maximum X.
     */
    public double maxX() {
        return maxX;
    }

    /**
     * Returns the maximum Y.
     */
    public double maxY() {
        return maxY;
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
     * Check if this bounding box contains specified point.
     */
    public boolean contains(Point p) {
        return contains(p.x(), p.y());
    }

    /**
     * Check if this bounding box contains specified coordinate.
     */
    public boolean contains(double x, double y) {
        return minX <= x && maxX >= x && minY <= y && maxY >= y;
    }
    
    /**
     * Returns true if the specified bounding box is contained by this bounding box.
     */
    public boolean contains(BoundingBox bb) {
        return minX <= bb.minX && maxX >= bb.maxX && minY <= bb.minY && maxY >= bb.maxY;
    }
    
    /**
     * Get area of this bounding box.
     * @return the area of this bounding box.
     */
    public double area() {
        return (maxX - minX) * (maxY - minY);
    }

    /**
     * The margin is defined as sum of the lengths of all edges of bounding box.
     */
    public double margin() {
        return ((maxX - minX) + (maxY - minY)) * 2;
    }

    /**
     * Returns the central point of this bounding box.
     */
    public Point centre() {
        return centre;
    }
    
    /**
     * Join two bounding boxes into a large bounding box covers all area of two bounding box.
     * @param bb another bounding box.
     * @return the new large bounding box.
     */
    public BoundingBox join(BoundingBox bb) {
        return new BoundingBox(Math.min(this.minX, bb.minX), Math.min(this.minY, bb.minY),
            Math.max(this.maxX, bb.maxX), Math.max(this.maxY, bb.maxY));
    }

    /**
     * Check if this bounding box overlap with another bounding box.
     * @param bb a bounding box.
     * @return true if two bounding boxs overlap.
     */
    public boolean overlaps(BoundingBox bb) {
        return this.minX < bb.maxX && this.maxX > bb.minX && this.minY < bb.maxY && this.maxY > bb.minY;
    }
    
    /**
     * Get overlap area with specified bounding box.
     * @param bb another bounding box.
     * @return the overlap area if this bounding box overlaps with specified bounding box,
     *         0 if there is no overlap.
     */
    public double getOverlap(BoundingBox bb) {
        double minx = Math.max(this.minX, bb.minX);
        double miny = Math.max(this.minY, bb.minY);
        double maxy = Math.min(this.maxY, bb.maxY);
        double maxx = Math.min(this.maxX, bb.maxX);
        if (minx < maxx && miny < maxy) {
            return (maxx - minx) * (maxy - miny);
        } else {
            return 0;
        }
    }
    
    /**
     * Check if this bounding box intersects with specified line segment.
     * @param p1 an end of line segment.
     * @param p2 another end of line segment.
     * @return true if this bounding box intersect with specified line segment.
     */
    public boolean intersects(Point p1, Point p2) {
        double minPx;
        double maxPx;
        if (p1.x() <= p2.x()) {
            minPx = p1.x();
            maxPx = p2.x();
        } else {
            minPx = p2.x();
            maxPx = p1.x();
        }
        
        double minPy;
        double maxPy;
        if (p1.y() < p2.y()) {
            minPy = p1.y();
            maxPy = p2.y();
        } else {
            minPy = p2.y();
            maxPy = p1.y();
        }
        
        Line segment = new Line(p1, p2);
        if (this.minX >= minPx && this.minX <= maxPx) {
            double y1 = segment.getY(minX);
            if (y1 >= minY && y1 <= maxY) {
                return true;
            }
        }
        if (this.maxX >= minPx && this.maxX <= maxPx) {
            double y2 = segment.getY(maxX);
            if (y2 >= minY && y2 <= maxY) {
                return true;
            }
        }

        if (this.minY >= minPy && this.minY <= maxPy) {
            double x1 = segment.getX(minY);
            if (x1 >= minX && x1 <= maxX) {
                return true;
            }
        }
        if (this.maxY >= minPy && this.maxY <= maxPy) {
            double x2 = segment.getX(maxY);
            if (x2 >= minX && x2 <= maxX) {
                return true;
            }
        }

        return false;
        
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(maxX);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(maxY);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(minX);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(minY);
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

        BoundingBox other = (BoundingBox) obj;
        return Double.doubleToLongBits(this.minX) == Double.doubleToLongBits(other.minX)
            && Double.doubleToLongBits(this.maxX) == Double.doubleToLongBits(other.maxX)
            && Double.doubleToLongBits(this.minY) == Double.doubleToLongBits(other.minY)
            && Double.doubleToLongBits(this.maxY) == Double.doubleToLongBits(other.maxY);
    }

    @Override
    public String toString() {
        return "BoundingBox [minX=" + minX + ", minY=" + minY + ", maxX=" + maxX + ", maxY=" + maxY + "]";
    }
    
}
