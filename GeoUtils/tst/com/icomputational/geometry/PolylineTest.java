package com.icomputational.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.icomputational.geoelements.Coordinate;
import com.icomputational.geoelements.CylindricalProjection;
import com.icomputational.geoelements.GeoProjection;
import com.icomputational.geoelements.StereographicProjection;

public class PolylineTest {
    double[] locationPoints_DonMilagroDr = {
            -118.351058,
            34.006667,
            -118.351027,
            34.006952,
            -118.351035,
            34.007042,
            -118.351059,
            34.00713,
            -118.351105,
            34.007223,
            -118.35117,
            34.007308,
            -118.351243,
            34.007375,
            -118.351327,
            34.007432,
            -118.351411,
            34.007473,
            -118.3515,
            34.007504,
            -118.351643,
            34.007534,
            -118.351771,
            34.007546,
            -118.351918,
            34.007544,
            -118.352334,
            34.007507,
            -118.352463,
            34.007513,
            -118.35259,
            34.007536,
            -118.352711,
            34.007575,
            -118.352811,
            34.007623,
            -118.352902,
            34.007681,
            -118.352981,
            34.007749,
            -118.353049,
            34.007827,
            -118.353098,
            34.0079,
            -118.35314,
            34.007989,
            -118.353167,
            34.008082,
            -118.353178,
            34.00819,
            -118.353172,
            34.00828 };

    double[] DonTomasoDr = {
            -118.350741, 34.005018,
            -118.350989, 34.004937,
            -118.351211, 34.004850,
            -118.351426, 34.004749,
            -118.351631, 34.004636,
            -118.351779, 34.004544,
            -118.351966, 34.004412,
            -118.352142, 34.004268,
            -118.352291, 34.004129,
            -118.353953, 34.002477
    };

    double[] culverBlvd = {
            -118.433414, 33.973842,
            -118.433407, 33.973826,
            -118.433137, 33.973298,
            -118.433095, 33.973185,
            -118.433071, 33.973070,
            -118.433064, 33.972952,
            -118.433071, 33.972852,
            -118.433096, 33.972736,
            -118.433137, 33.972624,
            -118.433194, 33.972517,
            -118.433267, 33.972416,
            -118.433354, 33.972324,
            -118.433439, 33.972252,
            -118.433548, 33.972178,
            -118.433654, 33.972122,
            -118.435120, 33.971453,
            -118.437335, 33.970339,
            -118.437497, 33.970243,
            -118.437652, 33.970137,
            -118.437832, 33.969993,
            -118.437996, 33.969836,
            -118.438112, 33.969706,
            -118.440434, 33.966846
    };

    double[] rodeoLn = {
            -118.345182, 34.020298,
            -118.348448, 34.020318,
            -118.348507, 34.020336,
            -118.348549, 34.020363,
            -118.348577, 34.020395,
            -118.348596, 34.020437,
            -118.348599, 34.020468,
            -118.348592, 34.021368
    };

    @Test
    public void testSimple() {
        Point[] points = { new Point(0, 0), new Point(1, 1) };
        Polyline polyline = new Polyline(points);
        assertNear(new Point(0, 0), polyline.interpolate(0));
        assertNear(new Point(1, 1), polyline.interpolate(1));
        assertNear(new Point(0.5, 0.5), polyline.interpolate(0.5));
    }

    @Test
    public void testOffset() {
        Point[] points = { new Point(1, -1), new Point(0, 0), new Point(1, 1) };
        Polyline polyline = new Polyline(points);
        assertNear(new Point(1.7071067811865475, 0.29289321881345254), polyline.interpolate(1, 1));
        assertNear(new Point(1, 0), polyline.interpolate(0.75, Math.sqrt(2) / 2));
        assertNear(new Point(1, 0), polyline.interpolate(0.5, 1));
        assertNear(new Point(-1, 0), polyline.interpolate(0.5, -1));
    }

    private void assertNear(Point p1, Point p2) {
        assertTrue(p1 + "<>" + p2, p1.distance(p2) < 1E-15);
    }

    Point[] toPoints(double[] data, GeoProjection projection) {
        Point[] points = new Point[data.length / 2];
        for (int i = 0; i < points.length; i++) {
            points[i] = projection.toPoint(new Coordinate(data[i * 2], data[i * 2 + 1]));
        }
        return points;
    }

    private final GeoProjection cylindrical = new CylindricalProjection(-120.0);
    private final GeoProjection stereographic = new StereographicProjection(new Coordinate(-118.0, 34));

    @Test
    public void testReal() {
        double proportion = 0.053691275167785234;
        Polyline polyline1 = new Polyline(toPoints(rodeoLn, cylindrical));
        Coordinate loc1 = cylindrical.toCoordinate(polyline1.interpolate(proportion));
        assertEquals(new Coordinate(-118.34541810114202, 34.020299445812256), loc1);

        Polyline polyline2 = new Polyline(toPoints(rodeoLn, stereographic));
        Coordinate loc2 = stereographic.toCoordinate(polyline2.interpolate(proportion));
        assertEquals(new Coordinate(-118.34542936623554, 34.02029951781768), loc2);
    }

    @Test
    public void testSpecial() {
        Polyline polyline = new Polyline(Arrays.asList(
            new Point(-0.09787240485381068, 0.2722060202351565),
            new Point(0, 0),
            new Point(0.013221234941833667, -0.02668681178655881)));
        Point p = polyline.interpolate(1.0, 0);
        assertNotNull(p);
        assertEquals(new Point(0.013221234941833667, -0.02668681178655881), p);
    }
}
