package com.icomputational.geoelements;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SphereTest {

    @Test
    public void testGetDistanceMethods() {
        assertEquals(15.725337, Sphere.EARTH.getDistance(new Coordinate(-179.9999, 45), new Coordinate(179.9999,
            45)), 1);
        assertEquals(0, Sphere.EARTH.getDistance(new Coordinate(-180.0D, 45.0D), new Coordinate(180.0D, 45.0D)), 1);
    }

    @Test
    public void testPerpendicularShift() {
        Coordinate s = new Coordinate(118.351243, 34.007375);
        Coordinate t = new Coordinate(118.353593, 34.007136);

        Coordinate x = Sphere.EARTH.perpendicularShift(s, t, 10);
        assertEquals(118.35122979050223, x.longitude(), 1E-6);
        assertEquals(34.007285736983675, x.latitude(), 1E-6);

        Coordinate y = Sphere.EARTH.perpendicularShift(s, t, -10);
        assertEquals(118.35125620952554, y.longitude(), 1E-6);
        assertEquals(34.00746426301493, y.latitude(), 1E-6);
    }

    @Test
    public void testInterpolation() {
        List<Coordinate> coordinates = new ArrayList<Coordinate>();

        coordinates.add(new Coordinate(-89.541239, 43.373936));
        coordinates.add(new Coordinate(-89.544337, 43.374143));
        coordinates.add(new Coordinate(-89.545416, 43.37422));
        coordinates.add(new Coordinate(-89.54611, 43.374262));

        Coordinate expectedCoordinate = new Coordinate(-89.541239, 43.373936);
        Coordinate realCoordinate = Sphere.EARTH.interpolate(coordinates, 0.0);
        assertEquals(expectedCoordinate.longitude(), realCoordinate.longitude(), 1E-6);
        assertEquals(expectedCoordinate.latitude(), realCoordinate.latitude(), 1E-6);

        expectedCoordinate = new Coordinate(-89.54611, 43.374262);
        realCoordinate = Sphere.EARTH.interpolate(coordinates, 1.0);
        assertEquals(expectedCoordinate.longitude(), realCoordinate.longitude(), 1E-6);
        assertEquals(expectedCoordinate.latitude(), realCoordinate.latitude(), 1E-6);

        expectedCoordinate = new Coordinate(-89.544161, 43.374131);
        realCoordinate = Sphere.EARTH.interpolate(coordinates, 0.6);
        assertEquals(expectedCoordinate.longitude(), realCoordinate.longitude(), 1E-6);
        assertEquals(expectedCoordinate.latitude(), realCoordinate.latitude(), 1E-6);

        expectedCoordinate = new Coordinate(-89.546089, 43.374441);
        realCoordinate = Sphere.EARTH.interpolate(coordinates, 1.0, 20);
        assertEquals(expectedCoordinate.longitude(), realCoordinate.longitude(), 1E-6);
        assertEquals(expectedCoordinate.latitude(), realCoordinate.latitude(), 1E-6);
    }
}
