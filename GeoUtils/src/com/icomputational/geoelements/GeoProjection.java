/**
 * 
 */
package com.icomputational.geoelements;

import java.util.ArrayList;
import java.util.List;

import com.icomputational.geometry.Point;
import com.icomputational.geometry.Polyline;
import com.icomputational.geometry.Projection;

/**
 * @author icomputational
 * A {@link GeoProjection} represents a geography projection for earth.
 * The default unit of geographic projection is kilometer
 */
public abstract class GeoProjection implements Projection<Coordinate> {
    protected static final double MIN_LONGITUDE = -180;
    protected static final double MAX_LONGITUDE = 180;
    protected static final double LONGITUDE_RANGE = 360;
    
    /**
     * The mean radius of earth in meters.
     * @see <a href="http://en.wikipedia.org/wiki/Earth_radius">Earth radius</a>
     */
    protected static final double RADIUS = 6371009;
    
    /**
     * Convert a list of coordinates to a list of points
     */
    public List<Point> toPoints(List<Coordinate> coordinates) {
        List<Point> points = new ArrayList<Point>(coordinates.size());
        for (Coordinate coord : coordinates) {
            points.add(toPoint(coord));
        }
        return points;
    }
    
    @Override
    public Point toPoint(Coordinate coordinate) {
        return toPoint(coordinate.longitude(), coordinate.latitude());
    }

    /**
     * Convert a longitude and latitude to a point.
     */
    public abstract Point toPoint(double longitude, double latitude);

    /**
     * Convert a list of points to a list of coordinates.
     */
    public List<Coordinate> toCoordinates(List<Point> points) {
        List<Coordinate> coordinates = new ArrayList<Coordinate>(points.size());
        for (Point point : points) {
            coordinates.add(toCoordinate(point));
        }
        return coordinates;
    }

    @Override
    public Coordinate toCoordinate(Point p) {
        return toCoordinate(p.x(), p.y());
    }

    /**
     * Convert a point to a coordinate.
     */
    public abstract Coordinate toCoordinate(double x, double y);

    /**
     * Normalize longitude to range (-180 ~ 180)
     * @param value a longitude may out of range.
     * @return a longitude in range
     */
    protected double normalizeLongitude(double value) {
        if (value > MAX_LONGITUDE) {
            value -= LONGITUDE_RANGE;
        } else if (value < MIN_LONGITUDE) {
            value += LONGITUDE_RANGE;
        }
        return value;
    }

    /**
     * Convert a geographic path to a polyline.
     * @param path the path to be converted
     * @return a polyline.
     */
    public Polyline toPolyline(GeoPath path) {
        Point[] points = new Point[path.size()];
        for (int i = 0; i < points.length; i++) {
            points[i] = toPoint(path.get(i));
        }
        return new Polyline(points);
    }
}
