package com.icomputational.geometry.rtree;

import java.util.Comparator;
import java.util.List;

import com.icomputational.geometry.BoundingBox;
import com.icomputational.geometry.util.BoundingBoxBuilder;

/**
 * A {@link Distribution} is used by R*-tree to distribute entries.
 */
class Distribution {
    static final Comparator<Distribution> COMPARATOR = new Comparator<Distribution>() {

        @Override
        public int compare(Distribution left, Distribution right) {
            if (left.overlapArea < right.overlapArea) {
                return -1;
            } else if (left.overlapArea > right.overlapArea) {
                return 1;
            }

            return Double.compare(left.area, right.area);
        }
    };

    private final int index;
    private final List<Entry> entries;
    private final BoundingBox firstBox;
    private final BoundingBox secondBox;
    private final double overlapArea;
    private final double area;

    /**
     * Construct a distribution from a entry list and split index.
     */
    Distribution(List<Entry> entries, int index) {
        this.entries = entries;
        this.index = index;

        BoundingBoxBuilder builder1 = new BoundingBoxBuilder(entries.get(0).getBoundingBox());
        for (int i=1; i<index; i++) {
            builder1.add(entries.get(i).getBoundingBox());
        }
        firstBox = builder1.toBoundingBox();

        BoundingBoxBuilder builder2 = new BoundingBoxBuilder(entries.get(index).getBoundingBox());
        for (int i=index+1; i<entries.size(); i++) {
            builder2.add(entries.get(i).getBoundingBox());
        }
        secondBox = builder2.toBoundingBox();
        area = firstBox.area() + secondBox.area();
        overlapArea = firstBox.getOverlap(secondBox);
    }

    /**
     * Calculate area value of this distribution.
     * @return area[bb(first group)] + area[bb(second group)]
     */
    public double area() {
        return area;
    }

    /**
     * Calculate margin value of this distribution.
     * @return margin[bb(first group)] + margin[bb(second group)]
     */
    public double margin() {
        return firstBox.margin() + secondBox.margin();
    }

    /**
     * Calculate overlap value of this distribution.
     * @return area[bb(first group) AND bb(second group)]
     */
    public double overlap() {
        return overlapArea;
    }

    /**
     * Get first entry group of this distribution.
     */
    public List<Entry> getFirstGroup() {
        return entries.subList(0, index);
    }

    /**
     * Get second entry group of this distribution.
     */
    public List<Entry> getSecondGroup() {
        return entries.subList(index, entries.size());
    }
}