package com.icomputational.geometry.rtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.icomputational.geometry.BoundingBox;
import com.icomputational.geometry.Point;
import com.icomputational.geometry.Shape;
import com.icomputational.geometry.util.BoundingBoxBuilder;

abstract class Node {


    protected BranchNode parent;

    protected List<Entry> entries;
    protected final int maxEntries;

    protected Node(int maxEntries) {
        this.maxEntries = maxEntries;
        entries = new ArrayList<Entry>(maxEntries);
    }

    /**
     * Return true if this node is a leaf node.
     * @return true if this node is a leaf node.
     */
    public abstract boolean isLeaf();

    /**
     * Set parent for this node.
     * @param parent the new parent node.
     */
    public void setParent(BranchNode parent) {
        this.parent = parent;
    }

    /**
     * Get parent node of this node.
     * @return parent node, null for root node.
     */
    public BranchNode getParent() {
        return parent;
    }

    /**
     * Set entries of this node.
     */
    protected void setEntries(Collection<Entry> entries) {
        this.entries.clear();
        this.entries.addAll(entries);
    }

    /**
     * Check if this node is a root node.
     * @return true if this node has no parent node.
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * Get a bounding box that covers all entries of this node.
     * @return a bounding box, null if no entry found for this node.
     */
    public BoundingBox getBoundingBox() {
        if (entries.isEmpty()) {
            return null;
        }

        Iterator<Entry> itr = entries.iterator();
        BoundingBoxBuilder builder = new BoundingBoxBuilder(itr.next().getBoundingBox());
        while (itr.hasNext()) {
            Entry entry = itr.next();
            builder.add(entry.getBoundingBox());
        }

        return builder.toBoundingBox();
    }

    /**
     * Try to install an entry to this node.
     * @param entry a new entry
     * @return true if the entry is added successfully.
     */
    public abstract boolean add(Entry entry);

    public abstract void search(Point point, List<Shape> result);

    public abstract void search(BoundingBox bb, List<Shape> result);

    /**
     * Find a leaf node contains specified entry.
     */
    public abstract LeafNode findLeaf(LeafEntry entry);

    public abstract Node createPartner(Collection<Entry> entries);

    /**
     * Calculate the number of leaf entries in this sub tree.
     */
    public abstract int size();

    /**
     * Returns the level of this branch node.
     * A leaf node is treated as level 0, and a branch node is always have a positive level.
     */
    public abstract int level();

    /**
     * Remove an entry from entries of this node.
     * @param entry the entry to be removed.
     * @return true if the entry is removed successfully.
     */
    public boolean delete(Entry entry) {
        return entries.remove(entry);
    }

    /**
     * Get all entries of this node plus the new entry.
     * @return a list of entries.
     */
    List<Entry> getEntries(Entry newEntry) {
        List<Entry> result = new ArrayList<Entry>(maxEntries + 1);
        result.addAll(this.entries);
        result.add(newEntry);
        return result;
    }
}
