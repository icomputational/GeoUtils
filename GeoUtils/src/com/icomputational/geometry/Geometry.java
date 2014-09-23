package com.icomputational.geometry;

/**
 * Some common routines for geometry calculation.
 * Note: this class is intended for internal use only, the implementation may be changed without
 * consider the compatibility.
 */
public class Geometry {
    private static final int maxUlps = 16;
    
    /**
     * Check if a double value is almost zero.
     * This is a simplified version of {{@link #almostEquals(double, double)}.
     */
    public static boolean almostZero(double v) {
        if (v == 0D) {
            return true;
        }
        
        if (Double.isNaN(v) || Double.isInfinite(v)) {
            return false;
        }
        
        long bits = Double.doubleToLongBits(v);
        if (bits < 0) {
            bits = -(Long.MIN_VALUE - bits);
        }
        
        return bits < maxUlps;
    }
    
    /**
     * Check if two double values are almost equals.
     * @see <a href="http://www.cygnus-software.com/papers/comparingfloats/comparingfloats.htm">Comparing floating point numbers</a>
     */
    public static boolean almostEquals(double v1, double v2) {
        if (v1 == v2) {
            return true;
        }
        
        if (Double.isNaN(v1) || Double.isNaN(v2) || Double.isInfinite(v1) || Double.isInfinite(v2)) {
            return false;
        }
        
        long bits1 = Double.doubleToLongBits(v1);
        long bits2 = Double.doubleToLongBits(v2);
        if (bits1 < 0) {
            bits1 = Long.MIN_VALUE - bits1;
        }
        
        if (bits2 < 0) {
            bits2 = Long.MIN_VALUE - bits2;
        }
        
        long diff = bits1 - bits2;
        if (diff < 0) {
            diff = -diff;
        }
        
        return diff < maxUlps;
    }
}
