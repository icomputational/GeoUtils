package com.icomputational.geometry.rtree;

import com.icomputational.geometry.BoundingBox;

/**
 * An {@link Entry} represents an entry of R-tree node.
 */
abstract interface Entry {
    /**
     * Returns the bounding box of this entry.
     */
    public abstract BoundingBox getBoundingBox();
}
