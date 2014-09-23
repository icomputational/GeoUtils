package com.icomputational.geometry;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class PolygonTest {

    @Test
    public void testSimple() {
        List<Point> vertices = Arrays.asList(new Point(1, 1), new Point(1, -1), new Point(-1, -1), new Point(0, 0),
            new Point(-1, 1));

        LinearRing ring = new LinearRing(vertices);

        Polygon polygon = new Polygon(ring);
        assertTrue(polygon.isValid());

        assertTrue(polygon.contains(new Point(0.5, -0.5)));
        assertFalse(polygon.contains(new Point(-0.5, 0)));

    }

    @Test
    public void testNegative() {
        try {
            new Polygon(null);
            fail("no exception occurs for negative data");
        } catch (IllegalArgumentException e) {
            // pass
        }

        List<Point> vertices = Arrays.asList(new Point(1, 1), new Point(1, -1), new Point(-1, -1), new Point(0, 0),
            new Point(-1, 1));

        Polygon polygon = new Polygon(new LinearRing(vertices));
        try {
            polygon.addInnerRing(null);
            fail("no exception occurs for negative data");
        } catch (IllegalArgumentException e) {
            // pass
        }

    }

    @Test
    public void testPolygonWithHole() {
        List<Point> vertices = Arrays.asList(new Point(1, 1), new Point(1, -1), new Point(-1, -1), new Point(0, 0),
            new Point(-1, 1));

        Polygon polygon = new Polygon(new LinearRing(vertices));
        // 0.1, -0.9, 0.9, -0.1
        vertices = Arrays
            .asList(new Point(0.1, -0.9), new Point(0.9, -0.9), new Point(0.9, -0.1), new Point(0.1, -0.1));
        polygon.addInnerRing(new LinearRing(vertices));
        assertTrue(polygon.isValid());
        assertTrue(polygon.contains(new Point(0, -0.5)));
        assertFalse(polygon.contains(new Point(0.5, -0.5)));

        assertTrue(polygon.overlaps(new BoundingBox(-1.5, 0, -0.5, 1)));
        assertTrue(polygon.overlaps(new BoundingBox(-2, -2, 2, 2)));
        assertTrue(polygon.overlaps(new BoundingBox(0.09, -0.9, 0.9, -0.1)));
        assertFalse(polygon.overlaps(new BoundingBox(0.11, -0.89, 0.89, -0.11)));

        assertFalse(polygon.overlaps(new BoundingBox(2, 2, 3, 3)));
    }
}
