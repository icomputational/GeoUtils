package com.icomputational.geoelements;

import com.icomputational.geometry.Point;

/**
 * A static helper class for UK related coordinates conversion and transformation.
 * @see <a href="http://badc.nerc.ac.uk/help/coordinates/OSGB.pdf">A guide to coordinate systems in Great Britain</a>
 */
public class UK {
    /**
     * The transformation from WGS84 datum to OSGB36 datum
     */
    public static final HelmertTransformation WGS84_OSGB36 = new HelmertTransformation(-446.448, 125.157, -542.06,
        20.4894, -0.1502, -0.247, -0.8421);

    /**
     * The transformation from OSGB36 datum to WGS84 datum
     */
    public static final HelmertTransformation OSGB36_WGS84 = new HelmertTransformation(446.448, -125.157, 542.06,
        -20.4894, 0.1502, 0.247, 0.8421);

    /**
     * Transverse Mercator projection for UK National Grid
     */
    public static final TransverseMercatorProjection UK_NATIONAL_GRID = new TransverseMercatorProjection(0.9996012717,
        new Coordinate(-2, 49), new Point(400000, -100000), Ellipsoid.AIRY1830);

    /**
     * Transform a Airy 1830 coordinate to GRS80 coordinate with Helmert transformation.
     */
    public static final Coordinate toGRS80(Coordinate coord) {
        return Ellipsoid.GRS80
            .toEllipsoidal(OSGB36_WGS84.transform(Ellipsoid.AIRY1830.toCartesian(coord, 0)), 0.000001);
    }

    /**
     * Transform a GRS80 coordinate to Airy 1830 coordinate with Helmert transformation.
     */
    public static final Coordinate toAiry(Coordinate coord) {
        return Ellipsoid.AIRY1830
            .toEllipsoidal(WGS84_OSGB36.transform(Ellipsoid.GRS80.toCartesian(coord, 0)), 0.000001);
    }

    /**
     * Transform UK National Grid reference to GRS80 coordinate.
     * Note this transformation only applied to UK, not other regions.
     * @param eastings the eastings of the reference.
     * @param northings the northings of the reference.
     * @return a GRS80 coordinate.
     */
    public static final Coordinate toGRS80(double eastings, double northings) {
        Coordinate coord = UK_NATIONAL_GRID.toCoordinate(eastings, northings);
        return toGRS80(coord);
    }
}
