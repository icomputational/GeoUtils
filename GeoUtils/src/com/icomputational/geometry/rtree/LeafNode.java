package com.icomputational.geometry.rtree;

import java.util.Collection;
import java.util.List;

import com.icomputational.geometry.BoundingBox;
import com.icomputational.geometry.Point;
import com.icomputational.geometry.Shape;

class LeafNode extends Node {
    /**
     * Construct a node from capacity of entries.
     * @param capacity
     */
    public LeafNode(int capacity) {
        super(capacity);
    }

    @Override
    public int level() {
        // it's always 0 for a leaf node
        return 0;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public void search(Point point, List<Shape> result) {
        for (Entry entry : entries) {
            if (entry.getBoundingBox().contains(point)) {
                Shape shape = ((LeafEntry) entry).shape;
                if (shape.contains(point)) {
                    result.add(shape);
                }
            }
        }
    }

    @Override
    public void search(BoundingBox bb, List<Shape> result) {
        for (Entry entry : entries) {
            if (entry.getBoundingBox().overlaps(bb)) {
                Shape shape = ((LeafEntry) entry).shape;
                if (shape.overlaps(bb)) {
                    result.add(shape);
                }
            }
        }
    }

    @Override
    public LeafNode findLeaf(LeafEntry leafEntry) {
        if (entries.indexOf(leafEntry) != -1) {
            return this;
        } else {
            return null;
        }
    }

    @Override
    public boolean add(Entry entry) {
        assert (entry instanceof LeafEntry);
        if (entries.size() < maxEntries) {
            entries.add(entry);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public Node createPartner(Collection<Entry> entries) {
        LeafNode partner = new LeafNode(maxEntries);
        partner.setEntries(entries);
        return partner;
    }
}
