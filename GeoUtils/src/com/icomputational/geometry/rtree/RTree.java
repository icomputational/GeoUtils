package com.icomputational.geometry.rtree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.icomputational.geometry.BoundingBox;
import com.icomputational.geometry.Point;
import com.icomputational.geometry.Shape;
import com.icomputational.geometry.util.BoundingBoxBuilder;

/**
 * The {@link RTree} is an implementation of R-tree.
 * @see <a href="http://www-db.deis.unibo.it/courses/SI-LS/papers/Gut84.pdf">R-Tree</a>
 */
public class RTree {
    protected Node rootNode;
    protected final int maxEntries;
    protected final int minEntries;

    /**
     * Construct a R*-tree entries limits.
     * @param M maximum number of entries that will fit in one node, should be greater than 1.
     * @param m minimum number of entries in a node, must be less than M/2.
     */
    public RTree(int M, int m) {
        if (M <= 1) {
            throw new IllegalArgumentException("M must greater than 1");
        } else if (m <= 0 || m > M / 2) {
            throw new IllegalArgumentException("m must greater than 0 and less than M/2");
        }
        this.maxEntries = M;
        this.minEntries = m;

        rootNode = createLeafNode();
    }

    /**
     * Create a new leaf node.
     */
    protected LeafNode createLeafNode() {
        return new LeafNode(maxEntries);
    }

    /**
     * Create a new root node.
     */
    protected BranchNode createRootNode(int level, Node... nodes) {
        BranchNode parent = new BranchNode(maxEntries, level);
        for (Node node : nodes) {
            BranchEntry entry = parent.createEntry(node);
            parent.add(entry);
        }
        return parent;
    }

    /**
     * Insert a shape to this tree.
     * @param shape a shape instance.
     */
    public void insert(Shape shape) {
        LeafEntry entry = new LeafEntry(shape);
        insert(entry, 0);
    }

    /**
     * Insert an entry to this tree.
     * @param entry an entry to insert
     * @param level desired level of node.
     */
    protected void insert(Entry entry, int level) {
        Node node = chooseSubTree(rootNode, entry.getBoundingBox(), level);
        if (node.add(entry)) {
            adjustTree(node);
        } else {
            Node partner = splitNode(node, entry);
            adjustTree(node, partner);
        }
    }

    /**
     * Choose a subtree for specified bounding box and level.
     * @param bb the bounding box of an entry.
     * @param level the desired level of the returning node.
     * @return a node for specified bounding box in specified level.
     */
    protected Node chooseSubTree(Node node, BoundingBox bb, int level) {
        if (node.level() == level) {
            return node;
        }

        List<BranchEntry> entries = ((BranchNode) node).sortByAreaEnlargement(bb);
        return chooseSubTree(entries.get(0).child, bb, level);
    }

    /**
     * Delete a shape from this tree.
     * @param shape the shape to be deleted.
     * @return true if the shape has been deleted successfully.
     */
    public boolean delete(Shape shape) {
        LeafEntry entry = new LeafEntry(shape);
        LeafNode leaf = rootNode.findLeaf(entry);
        if (leaf == null) {
            return false;
        }

        leaf.delete(entry);
        condenseTree(leaf);

        // shorten tree
        if (rootNode.entries.size() < 2 && rootNode instanceof BranchNode) {
            rootNode = ((BranchNode) rootNode).getFirstChild();
            rootNode.setParent(null);
        }
        return true;
    }

    /**
     * Calculate height of this tree.
     */
    public int height() {
        return rootNode.level() + 1;
    }

    /**
     * Calculate total size of this tree.
     * @return total number of leaves
     */
    public int size() {
        return rootNode.size();
    }

    protected void condenseTree(Node node) {
        List<Node> eliminated = new ArrayList<Node>();
        BranchNode parent = node.getParent();
        while (parent != null) {
            if (node.entries.size() < minEntries) {
                boolean removed = parent.eliminate(node);
                assert (removed);
                eliminated.add(node);
            }

            node = parent;
            parent = node.getParent();
        }

        // re-insert all entries of eliminated nodes to their original level
        for (Node e : eliminated) {
            for (Entry entry : e.entries) {
                insert(entry, e.level());
            }
        }
    }

    /**
     * Search for shapes that contains specified point.
     * @param point a point to be tested
     * @return a list of shapes contains specified point.
     */
    public List<Shape> search(Point point) {
        List<Shape> result = new ArrayList<Shape>();
        rootNode.search(point, result);
        return result;
    }

    /**
     * Search for shapes that overlaps specified bounding box.
     * @param bb a bounding box to be tested.
     * @return a list of shapes overlaps specified bounding box.
     */
    public List<Shape> search(BoundingBox bb) {
        List<Shape> result = new ArrayList<Shape>();
        rootNode.search(bb, result);
        return result;
    }

    protected void adjustTree(Node node, Node partner) {
        assert (partner != null);
        BranchNode parent = node.getParent();
        if (parent == null) {
            // node is root, grow tree taller, create a new root
            rootNode = createRootNode(node.level() + 1, node, partner);
            return;
        }

        parent.adjustEntry(node);
        BranchEntry entry = parent.createEntry(partner);
        if (parent.add(entry)) {
            adjustTree(parent);
        } else {
            Node pp = splitNode(parent, entry); // parent partner
            adjustTree(parent, pp);
        }
    }

    /**
     * Adjust tree when no split needed.
     */
    protected void adjustTree(Node node) {
        BranchNode parent = node.getParent();
        if (parent == null) {
            // node is root
            return;
        }

        parent.adjustEntry(node);
        adjustTree(parent);
    }

    /**
     * Split a node for a new entry.
     * @param node the node to be split.
     * @param newEntry the new entry.
     * @return the new node.
     */
    protected Node splitNode(Node node, Entry newEntry) {
        List<Entry> entries = node.getEntries(newEntry);

        Entry[] seeds = pickSeeds(entries);
        EntryGroup group1 = new EntryGroup(seeds[0], maxEntries);
        EntryGroup group2 = new EntryGroup(seeds[1], maxEntries);

        entries.remove(seeds[0]);
        entries.remove(seeds[1]);

        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            if (group1.entries.size() <= minEntries - (entries.size() - i)) {
                group1.add(entry);
            } else if (group2.entries.size() <= minEntries - (entries.size() - i)) {
                group2.add(entry);
            } else {
                double d1 = group1.getDelta(entry.getBoundingBox());
                double d2 = group2.getDelta(entry.getBoundingBox());
                if (d1 < d2) {
                    group1.add(entry);
                } else if (d1 > d2) {
                    group2.add(entry);
                } else {
                    double c1 = group1.area();
                    double c2 = group2.area();
                    if (c1 <= c2) {
                        group1.add(entry);
                    } else {
                        group2.add(entry);
                    }
                }
            }
        }

        node.setEntries(group1.entries);
        return node.createPartner(group2.entries);
    }

    /**
     * Pick seeds to split node.
     * This is an implementation of linear-cost algorithm described in Gut's paper.
     * @param entries a list of entries contains all entries of original node and newly added entry.
     * @return two seed entries.
     */
    private Entry[] pickSeeds(List<Entry> entries) {
        Iterator<Entry> itr = entries.iterator();

        Entry first = itr.next();
        Entry lowX = first;
        Entry highX = first;
        Entry lowY = first;
        Entry highY = first;

        BoundingBoxBuilder builder = new BoundingBoxBuilder(first.getBoundingBox());

        while (itr.hasNext()) {
            Entry entry = itr.next();
            BoundingBox bb = entry.getBoundingBox();
            if (lowX.getBoundingBox().maxX() < bb.maxX()) {
                lowX = entry;
            } else if (highX.getBoundingBox().minX() > bb.minX()) {
                highX = entry;
            }
            if (lowY.getBoundingBox().maxY() < bb.maxY()) {
                lowY = entry;
            } else if (highY.getBoundingBox().minY() > bb.minY()) {
                highY = entry;
            }

            builder.add(bb);
        }

        assert (lowX != highX);
        assert (lowY != highY);

        // normalize the separation value
        double separationX = (highX.getBoundingBox().minX() - lowX.getBoundingBox().maxX()) / builder.width();
        double separationY = (highY.getBoundingBox().minY() - lowY.getBoundingBox().maxY()) / builder.height();
        if (separationX > separationY) {
            return new Entry[] { lowX, highX };
        } else {
            return new Entry[] { lowY, highY };
        }
    }
}
