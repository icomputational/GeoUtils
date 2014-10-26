package com.icomputational.geoelements;

import com.icomputational.geometry.Point;

public class MercatorProjection extends GeoProjection {
    public static final double PARALLEL_LIMIT = 80;
    private double centralMeridian;

    /**
     * Construct from a central meridian.
     * @param meridian the central maridian.
     */
    public MercatorProjection(double meridian) {
        this.centralMeridian = meridian;
    }

    @Override
    public Point toPoint(double longitude, double latitude) {
        if (Math.abs(latitude) > PARALLEL_LIMIT) {
            throw new IllegalArgumentException("Unsupported latitude " + latitude);
        }

        double x = super.normalizeLongitude(longitude - centralMeridian);
        double latitudeRadians = Math.toRadians(latitude);
        double y = Math.toDegrees(Math.log(Math.tan(latitudeRadians) + 1 / Math.cos(latitudeRadians)));
        return new Point(x, y);
    }

    @Override
    public Coordinate toCoordinate(double x, double y) {
        double latitude = Math.toDegrees(Math.atan(Math.sinh(Math.toRadians(y))));
        double longtitude = super.normalizeLongitude(x + centralMeridian);
        return new Coordinate(longtitude, latitude);
    }
}
