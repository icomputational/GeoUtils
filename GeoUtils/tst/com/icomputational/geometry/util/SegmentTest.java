package com.icomputational.geometry.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.icomputational.geometry.Line;
import com.icomputational.geometry.Point;

public class SegmentTest {

    @Test
    public void testSimple() {
        Point p1 = new Point(1, 1);
        Point p2 = new Point(2, 2);
        Point p3 = new Point(2, 3);
        Point p4 = new Point(4, 4);
        Segment seg1 = new Segment(p1, p2);
        Segment seg2 = new Segment(p1, p2);

        Segment seg3 = new Segment(p2, p3);
        Segment seg4 = new Segment(p3, p4);
        assertEquals(seg1, seg2);
        assertEquals(seg1.hashCode(), seg2.hashCode());

        Line line = Line.horizontalLine(1.5);
        assertNotNull(seg1.intersect(line));

        assertNull(seg1.intersect(seg2));
        assertTrue(seg2.isJoined(seg3));
        assertFalse(seg1.isJoined(seg4));

        seg1.isEndPoint(p1);
        seg2.isEndPoint(p2);
        assertEquals(p1, seg1.left());
        assertEquals(p2, seg1.right());
        assertNotNull(seg1.toString());
    }

}
