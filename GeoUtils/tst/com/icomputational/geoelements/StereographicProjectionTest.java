package com.icomputational.geoelements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.icomputational.geometry.Point;
import com.icomputational.geometry.Polyline;

public class StereographicProjectionTest {
    @Test
    public void testNormalizeLongitude() {
        Coordinate centre = new Coordinate(0.0D, 90.0D);
        StereographicProjection projection = new StereographicProjection(centre);
        double latitude = projection.normalizeLongitude(190);
        assertTrue(latitude + "<>" + 10, Math.abs(latitude + 170) < 1E-6);
        latitude = projection.normalizeLongitude(-190);
        assertTrue(latitude + "<>" + -10, Math.abs(latitude - 170) < 1E-6);
    }

    @Test
    public void testBatch() {
        Coordinate centre = new Coordinate(0.0D, 90.0D);
        StereographicProjection projection = new StereographicProjection(centre);

        List<Coordinate> coordinates = new ArrayList<Coordinate>();
        for (int i = -180; i <= 180; i += 60) {
            coordinates.add(new Coordinate(i, 80.0));
        }

        List<Point> points = projection.toPoints(coordinates);
        assertEquals(coordinates.size(), points.size());

        coordinates = projection.toCoordinates(points);
        assertEquals(coordinates.size(), points.size());
    }

    @Test
    public void testNorthPole() {
        Coordinate centre = new Coordinate(0.0D, 90.0D);
        StereographicProjection projection = new StereographicProjection(centre);

        Coordinate base = new Coordinate(0, 80.0);
        Point basePoint = projection.toPoint(base);
        double baseRadius = basePoint.distance(0, 0);

        for (int i = -180; i <= 180; i += 60) {
            Coordinate loc = new Coordinate(i, 80.0);
            Point p = projection.toPoint(loc);
            double r = p.distance(0, 0);
            // System.out.println(loc + " => " + p + " radius " + r);
            assertTrue(baseRadius + " <> " + r, Math.abs(baseRadius - r) < 1E-5);
        }
    }

    @Test
    public void testEquator() {
        Coordinate centre = new Coordinate(0.0, 0.0);
        StereographicProjection projection = new StereographicProjection(centre);

        for (int i = 0; i <= 180; i += 60) {
            Coordinate loc1 = new Coordinate(i, 10.0);
            Coordinate loc2 = new Coordinate(-i, 10.0);
            Point p1 = projection.toPoint(loc1);
            Point p2 = projection.toPoint(loc2);
            double r1 = p1.distance(0, 0);
            double r2 = p2.distance(0, 0);
            //System.out.println(loc1 + " => " + p1 + " radius " + r1);
            //System.out.println(loc2 + " => " + p2 + " radius " + r2);
            assertTrue(r1 + " <> " + r2, Math.abs(r1 - r2) < 1E-5);
            assertTrue(p1.x() + " <> -" + p2.x(), Math.abs(p1.x() + p2.x()) < 1E-5);
            assertTrue(p1.y() + " <> " + p2.y(), Math.abs(p1.y() - p2.y()) < 1E-5);
        }
    }

    @Test
    public void testZero() {
        Coordinate coord = new Coordinate(-122.415521889925, 37.63892613351345);
        GeoPath path = new GeoPath(coord, coord);
        GeoProjection projection = new StereographicProjection(path.middle());
        Polyline polyline = projection.toPolyline(path);

        Point point = polyline.interpolate(0, 0.02);
        assertTrue(point.almostZero());
        assertTrue(projection.toCoordinate(point).decimallyEquals(coord));
    }
}
