package com.icomputational.geometry;

import static org.junit.Assert.*;

import org.junit.Test;

import com.icomputational.geometry.Line;
import com.icomputational.geometry.Point;

public class LineTest {

    @Test
    public void testLine() {
        Line l1 = new Line(2, 3, 4);
        Line l2 = new Line(3, 4.5, 6);
        Line l3 = new Line(19, 2, 0);

        assertTrue(l1.isIdentical(l2));
        assertNull(l1.intersect(l2));

        Point point = l1.intersect(l3);
        assertEquals(point, l3.intersect(l1));

        assertTrue(l1.contains(point));
        assertTrue(l3.contains(point));

        Line horizontal = Line.horizontalLine(400);
        assertEquals(new Point(-598.0, 400.0), l1.intersect(horizontal));
    }
}