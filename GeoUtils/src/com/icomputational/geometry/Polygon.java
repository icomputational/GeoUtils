/**
 * 
 */
package com.icomputational.geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author icomputational
 * A {@link Polygon} represents a polygon defined in OpenGIS.
 * Note the definition for polygon in OpenGIS is different from definition in computational geometry.
 * @see <a href="http://www.gdmc.nl/publications/2003/Polygons.pdf">Polygon definition</a>
 */
public class Polygon extends Shape {
    private final LinearRing outerRing;
    private List<LinearRing> innerRings;
    private final BoundingBox boundingBox;
    
    /**
     * Construct a polygon from a outer ring.
     */
    public Polygon(LinearRing ring) {
        if (ring == null) {
            throw new IllegalArgumentException("null outer ring");
        }

        this.outerRing = ring;
        boundingBox = ring.boundingBox();
    }
    
    /**
     * Returns true if this area is valid.
     */
    public boolean isValid() {
        if (innerRings != null) {
            for (LinearRing ring : innerRings) {
                if (! boundingBox.contains(ring.boundingBox())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Add an inner ring to this polygon.
     * @param ring an inner ring
     */
    public void addInnerRing(LinearRing ring) {
        if (ring == null) {
            throw new IllegalArgumentException("null innner ring");
        }

        if (innerRings == null) {
            innerRings = new ArrayList<LinearRing>();
        }

        innerRings.add(ring);
    }
    
    /* (non-Javadoc)
     * @see com.icomputational.geometry.Shape#boundingBox()
     */
    @Override
    public BoundingBox boundingBox() {
        return boundingBox;
    }

    /* (non-Javadoc)
     * @see com.icomputational.geometry.Shape#contains(double, double)
     */
    @Override
    public boolean contains(double x, double y) {
        if (! boundingBox.contains(x, y)) {
            return false;
        }

        int count = outerRing.intersectRay(x, y);
        if (innerRings != null) {
            for (LinearRing ring : innerRings) {
                if (ring.boundingBox().contains(x, y)) {
                    count += ring.intersectRay(x, y);
                }
            }
        }

        return (count % 2) != 0;
    }

    /* (non-Javadoc)
     * @see com.icomputational.geometry.Shape#overlaps(com.icomputational.geometry.BoundingBox)
     */
    @Override
    public boolean overlaps(BoundingBox bb) {
        if (bb.contains(boundingBox)) {
            return true;
        } else if (!outerRing.overlaps(bb)) {
            return false;
        }

        if (innerRings != null) {
            for (LinearRing ring : innerRings) {
                if (ring.contains(bb)) {
                    return false;
                }
            }
        }
        return true;
    }

}
