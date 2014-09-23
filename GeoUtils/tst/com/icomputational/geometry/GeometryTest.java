package com.icomputational.geometry;

import static org.junit.Assert.*;

import org.junit.Test;

public class GeometryTest {

    @Test
    public void testAlmostZero() {
        assertTrue(Geometry.almostZero(Double.longBitsToDouble(1)));
        assertTrue(Geometry.almostZero(Double.MIN_VALUE));
        assertTrue(Geometry.almostZero(Double.longBitsToDouble(Long.MIN_VALUE + 1)));
        assertTrue(Geometry.almostZero(Double.longBitsToDouble(0x8000000000000001L)));
        assertTrue(Geometry.almostZero(Double.longBitsToDouble(0x8000000000000002L)));
    }

    @Test
    public void testAlmostEquals() {
        assertTrue(Geometry.almostEquals(0D, 0D));

        assertTrue(Geometry.almostEquals(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        assertTrue(Geometry.almostEquals(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
        assertFalse(Geometry.almostEquals(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY));

        assertFalse(Geometry.almostEquals(Double.NaN, Double.NaN));

        assertTrue(Geometry.almostEquals(0, Double.MIN_VALUE));
        assertTrue(Geometry.almostEquals(0, -Double.MIN_VALUE));

        assertFalse(Geometry.almostEquals(Double.MIN_NORMAL, Double.MIN_VALUE));

        long bits = 12345678;
        assertTrue(Geometry.almostEquals(Double.longBitsToDouble(bits), Double.longBitsToDouble(bits + 1)));
        bits = Double.doubleToLongBits(-100D);
        assertTrue(Geometry.almostEquals(Double.longBitsToDouble(bits), Double.longBitsToDouble(bits + 1)));
    }
}
