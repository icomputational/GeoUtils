package com.icomputational.geoelements;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.icomputational.geometry.Point;

public class UKTest {

    @Test
    public void testSimple() {
        double longitude = 1 + 43 / 60.0 + 4.5177 / 3600.0; // 1 43' 4.5177" E
        double latitude = 52 + 39 / 60.0 + 27.2531 / 3600.0; // 52 39' 27.2531" N
        Coordinate coord = new Coordinate(longitude, latitude);
        Point p = UK.UK_NATIONAL_GRID.toPoint(coord);
        assertNotNull(p);

        assertTrue(Math.abs(p.x() - 651409.903) < 0.01);
        assertTrue(Math.abs(p.y() - 313177.270) < 0.01);

        Coordinate result = UK.UK_NATIONAL_GRID.toCoordinate(p);
        assertTrue(Math.abs(coord.longitude() - result.longitude()) < 1E-7);
        assertTrue(Math.abs(coord.latitude() - result.latitude()) < 1E-7);
    }

    @Test
    public void testHelmertTrans() {
        Coordinate coord = new Coordinate(0.5, 51.5);
        Coordinate result1 = UK.toGRS80(coord);
        assertTrue(Math.abs(result1.longitude() - 0.49832264) < 1E-6);
        assertTrue(Math.abs(result1.latitude() - 51.500521395) < 1E-6);
        Coordinate result2 = UK.toAiry(result1);

        // System.out.println("Origin: " + coord + " GRS80: " + result1 + " Airy: " + result2);
        assertTrue(Math.abs(result2.longitude() - coord.longitude()) < 1E-6);
        assertTrue(Math.abs(result2.latitude() - coord.latitude()) < 1E-6);
    }

    @Test
    public void testToOSGB36() {
        // Waterloo Station 
        Coordinate coord = new Coordinate(-0.111178, 51.503076);
        coord = UK.toAiry(coord);
        Point point = UK.UK_NATIONAL_GRID.toPoint(coord);
        // Results from Google: 531194.6, 179928.9
        assertTrue(Math.abs(point.x() - 531194.6) < 0.1);
        assertTrue(Math.abs(point.y() - 179928.9) < 0.1);
    }
}
