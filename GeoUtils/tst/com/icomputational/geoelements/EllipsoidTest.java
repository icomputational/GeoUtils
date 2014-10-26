package com.icomputational.geoelements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EllipsoidTest {

    @Test
    public void testToCartesian() {
        double latitude = 52 + 39D / 60D + 27.2531 / 3600D;
        double longitude = 1 + 43D / 60D + 4.5177 / 3600D;
        double height = 24.700;

        Coord3D coord = Ellipsoid.AIRY1830.toCartesian(new Coordinate(longitude, latitude), height);

        assertTrue(Math.abs(coord.x() - 3874938.850) < 0.1);
        assertTrue(Math.abs(coord.y() - 116218.624) < 0.1);
        assertTrue(Math.abs(coord.z() - 5047168.207) < 0.1);
    }

    @Test
    public void testToEllipsoidal() {
        Coordinate coord = Ellipsoid.AIRY1830.toEllipsoidal(new Coord3D(3874938.8496876387, 116218.62377004114,
            5047168.207309848), 1E-10);

        assertTrue(Math.abs(coord.longitude() - 1.71792158333) < 1E-7);
        assertTrue(Math.abs(coord.latitude() - 52.65757030556) < 1E-7);
    }

    @Test
    public void testDistance() {
        Ellipsoid e = Ellipsoid.GRS80;
        assertEquals(0, e.getDistance(new Coordinate(0, 0), new Coordinate(0, 0)), 1.0);
        assertEquals(0, e.getDistance(new Coordinate(0, 90), new Coordinate(180, 90)), 1.0);
        assertEquals(0, e.getDistance(new Coordinate(0, -90), new Coordinate(180, -90)), 1.0);

        Coordinate coord1 = new Coordinate(-5.71475, 50.0663222);
        Coordinate coord2 = new Coordinate(-3.070094, 58.6440222);
        assertEquals(969954.114, e.getDistance(coord1, coord2), 1E-2);
    }
}
