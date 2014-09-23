package com.icomputational.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class LinearRingTest {
    @Test
    public void testSimple() {
        List<Point> vertices = new ArrayList<Point>();
        vertices.add(new Point(1, 1));
        vertices.add(new Point(1, -1));
        vertices.add(new Point(-1, -1));
        vertices.add(new Point(0, 0));
        vertices.add(new Point(-1, 1));

        LinearRing ring1 = new LinearRing(vertices);
        assertNotNull(ring1.boundingBox());
        LinearRing ring2 = new LinearRing(vertices);
        assertEquals(ring1, ring1);
        assertEquals(ring1, ring2);
        assertFalse(ring1.equals(null));
        assertEquals(ring1.hashCode(), ring2.hashCode());
        assertEquals(5, ring1.numberOfSides());
    }

    @Test
    public void testClockwise() {
        List<Point> vertices = new ArrayList<Point>();
        vertices.add(new Point(1, 1));
        vertices.add(new Point(1, -1));
        vertices.add(new Point(-1, -1));
        vertices.add(new Point(0, 0));
        vertices.add(new Point(-1, 1));

        LinearRing polygon = new LinearRing(vertices);
        assertEquals(LinearRing.CLOCKWISE, polygon.clockwise());
    }

    @Test
    public void testCounterClockwise() {
        List<Point> vertices = new ArrayList<Point>();
        vertices.add(new Point(1, 1));
        vertices.add(new Point(1, -1));
        vertices.add(new Point(-1, -1));
        vertices.add(new Point(0, 0));
        vertices.add(new Point(-1, 1));
        Collections.reverse(vertices);

        LinearRing polygon = new LinearRing(vertices);
        assertEquals(LinearRing.COUNTER_CLOCKWISE, polygon.clockwise());
    }

    @Test
    public void testColinearPoints() {
        List<Point> vertices = new ArrayList<Point>();
        vertices.add(new Point(1, 1));
        vertices.add(new Point(2, 2));
        vertices.add(new Point(-1, -1));
        vertices.add(new Point(0, 0));
        vertices.add(new Point(4, 4));

        LinearRing polygon = new LinearRing(vertices);
        assertEquals(LinearRing.COLINEAR, polygon.clockwise());
    }

    @Test
    public void testNegative() {
        try {
            new LinearRing(Arrays.asList(new Point(0, 0)));
            fail("no exception occurs for invalid data");
        } catch (IllegalArgumentException e) {
            // pass
        }
    }

    @Test
    public void testOverlaps() {
        List<Point> vertices = Arrays.asList(new Point(1, 1), new Point(1, -1), new Point(-1, -1), new Point(0, 0),
            new Point(-1, 1));

        LinearRing ring = new LinearRing(vertices);
        assertTrue(ring.overlaps(new BoundingBox(-1.5, 0, -0.5, 1)));
        assertTrue(ring.overlaps(new BoundingBox(-2, -2, 2, 2)));
        assertTrue(ring.overlaps(new BoundingBox(0.1, -0.9, 0.9, -0.1)));

        assertFalse(ring.overlaps(new BoundingBox(2, 2, 3, 3)));
    }

    @Test
    public void testContains() {
        List<Point> vertices = Arrays.asList(new Point(1, 1), new Point(1, -1), new Point(-1, -1), new Point(0, 0),
            new Point(-1, 1));

        LinearRing ring = new LinearRing(vertices);
        assertTrue(ring.contains(new BoundingBox(0.1, -0.9, 0.9, -0.1)));
        assertFalse(ring.contains(new BoundingBox(-1.5, 0, -0.5, 1)));
        assertFalse(ring.contains(new BoundingBox(-2, -2, 2, 2)));
        assertFalse(ring.contains(new BoundingBox(2, 2, 3, 3)));
    }
}