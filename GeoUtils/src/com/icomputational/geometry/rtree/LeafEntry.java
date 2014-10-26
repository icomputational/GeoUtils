package com.icomputational.geometry.rtree;

import com.icomputational.geometry.BoundingBox;
import com.icomputational.geometry.Shape;

class LeafEntry implements Entry {
    final Shape shape;

    LeafEntry(Shape shape) {
        this.shape = shape;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return shape.boundingBox();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((shape == null) ? 0 : shape.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass())
            return false;
        LeafEntry other = (LeafEntry) obj;
        return getBoundingBox().equals(other.getBoundingBox()) && shape.equals(other.shape);
    }
}