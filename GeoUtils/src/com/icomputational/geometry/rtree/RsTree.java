package com.icomputational.geometry.rtree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.icomputational.geometry.BoundingBox;
import com.icomputational.geometry.Point;

/**
 * The {@link RsTree} is an implementation of R*-tree.
 * @see <a href="http://dbs.mathematik.uni-marburg.de/publications/myPapers/1990/BKSS90.pdf">R*-tree</a>
 */
public class RsTree extends RTree {
    private final int reInsertThreshold;
    public RsTree(int M, int m) {
        super(M, m);
        reInsertThreshold = (M * 3) / 10;
    }

    @Override
    protected Node chooseSubTree(Node node, BoundingBox bb, int level) {
        if (node.level() == level) {
            return node;
        }

        List<BranchEntry> entries;
        if (node.level() == 1) {
            // the child pointers in N point to leaves
            entries = ((BranchNode) node).sortByOverlapEnlargement(bb);
        } else {
            entries = ((BranchNode) node).sortByAreaEnlargement(bb);
        }
        return chooseSubTree(entries.get(0).child, bb, level);
    }

    @Override
    protected Node splitNode(Node node, Entry newEntry) {
        List<Entry> entries = node.getEntries(newEntry);

        // choose split axis
        int max = maxEntries - 2 * minEntries + 2;
        List<Distribution> distributionX = createDistributions(entries, new LowerX(), max);
        distributionX.addAll(createDistributions(entries, new UpperX(), max));
        double xMargin = getTotalMargin(distributionX);

        List<Distribution> distributionY = createDistributions(entries, new LowerY(), max);
        distributionY.addAll(createDistributions(entries, new UpperY(), max));
        double yMargin = getTotalMargin(distributionY);

        List<Distribution> list = (xMargin < yMargin) ? distributionX : distributionY;

        // choose a distribution with minimal overlap
        Collections.sort(list, Distribution.COMPARATOR);
        Distribution result = list.get(0);

        // split the node
        node.setEntries(result.getFirstGroup());
        return node.createPartner(result.getSecondGroup());
    }

    private List<Distribution> createDistributions(List<Entry> entries, Comparator<Entry> comparator, int maxK) {
        List<Entry> list = new ArrayList<Entry>(entries);
        Collections.sort(list, comparator);

        List<Distribution> result = new ArrayList<Distribution>(maxK);
        for (int k=1; k<=maxK; k++) {
            result.add(new Distribution(list, k));
        }
        return result;
    }

    /**
     * Calculate total margin of a distribution list.
     */
    private double getTotalMargin(List<Distribution> list) {
        double total = 0;
        for (Distribution dist : list) {
            total += dist.margin();
        }
        return total;
    }

    @Override
    protected void insert(Entry entry, int level) {
        insert(entry, level, true);
    }

    private void insert(Entry entry, int level, boolean firstCall) {
        Node node = chooseSubTree(rootNode, entry.getBoundingBox(), level);
        if (node.add(entry)) {
            adjustTree(node);
            return;
        }

        treatOverflow(node, entry, firstCall);
    }

    /**
     * Overflow treatment.
     */
    private void treatOverflow(Node node, Entry entry, boolean firstCall) {
        BranchNode parent = node.getParent();
        if (parent == null) {
            Node partner = splitNode(node, entry);
            // node is root, grow tree taller, create a new root
            rootNode = createRootNode(node.level() + 1, node, partner);
            return;
        }

        BranchEntry parentEntry = parent.getEntry(node);
        // re-insert
        if (firstCall) {
            List<Entry> entries = node.getEntries(entry);
            entries = sortByCentreDistance(entries, parentEntry.getBoundingBox().centre());
            boolean addNewEntry = true; // add new entry to current node
            for (Entry e : entries) {
            	if (e == entry) {
            		addNewEntry = false;
            		continue;
            	}
                node.delete(e);
            }
            if (addNewEntry) {
            	node.add(entry);
            }
            parentEntry.adjust();

            for (Entry e : entries) {
                insert(e, node.level(), false);
            }
        } else {
            Node partner = splitNode(node, entry);
            parentEntry.adjust();

            BranchEntry newEntry = parent.createEntry(partner);
            if (parent.add(newEntry)) {
                adjustTree(parent);
            } else {
                treatOverflow(parent, newEntry, true);
            }
        }
    }

    /**
     * Sort entries by distance of the centers of their bounding boxes and the specified center point
     * in decreasing order.
     * @return first P entries of result, where P is 30% of M.
     */
    private List<Entry> sortByCentreDistance(List<Entry> entries, Point centre) {
        List<CentreDistance> list = new ArrayList<CentreDistance>(entries.size());
        for (Entry e : entries) {
            list.add(new CentreDistance(e, centre));
        }
        Collections.sort(list);
        for (int i=list.size() - 1; i>=reInsertThreshold; i--) {
            list.remove(i);
        }
        List<Entry> result = new ArrayList<Entry>(list.size());
        for (CentreDistance cd : list) {
            result.add(cd.entry);
        }
        return result;
    }

    static class CentreDistance implements Comparable<CentreDistance> {
        Entry entry;
        double distance;

        CentreDistance(Entry entry, Point centre) {
            this.entry = entry;
            distance = centre.distance(entry.getBoundingBox().centre());
        }

        @Override
        public int compareTo(CentreDistance o) {
            return Double.compare(o.distance, this.distance);
        }

        @Override
        public int hashCode() {
            return (int) Double.doubleToLongBits(distance);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof CentreDistance))
                return false;

            CentreDistance other = (CentreDistance) obj;
            return Double.doubleToLongBits(distance) == Double.doubleToLongBits(other.distance);
        }
    }

    static class LowerX implements Comparator<Entry>, Serializable {
        private static final long serialVersionUID = 1L;
        @Override
        public int compare(Entry e0, Entry e1) {
            return Double.compare(e0.getBoundingBox().minX(), e1.getBoundingBox().minX());
        }
    }

    static class UpperX implements Comparator<Entry>, Serializable {
        private static final long serialVersionUID = 1L;
        @Override
        public int compare(Entry e0, Entry e1) {
            return Double.compare(e1.getBoundingBox().maxX(), e0.getBoundingBox().maxX());
        }
    }

    static class LowerY implements Comparator<Entry>, Serializable {
        private static final long serialVersionUID = 1L;
        @Override
        public int compare(Entry e0, Entry e1) {
            return Double.compare(e0.getBoundingBox().minY(), e1.getBoundingBox().minY());
        }
    }

    static class UpperY implements Comparator<Entry>, Serializable {
        private static final long serialVersionUID = 1L;
        @Override
        public int compare(Entry e0, Entry e1) {
            return Double.compare(e1.getBoundingBox().maxY(), e0.getBoundingBox().maxY());
        }
    }

}
