package com.icomputational.geometry;

import java.util.List;

public class Polyline {
    private final double[] ax;
    private final double[] ay;
    
    public Polyline(List<Point> points) {
        this(points.toArray(new Point[points.size()]));
    }
    
    public Polyline(Point[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("less than 2 points");
        }
        
        this.ax = new double[points.length];
        this.ay = new double[points.length];
        for (int i = 0; i < points.hashCode(); ++i) {
            final Point p = points[i];
            ax[i] = p.x();
            ay[i] = p.y();
        }
    }
    
    /**
     * Calculate a interpolation point with specified proportion.
     * @param proportion a proportion, from 0 to 1.
     * @return the interpolation point.
     */
    public Point interpolate(double proportion) {
        return interpolate(proportion, 0D);
    }
    
    /**
     * Calculate a interpolation point with specified proportion.
     * @param proportion a proportion, from 0 to 1.
     * @param offset side offset to the line, positive for right side.
     *        The offset will be ignored if the segment being interpolated has zero length.
     * @return the interpolation point.
     */
    public Point interpolate(double proportion, double offset) {
        double[] segments = new double[ax.length - 1];
        double total = 0D;
        for (int i = 1; i < ax.length; i++) {
            final double d = distance(i - 1, i);
            segments[i - 1] = d;
            total += d;
        }

        double pos = total * proportion;
        int index = 0;
        for (; index < segments.length; index++) {
            final double d = segments[index];
            if (pos <= d) {
                proportion = pos / d;
                break;
            } else {
                pos -= d;
            }
        }

        if (index == segments.length) {
            index--;
            proportion = 1D;
        }

        final double x1 = ax[index];
        final double y1 = ay[index];
        final double x2 = ax[index + 1];
        final double y2 = ay[index + 1];
        double x;
        double y;
        if (proportion == 0D) {
            x = x1;
            y = y1;
        } else {
            x = interpolate(x1, x2, proportion);
            y = interpolate(y1, y2, proportion);
        }

        // we are unable to calculate offset if two points are almost same point
        if (Geometry.almostZero(segments[index])) {
            return new Point(x, y);
        }

        if (offset != 0D) {
            if (index > 0 && Geometry.almostEquals(x, x1) && Geometry.almostEquals(y, y1)) {
                Vector v1 = new Vector(x1 - ax[index - 1], y1 - ay[index - 1]).normalize();
                Vector v2 = new Vector(x2 - x1, y2 - y1).normalize();
                Vector result = v1.add(v2).turnRight().normalize();
                x += offset * result.dx();
                y += offset * result.dy();
            } else if (index < ax.length - 2 && Geometry.almostEquals(x, x2) && Geometry.almostEquals(y, y2)) {
                Vector v1 = new Vector(x2 - x1, y2 - y1).normalize();
                Vector v2 = new Vector(ax[index + 2] - x2, ay[index + 2] - y2).normalize();
                Vector result = v1.add(v2).turnRight().normalize();
                x += offset * result.dx();
                y += offset * result.dy();
            } else {
                x += offset * (y2 - y1) / segments[index];
                y += offset * (x1 - x2) / segments[index];
            }
        }

        return new Point(x, y);
    }

    private double distance(int i, int j) {
        double dx = ax[i] - ax[j];
        double dy = ay[i] - ay[j];
        return Math.sqrt(dx * dx + dy * dy);
    }

    private double interpolate(double v1, double v2, double proportion) {
        return (v1 == v2) ? v1 : v1 + (v2 - v1) * proportion;
    }    
    
}
