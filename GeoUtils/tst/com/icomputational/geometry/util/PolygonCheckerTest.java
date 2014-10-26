package com.icomputational.geometry.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.icomputational.geometry.Point;

public class PolygonCheckerTest {

    @Test
    public void testSimple() {
        PolygonChecker checker = new PolygonChecker();
        List<Point> points = Arrays.asList(new Point(1, 1), new Point(2, 2));
        assertFalse(checker.isSimple(points));

        points = Arrays.asList(new Point(1, 1), new Point(2, 2), new Point(1, 2));
        assertTrue(checker.isSimple(points));
    }

}
