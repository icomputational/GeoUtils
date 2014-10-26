package com.icomputational.geoelements;

import java.util.List;

/**
 * A {@link Sphere} represents a sphere for geographic calculation.
 */
public class Sphere {
    /**
     * The default sphere for the earth, use 6371.009 km as radius.
     * @see <a href="https://en.wikipedia.org/wiki/Earth_radius">Earth Radius</a>
     */
    public static final Sphere EARTH = new Sphere(6371009);

    private final double radius;

    /**
     * Construct a sphere from a radius.
     */
    public Sphere(double radius) {
        this.radius = radius;
    }

    /**
     * Get the radius of this sphere.
     */
    public double radius() {
        return radius;
    }

    /**
     * Get distance between two points on Earth by using HAVERSINE method.
     * @see <a href="https://en.wikipedia.org/wiki/Haversine_formula">Haversine Formula</a>
     * @param from coordinate of the start point in degrees
     * @param to coordinate of the end point in degrees
     * @return distance in meters
     */
    public double getDistance(Coordinate from, Coordinate to) {
        if (!from.isValid() || !to.isValid()) {
            return Double.NaN;
        }

        double srcLngr = Math.toRadians(from.longitude());
        double srcLatr = Math.toRadians(from.latitude());
        double dstLngr = Math.toRadians(to.longitude());
        double dstLatr = Math.toRadians(to.latitude());

        double deltaLngr = Math.abs(dstLngr - srcLngr);
        if (deltaLngr > Math.PI) {
            deltaLngr = 2 * Math.PI - deltaLngr;
        }
        double sinDeltaLatr = Math.sin((dstLatr - srcLatr) / 2);
        double cosDeltaLngr = Math.sin(deltaLngr / 2);

        double a = sinDeltaLatr * sinDeltaLatr + cosDeltaLngr * cosDeltaLngr * Math.cos(srcLatr) * Math.cos(dstLatr);
        double b = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return radius * b;
    }

    /**
     * Calculate bearing of two points on a spherical Earth.
     * Bearing is the clockwise angle between the north direction and the moving direction.
     * @param from pointing from this point
     * @param to to this point
     * @return the bearing in degree [0-360].
     */
    public double getBearing(Coordinate from, Coordinate to) {
        if (!from.isValid() || !to.isValid()) {
            return Double.NaN;
        }
        return (Math.toDegrees(getAzimuth(from, to)) + 360) % 360;
    }

    /**
     * Calculate the signed angle between the specific line and reference direction on a spherical Earth.
     * @param from pointing from this point
     * @param to to this point
     * @return the bearing in radians [-pi~pi].
     */
    protected double getAzimuth(Coordinate from, Coordinate to) {
        double srcLngr = Math.toRadians(from.longitude());
        double srcLatr = Math.toRadians(from.latitude());
        double dstLngr = Math.toRadians(to.longitude());
        double dstLatr = Math.toRadians(to.latitude());

        double deltaLngr = dstLngr - srcLngr;
        double cosDstLatr = Math.cos(dstLatr);

        double a = Math.sin(deltaLngr) * cosDstLatr;
        double b = Math.cos(srcLatr) * Math.sin(dstLatr) - Math.sin(srcLatr) * cosDstLatr * Math.cos(deltaLngr);
        return Math.atan2(a, b);
    }

    /**
     * Shifting the coordinate "from" along the perpendicular direction between two coordinates "from" and "to". 
     * @param from source coordinate.
     * @param to destination coordinate.
     * @param offset the distance to be shifted, in meters. Positive if shifting towards right-side.
     * @return the coordinate after shifting.
     */
    public Coordinate perpendicularShift(Coordinate from, Coordinate to, double offset) {
        if (offset == 0D) {
            return from;
        }

        double perpendicularAzimuth = getAzimuth(from, to) + Math.PI / 2;

        double srcLngr = Math.toRadians(from.longitude());
        double srcLatr = Math.toRadians(from.latitude());
        double sinSrcLatr = Math.sin(srcLatr);
        double cosSrcLatr = Math.cos(srcLatr);
        double sinOffset = Math.sin(offset / radius);
        double cosOffset = Math.cos(offset / radius);

        double shiftedLatr = Math.asin(sinSrcLatr * cosOffset +
            cosSrcLatr * sinOffset * Math.cos(perpendicularAzimuth));
        double shiftedLngr = srcLngr + Math.atan2(Math.sin(perpendicularAzimuth) * sinOffset * cosSrcLatr,
            cosOffset - sinSrcLatr * Math.sin(shiftedLatr));

        return new Coordinate(Math.toDegrees(shiftedLngr), Math.toDegrees(shiftedLatr));
    }

    /**
     * Interpolation along the polyline.
     * @param coordinates the points describing the polyline.
     * @param proportion the percentage of length between start point and interpolated point to the polyline length. 
     * @return coordinate of the interpolated point.
     **/
    public Coordinate interpolate(List<Coordinate> coordinates, double proportion) {
        return interpolate(coordinates, proportion, 0);
    }

    /**
     * Interpolation and then shift the interpolated point to specified side of the polyline.
     * @param coordinates the points describing the polyline.
     * @param proportion the percentage of length between start point and interpolated point to the polyline length. 
     * @param offset the distance to be shifted, in meters. Positive if shifting towards right-side.
     * @return coordinate of the interpolated and shifted point.
     */
    public Coordinate interpolate(List<Coordinate> coordinates, double proportion, double offset) {
        int coordinatesNumber = coordinates.size();
        if (coordinatesNumber < 2) {
            throw new IllegalArgumentException("coordinates have invalid size: " + coordinatesNumber);
        }

        if (proportion == 0D) {
            return perpendicularShift(coordinates.get(0), coordinates.get(1), offset);
        }
        if (proportion == 1D) {
            return perpendicularShift(coordinates.get(coordinatesNumber - 1), coordinates.get(coordinatesNumber - 2),
                -offset);
        }

        Coordinate pointOnRoad;
        int index = 0;
        if (coordinatesNumber > 2) {
            //interpolation along the polyline
            double[] streetSegmentLengths = new double[coordinatesNumber - 1];
            double streetTotalLength = 0.0;
            for (int i = 1; i < coordinatesNumber; i++) {
                double segmentLength = getDistance(coordinates.get(i), coordinates.get(i - 1));
                streetSegmentLengths[i - 1] = segmentLength;
                streetTotalLength += segmentLength;
            }

            double dstHouseDistance = streetTotalLength * proportion;
            for (; index < streetSegmentLengths.length; index++) {
                double length = streetSegmentLengths[index];
                if (dstHouseDistance < length) {
                    proportion = dstHouseDistance / length;
                    break;
                } else {
                    dstHouseDistance -= length;
                }
            }
        }

        //interpolation along the line segment.
        Coordinate from = coordinates.get(index);
        Coordinate to = coordinates.get(index + 1);
        pointOnRoad = new Coordinate(from.longitude() + (to.longitude() - from.longitude()) * proportion,
            from.latitude() + (to.latitude() - from.latitude()) * proportion);

        return perpendicularShift(pointOnRoad, to, offset);
    }
}
