package com.icomputational.geometry.rtree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.icomputational.geometry.BoundingBox;

/**
 * A {@link BranchEntry} represents a entry for {@link BranchNode}.
 */
class BranchEntry implements Entry {
    static final Comparator<Enlargement> ENLARGEMENT_COMPARATOR = new Comparator<Enlargement>() {
        @Override
        public int compare(Enlargement left, Enlargement right) {
            if (left.deltaOverlap < right.deltaOverlap) {
                return -1;
            } else if (left.deltaOverlap > right.deltaOverlap) {
                return 1;
            }

            if (left.deltaArea < right.deltaArea) {
                return -1;
            } else if (left.deltaArea > right.deltaArea) {
                return 1;
            }

            return Double.compare(left.entry().area(), right.entry().area());
        }

    };

    /**
     * A class used by chooseLeaf to find the node whose bounding box
     * needs least enlargement to include new entry.
     */
    class Enlargement {
        final double deltaArea;
        double deltaOverlap;
        BoundingBox result;

        Enlargement(BoundingBox result) {
            this.result = result;
            this.deltaArea = result.area() - BranchEntry.this.area();
        }

        BranchEntry entry() {
            return BranchEntry.this;
        }

        /**
         * Calculate overlap for an entry that overlaps with all other entries.
         */
        void computeOverlapDelta(List<Entry> entries) {
            deltaOverlap = 0;
            BoundingBox original = BranchEntry.this.getBoundingBox();
            for (Entry e : entries) {
                if (e == BranchEntry.this) {
                    continue;
                }

                double overlap = result.getOverlap(e.getBoundingBox());
                if (overlap == 0) {
                    continue;
                }

                double delta = overlap - original.getOverlap(e.getBoundingBox());
                deltaOverlap += delta;
            }
        }
    }

    static List<BranchEntry> toEntries(List<Enlargement> list) {
        List<BranchEntry> entries = new ArrayList<BranchEntry>(list.size());
        for (Enlargement enlargement : list) {
            entries.add(enlargement.entry());
        }
        return entries;
    }

    final Node child;
    private double area;
    private BoundingBox boundingBox;

    BranchEntry(Node child) {
        this.child = child;
        adjust();
    }

    public double area() {
        return area;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void adjust() {
        boundingBox = child.getBoundingBox();
        area = boundingBox.area();
    }

    public Enlargement getEnlargement(BoundingBox bb) {
        return new Enlargement(boundingBox.join(bb));
    }
}
