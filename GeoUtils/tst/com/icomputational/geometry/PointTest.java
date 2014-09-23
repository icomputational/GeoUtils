package com.icomputational.geometry;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PointTest {

    @Test
    public void testPoint() throws Exception {
        double x = -118.356668;
        double y = 33.841352;

        Point c = new Point(x, y);
        assertTrue(x == c.x());
        assertTrue(y == c.y());

        Point c1 = new Point(x, y);
        assertTrue(c.isIdentical(c1));
    }

    @Test
    public void testInterpolate() {
        Point c1 = new Point(-118.356668, 33.841352);
        Point c2 = new Point(-117.356668, 34.841352);
        Point c3 = new Point((c1.x() + c2.x()) / 2, (c1.y() + c2.y()) / 2);
        assertTrue(c1.isIdentical(c1.interpolate(c1, 0.3541)));
        assertTrue(c1.isIdentical(c1.interpolate(c2, 0)));
        assertTrue(c2.isIdentical(c1.interpolate(c2, 1)));
        assertTrue(c3.isIdentical(c1.interpolate(c2, 0.5)));
    }

    @Test
    public void testDistance() {
        Point c1 = new Point(-1, -1);
        Point c2 = new Point(2, 3);

        assertTrue(c1.distance(c1) == 0D);
        assertTrue(Math.abs(c1.distance(c2) - 5) < 0.1E-10D);
    }
}