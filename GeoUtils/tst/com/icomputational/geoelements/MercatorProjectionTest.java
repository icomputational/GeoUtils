package com.icomputational.geoelements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.icomputational.geometry.Point;
import com.icomputational.geometry.Polyline;

public class MercatorProjectionTest {

    @Test
    public void testSimple() {
        MercatorProjection projection = new MercatorProjection(170);
        Coordinate coordinate = projection.toCoordinate(0, 100);
        assertEquals(170, coordinate.longitude(), 1E-10);
        assertEquals(70.19337703736588, coordinate.latitude(), 1E-10);

        Point p = projection.toPoint(new Coordinate(-179, 30));
        assertEquals(11.0, p.x(), 1E-10);
        assertEquals(31.47292373094538, p.y(), 1E-10);
    }

    @Test
    public void testToPolyline() {
        MercatorProjection projection = new MercatorProjection(170);
        GeoPath path = new GeoPath(new Coordinate(0, 0), new Coordinate(1D, 2D));
        Polyline line = projection.toPolyline(path);
        assertNotNull(line);
    }
}
