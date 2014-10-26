package com.icomputational.geometry.rtree;

import java.util.ArrayList;
import java.util.List;

import com.icomputational.geometry.BoundingBox;
import com.icomputational.geometry.util.BoundingBoxBuilder;

/**
 * An {@link EntryGroup} represents a group of entries for R-tree to split entries of a node.
 */
class EntryGroup extends BoundingBoxBuilder {
    List<Entry> entries;

    EntryGroup(Entry seed, int maxEntries) {
        super(seed.getBoundingBox());
        entries = new ArrayList<Entry>(maxEntries);
        entries.add(seed);
    }

    void add(Entry entry) {
        entries.add(entry);
        super.add(entry.getBoundingBox());
    }

    /**
     * Calculate delta of area to cover specified bounding box.
     * @param bb a bounding box.
     * @return a delta area.
     */
    double getDelta(BoundingBox bb) {
        double newArea = (Math.max(maxX, bb.maxX()) - Math.min(minX, bb.minX()))
            * (Math.max(maxY, bb.maxY()) - Math.min(minY, bb.minY()));
        return newArea - area();
    }
}
