package com.icomputational.geoelements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

import com.icomputational.geometry.Polyline;

public class GeoPathTest {

    @Test
    public void testSimple() {
        GeoPath path = new GeoPath(new Coordinate(0, 0));
        assertEquals(1, path.size());
        assertFalse(path.isEmpty());

        List<Coordinate> list = path.toCoordinates();
        assertEquals(1, list.size());
        assertEquals(new Coordinate(0, 0), list.get(0));

        assertNotNull(path.get(0));

        path = new GeoPath();
        assertTrue(path.isEmpty());
    }

    @Test
    public void testIterator() {
        GeoPath path = new GeoPath(new int[] { 1, 2, 3, 4 });
        Iterator<Coordinate> itr = path.iterator();
        assertTrue(itr.hasNext());
        assertNotNull(itr.next());

        try {
            itr.remove();
        } catch (UnsupportedOperationException e) {
            // pass
        }

        assertTrue(itr.hasNext());
        assertNotNull(itr.next());

        assertFalse(itr.hasNext());
        try {
            assertNotNull(itr.next());
            fail("No exception thrown");
        } catch (NoSuchElementException e) {
            // pass
        }
    }

    @Test
    public void testMiddle() {
        GeoPath path1 = new GeoPath(new Coordinate(0, 0), new Coordinate(1D, 2D));
        assertEquals(new Coordinate(0, 0), path1.middle());

        GeoPath path2 = new GeoPath(new Coordinate(0, 0), new Coordinate(1D, 2D), new Coordinate(3D, 3D));
        assertEquals(new Coordinate(1D, 2D), path2.middle());
    }

    @Test
    public void testEquals() {
        GeoPath path1 = new GeoPath(new Coordinate(0, 0), new Coordinate(1D, 2D));
        GeoPath path2 = new GeoPath(new int[] { 0, 0, 1000000, 2000000 });
        GeoPath path3 = new GeoPath(new double[] { 0D, 0D, 1D, 2D });
        GeoPath path4 = new GeoPath(Arrays.asList(new Coordinate(0, 0), new Coordinate(1D, 2D)));

        assertEquals(path1, path1);

        assertEquals(path1, path2);
        assertEquals(path2, path3);
        assertEquals(path3, path4);
        assertEquals(path1.hashCode(), path2.hashCode());
        assertEquals(path2.hashCode(), path3.hashCode());
        assertEquals(path3.hashCode(), path4.hashCode());

        assertTrue(Arrays.equals(new int[] { 0, 0, 1000000, 2000000 }, path1.intArray()));
    }

    @Test
    public void testParse() {
        GeoPath path = GeoPath.parse("100.000,34.000", "120.000,32.000");
        assertNotNull(path);

        path = GeoPath.parse();
        assertNotNull(path);
        assertTrue(path.isEmpty());

        assertNull(GeoPath.parse(""));
    }

    @Test
    public void testReverse() {
        int[] data = new int[] { 1, 2 };
        GeoPath path = new GeoPath(data);
        path = path.reverse();
        assertTrue(Arrays.equals(new int[] { 1, 2 }, path.intArray()));

        data = new int[] { 1, 2, 3, 4 };
        path = new GeoPath(data);
        path = path.reverse();
        assertTrue(Arrays.equals(new int[] { 3, 4, 1, 2 }, path.intArray()));

        data = new int[] { 1, 2, 3, 4, 5, 6 };
        path = new GeoPath(data);
        path = path.reverse();
        assertTrue(Arrays.equals(new int[] { 5, 6, 3, 4, 1, 2 }, path.intArray()));

        data = new int[] { 1, 2, 3, 4, 5, 6, 7, 8 };
        path = new GeoPath(data);
        path = path.reverse();
        assertTrue(Arrays.equals(new int[] { 7, 8, 5, 6, 3, 4, 1, 2 }, path.intArray()));

        data = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        path = new GeoPath(data);
        path = path.reverse();
        assertTrue(Arrays.equals(new int[] { 9, 10, 7, 8, 5, 6, 3, 4, 1, 2 }, path.intArray()));

        data = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
        path = new GeoPath(data);
        path = path.reverse();
        assertTrue(Arrays.equals(new int[] { 11, 12, 9, 10, 7, 8, 5, 6, 3, 4, 1, 2 }, path.intArray()));
    }

    @Test
    public void testSpecialCase() {
        GeoPath path = new GeoPath(new Coordinate(-117.84198760986328, 33.643028259277344), new Coordinate(
            -117.84198760986328, 33.643028259277344), new Coordinate(-117.84198760986328, 33.643028259277344));
        GeoProjection projection = new StereographicProjection(path.middle());
        Polyline polyline = projection.toPolyline(path);
        assertNotNull(polyline);

        assertEquals(path.get(0), projection.toCoordinate(polyline.interpolate(0)));
        assertEquals(path.get(0), projection.toCoordinate(polyline.interpolate(1)));
        assertEquals(path.get(0), projection.toCoordinate(polyline.interpolate(0.99)));
        assertEquals(path.get(0), projection.toCoordinate(polyline.interpolate(0.1)));

        assertNotNull(projection.toCoordinate(polyline.interpolate(0, 100)));
        assertNotNull(projection.toCoordinate(polyline.interpolate(1, 100)));
        assertNotNull(projection.toCoordinate(polyline.interpolate(0.1, 100)));
        assertNotNull(projection.toCoordinate(polyline.interpolate(0.99, 100)));
    }
}
