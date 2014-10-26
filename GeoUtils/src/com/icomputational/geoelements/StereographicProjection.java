package com.icomputational.geoelements;

import com.icomputational.geometry.Geometry;
import com.icomputational.geometry.Point;

/**
 * The {@link StereographicProjection} implements a stereographic projection.
 * @see <a href="http://en.wikipedia.org/wiki/Stereographic_projection">Stereographic projection</a>
 */
public class StereographicProjection extends GeoProjection {
    private static final double DIAMETER = 2 * RADIUS;
    // latitude in radians
    private final double centralLatitude;
    private final double centralLongitude;
    private final double sinCentralLat;
    private final double cosCentralLat;

    private final Coordinate centre;

    /**
     * Construct with a central location.
     * @param centre the central location.
     */
    public StereographicProjection(Coordinate centre) {
        this.centre = centre;

        this.centralLatitude = Math.toRadians(centre.latitude());
        this.centralLongitude = Math.toRadians(centre.longitude());
        this.sinCentralLat = Math.sin(centralLatitude);
        this.cosCentralLat = Math.cos(centralLatitude);
    }

    @Override
    public Point toPoint(double longitude, double latitude) {
        final double latitudeRadians = Math.toRadians(latitude);
        final double deltaLongitude = Math.toRadians(longitude) - centralLongitude;
        final double sinLat = Math.sin(latitudeRadians);
        final double cosLat = Math.cos(latitudeRadians);
        final double cosDelta = Math.cos(deltaLongitude);
        double k = DIAMETER / (1 + sinCentralLat * sinLat + cosCentralLat * cosLat * cosDelta);
        double x = k * cosLat * Math.sin(deltaLongitude);
        double y = k * (cosCentralLat * sinLat - sinCentralLat * cosLat * cosDelta);
        return new Point(x, y);
    }

    @Override
    public Coordinate toCoordinate(double x, double y) {
        if (Geometry.almostZero(x) && Geometry.almostZero(y)) {
            return centre;
        }

        final double a = Math.sqrt(x * x + y * y);
        final double c = 2 * Math.atan(a / DIAMETER);
        final double sinc = Math.sin(c);
        final double cosc = Math.cos(c);
        double latitude = Math.asin(cosc * sinCentralLat + y * sinc * cosCentralLat / a);
        double longitude = centralLongitude
            + Math.atan(x * sinc / (a * cosCentralLat * cosc - y * sinCentralLat * sinc));
        return new Coordinate(Math.toDegrees(longitude), Math.toDegrees(latitude));
    }
}
