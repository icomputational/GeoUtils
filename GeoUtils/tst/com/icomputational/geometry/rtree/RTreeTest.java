package com.icomputational.geometry.rtree;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.icomputational.geometry.BoundingBox;
import com.icomputational.geometry.Point;
import com.icomputational.geometry.Shape;


public class RTreeTest {

    @Test
    public void testSimple() {
        RTree tree = new RTree(50, 2);
        Rectangle rect1 = new Rectangle(0, 0, 10, 10);
        Rectangle rect2 = new Rectangle(-1, -3, 3, 8);
        tree.insert(rect1);
        tree.insert(rect1);
        tree.insert(rect2);
        tree.insert(rect2);
        for (int i = 0; i < 100; i++) {
            tree.insert(new Rectangle(i * 5, i * 10, i * 5 + 3, i * 10 + 10));
        }
        List<Shape> shapes = tree.search(new Point(1, 9));
        assertEquals(3, shapes.size());
        assertEquals(rect1, shapes.get(0));
        System.out.println("Tree height: " + tree.height() + ", size: " + tree.size());
    }

    @Test
    public void testDelete() {
        RTree tree = new RTree(50, 2);
        Rectangle rect1 = new Rectangle(0, 0, 10, 10);
        Rectangle rect2 = new Rectangle(-1, -3, 3, 8);
        tree.insert(rect1);
        tree.insert(rect2);
        for (int i = 0; i < 100; i++) {
            tree.insert(new Rectangle(i * 5, i * 10, i * 5 + 3, i * 10 + 10));
        }
        assertTrue(tree.delete(rect1));
        assertTrue(tree.delete(rect2));
        System.out.println("Tree height: " + tree.height() + ", size: " + tree.size());

    }
}
