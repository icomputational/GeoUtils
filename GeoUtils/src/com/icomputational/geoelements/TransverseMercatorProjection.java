package com.icomputational.geoelements;

import com.icomputational.geometry.Point;

/**
 * A simple implementation of Transverse Mercator projection according to the A guide to coordinate systems in Great Britain.
 * @see <a href="http://badc.nerc.ac.uk/help/coordinates/OSGB.pdf">A guide to coordinate systems in Great Britain</a>
 */
public class TransverseMercatorProjection extends GeoProjection {
    /**
     * The threshold in meters, which balancing the precision and computation time.
     * @see #toCoordinate(double, double)
     */
    private static final double PRECISION_THRESHOLD = 0.00001;
    private final double f0;
    private final double n0;
    private final double e0;
    private final double phi0;
    private final double lamda0;

    private final double a;
    private final double b;
    // square of e
    private final double e2;
    // (a - b) / (a + b)
    private final double n;

    private final double cn0;
    private final double cn1;
    private final double cn2;
    private final double cn3;

    /**
     * Construct a Transverse Mercator Projection instance.
     * @param f scale factor on central meridian.
     * @param origin true origin
     * @param mapOrigin map coordinate of true origin.
     */
    public TransverseMercatorProjection(double f, Coordinate origin, Point mapOrigin, Ellipsoid ellipsoid) {
        this.f0 = f;
        this.e0 = mapOrigin.x();
        this.n0 = mapOrigin.y();
        this.phi0 = origin.latitudeRadians();
        this.lamda0 = origin.longitudeRadians();

        this.a = ellipsoid.a();
        this.b = ellipsoid.b();
        this.e2 = ellipsoid.e2();
        this.n = (a - b) / (a + b);

        this.cn0 = 1 + n + 5.0 / 4.0 * n * n + 5.0 / 4.0 * n * n * n;
        this.cn1 = 3 * n + 3 * n * n + 21.0 / 8.0 * n * n * n;
        this.cn2 = 15.0 / 8.0 * n * n + 15.0 / 8.0 * n * n * n;
        this.cn3 = 35.0 / 24.0 * n * n * n;
    }

    @Override
    public Point toPoint(double longitude, double latitude) {
        final double phi = Math.toRadians(latitude);
        final double lamda = Math.toRadians(longitude);

        final double dLamda = lamda - lamda0;
        final double dLamda2 = dLamda * dLamda;
        final double dLamda4 = dLamda2 * dLamda2;
        final double dLamda6 = dLamda4 * dLamda2;

        final double sinPhi = Math.sin(phi);
        final double cosPhi = Math.cos(phi);
        final double cosPhi2 = cosPhi * cosPhi;
        final double cosPhi3 = cosPhi2 * cosPhi;
        final double cosPhi5 = cosPhi2 * cosPhi3;
        final double tanPhi = Math.tan(phi);
        final double tanPhi2 = tanPhi * tanPhi;
        final double tanPhi4 = tanPhi2 * tanPhi2;

        // ν
        final double tmp0 = (1 - e2 * sinPhi * sinPhi);
        final double mu = a * f0 * Math.pow(tmp0, -0.5);
        // ρ
        final double rho = a * f0 * (1 - e2) * Math.pow(tmp0, -1.5);
        // square of η
        final double eta2 = mu / rho - 1;

        double M = computeM(phi);
        double I = M + n0;
        double II = mu / 2 * sinPhi * cosPhi;
        double III = mu / 24 * sinPhi * cosPhi3 * (5 - tanPhi2 + 9 * eta2);
        double IIIA = mu / 720 * sinPhi * cosPhi5 * (61 - 58 * tanPhi2 + tanPhi2 * tanPhi2);
        double IV = mu * cosPhi;
        double V = mu / 6 * cosPhi3 * (mu / rho - tanPhi2);
        double VI = mu / 120 * cosPhi5 * (5 - 18 * tanPhi2 + tanPhi4 + 14 * eta2 - 58 * tanPhi2 * eta2);
        double N = I + II * dLamda2 + III * dLamda4 + IIIA * dLamda6;
        double E = e0 + (IV + V * dLamda2 + VI * dLamda4) * dLamda;

        return new Point(E, N);
    }

    @Override
    public Coordinate toCoordinate(double E, double N) {
        final double dE = (E - e0);
        final double dE2 = dE * dE;
        final double dE4 = dE2 * dE2;
        final double dE6 = dE4 * dE2;

        final double dN = (N - n0);
        double phi = dN / (a * f0) + phi0;
        double M;
        do {
            M = computeM(phi);
            phi = (N - n0 - M) / (a * f0) + phi;
        } while (Math.abs(N - n0 - M) > PRECISION_THRESHOLD);

        final double sinPhi = Math.sin(phi);
        final double cosPhi = Math.cos(phi);
        final double secPhi = 1 / cosPhi;

        final double tanPhi = Math.tan(phi);
        final double tanPhi2 = tanPhi * tanPhi;
        final double tanPhi4 = tanPhi2 * tanPhi2;

        // ν
        final double tmp0 = (1 - e2 * sinPhi * sinPhi);
        final double mu = a * f0 * Math.pow(tmp0, -0.5);
        final double mu2 = mu * mu;
        final double mu3 = mu2 * mu;
        final double mu5 = mu3 * mu2;

        // ρ
        final double rho = a * f0 * (1 - e2) * Math.pow(tmp0, -1.5);
        // square of η
        final double eta2 = mu / rho - 1;

        double VII = tanPhi / (2 * rho * mu);
        double VIII = tanPhi / (24 * rho * mu3) * (5 + 3 * tanPhi2 + eta2 - 9 * tanPhi2 * eta2);
        double IX = tanPhi / (720 * rho * mu5) * (61 + 90 * tanPhi2 + 45 * tanPhi4);
        double X = secPhi / mu;
        double XI = secPhi / (6 * mu3) * (mu / rho + 2 * tanPhi2);
        double XII = secPhi / (120 * mu5) * (5 + 28 * tanPhi2 + 24 * tanPhi4);
        double XIIA = secPhi / (5040 * mu5 * mu2) * (61 + 662 * tanPhi2 + 1320 * tanPhi4 + 720 * tanPhi2 * tanPhi4);

        phi = phi - VII * dE2 + VIII * dE4 - IX * dE6;
        double lamda = lamda0 + (X - XI * dE2 + XII * dE4 - XIIA * dE6) * dE;
        return new Coordinate(Math.toDegrees(lamda), Math.toDegrees(phi));
    }

    private double computeM(double phi) {
        double dPhi = phi - phi0;
        double sPhi = phi + phi0;

        return b
            * f0
            * (cn0 * dPhi - cn1 * Math.sin(dPhi) * Math.cos(sPhi) + cn2 * Math.sin(2 * dPhi) * Math.cos(2 * sPhi) - cn3
                * Math.sin(3 * dPhi) * Math.cos(3 * sPhi));
    }
}
