package com.icomputational.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class BoundingBoxTest {
    @Test
    public void testSimple() {
        BoundingBox bb = new BoundingBox(0, 0, 1, 1);
        assertTrue(Geometry.almostEquals(1, bb.width()));
        assertTrue(Geometry.almostEquals(1, bb.height()));
        assertTrue(bb.contains(new Point(0.5, 0.5)));

        assertTrue(new BoundingBox(0, 0, 2, 2).contains(bb));
        assertTrue(new BoundingBox(0.5, 0.5, 1.5, 1.5).overlaps(bb));

        assertEquals(bb, new BoundingBox(0, 0, 1, 1));
        assertEquals(bb.hashCode(), new BoundingBox(0, 0, 1, 1).hashCode());

        assertNotNull(bb.toString());
    }
    
    @Test
    public void testInvalid() {
        try {
            new BoundingBox(100, 100, 0, 0);
            fail("no exception occurs for invalid input");
        } catch (IllegalArgumentException e) {
            // pass
        }

        try {
            new BoundingBox(0, 100, 10, 0);
            fail("no exception occurs for invalid input");
        } catch (IllegalArgumentException e) {
            // pass
        }

        try {
            new BoundingBox(0, 0, Double.POSITIVE_INFINITY, 10);
            fail("no exception occurs for invalid input");
        } catch (IllegalArgumentException e) {
            // pass
        }

        try {
            new BoundingBox(Double.NEGATIVE_INFINITY, 0, 0, 10);
            fail("no exception occurs for invalid input");
        } catch (IllegalArgumentException e) {
            // pass
        }

        try {
            new BoundingBox(0, 0, 0, Double.POSITIVE_INFINITY);
            fail("no exception occurs for invalid input");
        } catch (IllegalArgumentException e) {
            // pass
        }

        try {
            new BoundingBox(0, Double.NEGATIVE_INFINITY, 0, 10);
            fail("no exception occurs for invalid input");
        } catch (IllegalArgumentException e) {
            // pass
        }
    }

    @Test
    public void testIntersect() {
        BoundingBox bb = new BoundingBox(0, 0, 1, 1);
        Point p1 = new Point(0, 0);
        Point p2 = new Point(1, 1);
        assertTrue(bb.intersects(p1, p2));

        p2 = new Point(0.1, 2);
        assertTrue(bb.intersects(p1, p2));

        p2 = new Point(10, 10);
        assertTrue(bb.intersects(p1, p2));

        p2 = new Point(1, 0);
        assertTrue(bb.intersects(p1, p2));

        p1 = new Point(-1E-16, 0);
        p2 = new Point(-1, 0);
        assertFalse(bb.intersects(p1, p2));

        p1 = new Point(-0.5, 1);
        p2 = new Point(0.5, 0.5);
        assertTrue(bb.intersects(p1, p2));

        p1 = new Point(0.5, 0.8);
        p2 = new Point(1.5, 0.5);
        assertTrue(bb.intersects(p1, p2));

        p1 = new Point(0.5, 0.1);
        p2 = new Point(0.8, 1.2);
        assertTrue(bb.intersects(p1, p2));

        p1 = new Point(0.3, -0.5);
        p2 = new Point(0.8, 0.3);
        assertTrue(bb.intersects(p1, p2));

        p1 = new Point(-1, 0);
        p2 = new Point(1, 2);
        assertTrue(bb.intersects(p1, p2));

        p1 = new Point(-1, 0.1);
        p2 = new Point(1, 2);
        assertFalse(bb.intersects(p1, p2));

        p1 = new Point(0, 2);
        p2 = new Point(2, 0);
        assertTrue(bb.intersects(p1, p2));

        p1 = new Point(0, 2.1);
        p2 = new Point(2, 0);
        assertFalse(bb.intersects(p1, p2));

        p1 = new Point(-1, 1);
        p2 = new Point(1, -1);
        assertTrue(bb.intersects(p1, p2));

        p1 = new Point(-1.1, 1);
        p2 = new Point(1, -1);
        assertFalse(bb.intersects(p1, p2));

        p1 = new Point(0, -1);
        p2 = new Point(2, 1);
        assertTrue(bb.intersects(p1, p2));

        p1 = new Point(0, -1.1);
        p2 = new Point(2, 1);
        assertFalse(bb.intersects(p1, p2));
    }
    
}
