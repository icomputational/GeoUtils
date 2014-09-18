package com.icomputational.geoelements;

import static org.junit.Assert.*;

import org.junit.Test;

public class CoordinateTest {
	@Test
	public void testBaseCases() {
		Coordinate coord = new Coordinate(0D, 0D);
		assertEquals(Double.doubleToLongBits(0D), Double.doubleToLongBits(coord.longitude()));
        assertEquals(Double.doubleToLongBits(0D), Double.doubleToLongBits(coord.latitude()));

        assertTrue(coord.isValid());
        
        coord = new Coordinate(0D, Double.NaN);
        assertFalse(coord.isValid());

        coord = new Coordinate(181, 0);
        assertFalse(coord.isValid());

        coord = new Coordinate(19, 91);
        assertFalse(coord.isValid());
	}
	
	@Test
	public void testDecimalDegrees() {
		Coordinate coord = new Coordinate(0D, 0D);
		assertEquals(0, coord.decimalLatitude());
		assertEquals(0, coord.decimalLongitude());
		
		assertEquals(coord, Coordinate.fromDecimal(0, 0));

        coord = Coordinate.fromDecimal(1234567, 2345678);
        assertEquals(1234567, coord.decimalLongitude());
        assertEquals(2345678, coord.decimalLatitude());

        coord = new Coordinate(0.12345678, 0.12345678);
        assertEquals(123457, coord.decimalLongitude());
        assertEquals(123457, coord.decimalLatitude());

        coord = new Coordinate(0.1234564999, 0.1234564999);
        assertEquals(123456, coord.decimalLongitude());
        assertEquals(123456, coord.decimalLatitude());

        coord = new Coordinate(-0.12345678, -0.12345678);
        assertEquals(-123457, coord.decimalLongitude());
        assertEquals(-123457, coord.decimalLatitude());
	}
	
    @Test
    public void testParse() {
        Coordinate coordinate = Coordinate.parse("119.12345678,34.12345678");
        assertNotNull(coordinate);

        assertTrue(Math.abs(coordinate.longitude() - 119.12345678) < 1E-10);
        assertTrue(Math.abs(coordinate.latitude() - 34.12345678) < 1E-10);

        assertNull(Coordinate.parse(null));
        assertNull(Coordinate.parse(""));
        assertNull(Coordinate.parse("1234"));
        assertNull(Coordinate.parse("xyaz"));
        assertNull(Coordinate.parse("1234,434"));

        assertNotNull(Coordinate.fromDecimal(1000000, 1000000));
        assertNotNull(Coordinate.fromDecimal(0, 0));

        assertNull(Coordinate.fromDecimal(Integer.MAX_VALUE, 0));
        assertNull(Coordinate.fromDecimal(Integer.MIN_VALUE, 0));
        assertNull(Coordinate.fromDecimal(0, Integer.MAX_VALUE));
        assertNull(Coordinate.fromDecimal(0, Integer.MIN_VALUE));
    }

    @Test
    public void testCompare() {
        Coordinate coord1 = new Coordinate(0D, 0D);
        Coordinate coord2 = new Coordinate(0D, 1D);
        Coordinate coord3 = new Coordinate(1D, 0D);

        assertEquals(0, coord1.compareTo(coord1));
        assertEquals(-1, coord1.compareTo(coord2));
        assertEquals(1, coord2.compareTo(coord1));
        assertEquals(-1, coord1.compareTo(coord3));
        assertEquals(-1, coord2.compareTo(coord3));

        assertEquals(coord1, new Coordinate(0D, 0D));
    }
}
