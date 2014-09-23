package com.icomputational.geoelements;

/**
 * The {@link Coord3D} represents coordinates in a 3D Cartesian coordinate system.
 */
public class Coord3D {
	private final double x;
	private final double y;
	private final double z;
	
	public Coord3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double x() {
		return x;
	}
	
	public double y() {
		return y;
	}
	
	public double z() {
		return z;
	}
	
	@Override
	public String toString() {
		return "x=" + x + ", y=" + y + ", z=" + z;
	}
}
