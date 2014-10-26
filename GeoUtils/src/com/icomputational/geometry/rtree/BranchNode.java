package com.icomputational.geometry.rtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.icomputational.geometry.BoundingBox;
import com.icomputational.geometry.Point;
import com.icomputational.geometry.Shape;

class BranchNode extends Node {
    private static final int OVERLAP_COST_ENTRIES = 32;

    /**
     * Create a new entry for a node.
     * @param child
     * @return
     */
    BranchEntry createEntry(Node node) {
        node.setParent(this);
        return new BranchEntry(node);
    }

    private final int level;

    public BranchNode(int maxEntries, int level) {
        super(maxEntries);
        this.level = level;
    }

    @Override
    public int level() {
        return level;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    /**
     * Get entry for specified child node.
     * @param child a child node.
     * @return the entry for the child, null if not found.
     */
    BranchEntry getEntry(Node child) {
        for (Entry entry : entries) {
            BranchEntry be = (BranchEntry) entry;
            if (be.child == child) {
                return be;
            }
        }
        return null;
    }

    @Override
    public void search(Point point, List<Shape> result) {
        for (Entry entry : entries) {
            if (entry.getBoundingBox().contains(point)) {
                Node child = ((BranchEntry) entry).child;
                child.search(point, result);
            }
        }
    }

    @Override
    public void search(BoundingBox bb, List<Shape> result) {
        for (Entry entry : entries) {
            if (entry.getBoundingBox().overlaps(bb)) {
                Node child = ((BranchEntry) entry).child;
                child.search(bb, result);
            }
        }
    }

    @Override
    public LeafNode findLeaf(LeafEntry leafEntry) {
        for (Entry entry : entries) {
            if (entry.getBoundingBox().overlaps(leafEntry.getBoundingBox())) {
                LeafNode node = ((BranchEntry) entry).child.findLeaf(leafEntry);
                if (node != null) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * Adjust entry for specified node.
     * @param node
     * @return
     */
    public boolean adjustEntry(Node node) {
        for (Entry entry : entries) {
            if (((BranchEntry) entry).child == node) {
                ((BranchEntry) entry).adjust();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean add(Entry entry) {
        assert (entry instanceof BranchEntry);
        // always set parent to this node, the parent will be updated if split needed
        // and the entry has been assigned to partner of this node.
    	((BranchEntry) entry).child.setParent(this);
        if (entries.size() < maxEntries) {
            if (entries.add(entry)) {
            	return true;
            }
        }
        return false;
    }

    /**
     * Eliminate a node if it's under full
     * @param node
     * @return true if the child node is found and removed
     */
    boolean eliminate(Node node) {
        Iterator<Entry> itr = entries.iterator();
        while (itr.hasNext()) {
            BranchEntry entry = (BranchEntry) itr.next();
            if (entry.child.equals(node)) {
                itr.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public Node createPartner(Collection<Entry> entries) {
        BranchNode partner = new BranchNode(maxEntries, level);
        partner.setEntries(entries);
        for (Entry entry : entries) {
            ((BranchEntry) entry).child.setParent(partner);
        }
        return partner;
    }

    @Override
    public int size() {
        int size = 0;
        for (Entry entry : entries) {
            size += ((BranchEntry) entry).child.size();
        }
        return size;
    }

    Node getFirstChild() {
        return ((BranchEntry) entries.get(0)).child;
    }

    /**
     * Sort entries by area enlargement for specified bounding box.
     */
    public List<BranchEntry> sortByAreaEnlargement(BoundingBox bb) {
        List<BranchEntry.Enlargement> list = new ArrayList<BranchEntry.Enlargement>(entries.size());
        for (Entry entry : entries) {
            list.add(((BranchEntry) entry).getEnlargement(bb));
        }

        Collections.sort(list, BranchEntry.ENLARGEMENT_COMPARATOR);
        return BranchEntry.toEntries(list);
    }

    /**
     * Sort all entries by overlap enlargement, used by R*-tree
     */
    public List<BranchEntry> sortByOverlapEnlargement(BoundingBox bb) {
        List<BranchEntry.Enlargement> list = new ArrayList<BranchEntry.Enlargement>(entries.size());
        for (Entry entry : entries) {
            list.add(((BranchEntry) entry).getEnlargement(bb));
        }
        Collections.sort(list, BranchEntry.ENLARGEMENT_COMPARATOR);
        for (int i = list.size() - 1; i >= OVERLAP_COST_ENTRIES; i--) {
            list.remove(i);
        }

        for (BranchEntry.Enlargement ae : list) {
            ae.computeOverlapDelta(entries);
        }
        Collections.sort(list, BranchEntry.ENLARGEMENT_COMPARATOR);
        return BranchEntry.toEntries(list);
    }
}
