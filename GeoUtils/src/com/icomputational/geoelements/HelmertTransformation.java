package com.icomputational.geoelements;

/**
 * The {@link HelmertTransformation} represents an approximate 3D Cartesian transformation.
 * @see <a href="http://en.wikipedia.org/wiki/Helmert_transformation">Helmert transformation</a>
 */
public class HelmertTransformation {
    private final double cx;
    private final double cy;
    private final double cz;
    private final double s;
    private final double rx;
    private final double ry;
    private final double rz;

    /**
     * Construct from the seven parameters
     * @param cx translation vector, Cx in meter
     * @param cy translation vector, Cy in meter
     * @param cz translation vector, Cz in meter.
     * @param s scale factor.
     * @param rx rotation matrix, Rx in arcseconds.
     * @param ry rotation matrix, Ry in arcseconds.
     * @param rz rotation matrix, Rz in arcseconds.
     */
    public HelmertTransformation(double cx, double cy, double cz, double s, double rx, double ry, double rz) {
        this.cx = cx;
        this.cy = cy;
        this.cz = cz;
        this.s = s;
        this.rx = Math.toRadians(rx / 3600);
        this.ry = Math.toRadians(ry / 3600);
        this.rz = Math.toRadians(rz / 3600);
    }

    /**
     * Transform a coordinate with parameters of this transformation.
     * @param src the coordinate to be transformed.
     * @return the transformation result.
     */
    public Coord3D transform(Coord3D src) {
        final double a = (1 + s * 1E-6);
        double x = cx + a * (src.x() - rz * src.y() + ry * src.z());
        double y = cy + a * (rz * src.x() + src.y() - rx * src.z());
        double z = cz + a * (-ry * src.x() + rx * src.y() + src.z());
        return new Coord3D(x, y, z);
    }
}
