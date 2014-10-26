package com.icomputational.geoelements;

import com.icomputational.geometry.Geometry;

/**
 * The {@link Ellipsoid} provides support of converting between 3D Cartesian and 
 * ellipsoidal latitude, longitude and height coordinates.
 */
public class Ellipsoid {
    /**
     * The ellipsoid used by OSGB36 of UK.
     */
    public static final Ellipsoid AIRY1830 = new Ellipsoid(6377563.396, 6356256.909);
    /**
     * The WGS84 ellipsoid.
     */
    public static final Ellipsoid GRS80 = new Ellipsoid(6378137.000, 6356752.3141);
    
    private final double a;
    private final double b;
    // square of e
    private final double e2;
    
    /**
     * Construct from axes.
     * @param a semi-major axis a.
     * @param b semi-minor axis b.
     */
    public Ellipsoid(double a, double b) {
        this.a = a;
        this.b = b;
        this.e2 = 1 - (b / a) * (b / a);
    }
    
    public double a() {
        return a;
    }
    
    public double b() {
        return b;
    }

    public double e2() {
        return e2;
    }
    
    /**
     * Convert ellipsoidal coordinates to 3D Cartesian coordinates.
     * @param coord ellipsoidal coordinates.
     * @param height the height
     * @return 3D Cartesian coordinates.
     */
    public Coord3D toCartesian(Coordinate coord, double height) {
        final double sinPhi = Math.sin(coord.latitudeRadians());
        final double cosPhi = Math.cos(coord.latitudeRadians());
        final double sinLamda = Math.sin(coord.longitudeRadians());
        final double cosLamda = Math.cos(coord.longitudeRadians());
        double v = a / Math.sqrt(1 - e2 * sinPhi * sinPhi);
        double x = (v + height) * cosPhi * cosLamda;
        double y = (v + height) * cosPhi * sinLamda;
        double z = ((1 - e2) * v + height) * sinPhi;
        return new Coord3D(x, y, z);
    }
    
    /**
     * Convert 3D Cartesian coordinates to ellipsoidal coordinates.
     * @param coord 3D Cartesian coordinates.
     * @param precision expected precision, should be positive.
     * @return ellipsoidal coordinates.
     */
    public Coordinate toEllipsoidal(Coord3D coord, final double precision) {
        if (precision <= Double.MIN_NORMAL) {
            throw new IllegalArgumentException("Invalid precision");
        }

        final double lamda = Math.atan(coord.y() / coord.x());
        final double p = Math.sqrt(coord.x() * coord.x() + coord.y() * coord.y());
        double phi = Math.atan(coord.z() / (p * (1 - e2)));
        double lastPhi;
        do {
            double sinPhi = Math.sin(phi);
            double v = a / Math.sqrt(1 - e2 * sinPhi * sinPhi);
            lastPhi = phi;
            phi = Math.atan((coord.z() + e2 * v * sinPhi) / p);
        } while (Math.abs(lastPhi - phi) > precision);
        return new Coordinate(Math.toDegrees(lamda), Math.toDegrees(phi));
    }
    
    /**
     * Calculate distance of two coordinates with Vincenty formula.
     * @param coord1 the first coordinate.
     * @param coord2 the second coordinate.
     * @return the distance of the two coordinate on the ellipsoid surface.
     * @see <a href="http://en.wikipedia.org/wiki/Vincenty%27s_formulae">Vincenty's formulae</a>
     * @see <a href="http://www.movable-type.co.uk/scripts/latlong-vincenty.html">Vincenty formula for distance between two Latitude/Longitude points</a>
     */
    public double getDistance(Coordinate coord1, Coordinate coord2) {
        if (coord1.equals(coord2)) {
            return 0D;
        }

        // flattening
        final double f = (a - b) / a;
        // 蠁1, 蠁2 = geodetic latitude
        final double phi1 = coord1.latitudeRadians();
        final double phi2 = coord2.latitudeRadians();
        // L = difference in longitude
        final double L = Math.abs(coord1.longitudeRadians() - coord2.longitudeRadians());

        // U1 = atan((1鈭抐).tan蠁1) (U is 鈥榬educed latitude鈥�)
        final double U1 = Math.atan((1 - f) * Math.tan(phi1));
        final double sinU1 = Math.sin(U1);
        final double cosU1 = Math.cos(U1);

        // U2 = atan((1鈭抐).tan蠁2)
        final double U2 = Math.atan((1 - f) * Math.tan(phi2));
        final double sinU2 = Math.sin(U2);
        final double cosU2 = Math.cos(U2);

        // first approximation
        double lambda = L;
        int iterLimit = 100;

        double lambdaP, cosSqAlpha, sigma, sinSigma, cos2SigmaM, cosSigma;
        do {
            double sinLamda = Math.sin(lambda);
            double cosLamda = Math.cos(lambda);
            // sin蟽
            sinSigma = Math.sqrt(square(cosU2 * sinLamda) + square(cosU1 * sinU2 - sinU1 * cosU2 * cosLamda));
            if (Geometry.almostZero(sinSigma)) {
                return 0; // // co-incident points
            }

            // cos蟽
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLamda;
            sigma = Math.atan2(sinSigma, cosSigma);

            double sinAlpha = cosU1 * cosU2 * sinLamda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
            if (Double.isNaN(cos2SigmaM)) {
                cos2SigmaM = 0;
            }
            double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));

            lambdaP = lambda;
            lambda = L + (1 - C) * f * sinAlpha
                * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
        } while (Math.abs(lambda - lambdaP) > 1E-12 && --iterLimit > 0);

        // formula failed to converge
        if (iterLimit == 0) {
            return Double.NaN;
        }

        double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) -
            B / 6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
        double s = b * A * (sigma - deltaSigma);
        return s;
    }

    private static double square(double x) {
        return x * x;
    }
}
