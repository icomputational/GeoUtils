package com.icomputational.geoelements;

import com.icomputational.geometry.Point;

/**
 * The {@link CylindricalProjection} represents a simple projection for geography location.
 * @see <a href="http://en.wikipedia.org/wiki/Map_projection#Cylindrical">Cylindrical projection</a>
 */
public class CylindricalProjection extends GeoProjection {

    private final double centralMeridian;

    /**
     * Construct a cylindrical project from a central meridian.
     * @param meridian the central meridian.
     */
    public CylindricalProjection(double meridian) {
        this.centralMeridian = normalizeLongitude(meridian);
    }

    @Override
    public Point toPoint(double longitude, double latitude) {
        double x = normalizeLongitude(longitude - centralMeridian);
        return new Point(x, latitude);
    }

    @Override
    public Coordinate toCoordinate(double x, double y) {
        double longitude = normalizeLongitude(centralMeridian + x);
        double latitude = y;
        return new Coordinate(longitude, latitude);
    }

}
