package com.icomputational.geometry.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.icomputational.geometry.Point;

/**
 * A utility for polygon to check if a polygon is a simple polygon.
 */
public class PolygonChecker {
    static class PointComparator implements Comparator<Point>, Serializable {
        private static final long serialVersionUID = 1L;

        @Override
        public int compare(Point p1, Point p2) {
            if (p1.x() < p2.x()) {
                return -1;
            } else if (p1.x() > p2.x()) {
                return 1;
            }

            if (p1.y() < p2.y()) {
                return -1;
            } else if (p1.y() > p2.y()) {
                return 1;
            }

            return 0;
        }
    }

    static class SegComparator implements Comparator<Segment>, Serializable {
        private static final long serialVersionUID = 1L;

        private final double x;

        SegComparator(double x) {
            this.x = x;
        }

        @Override
        public int compare(Segment s1, Segment s2) {
            return Double.compare(getY(s1, x), getY(s2, x));
        }

        private double getY(Segment s, double x) {
            double y = s.getY(x);
            if (Double.isNaN(y) || Double.isInfinite(y)) {
                return s.left().y();
            }

            return y;
        }
    }

    /**
     * Check if the specified vertices represents a simple polygon in computational geometry.
     * A polygon with any two edges interacts, or non-neighbor edges joined are treated as complex polygon.
     * The sweep line algorithm presented by Shamos and Hoey are used in this implementation.
     *
     * @param vertices a list of vertices.
     * @return true if the polygon is a simple polygon in computational geometry.
     * @see <a href="http://en.wikipedia.org/wiki/Sweep_line_algorithm">Sweep line algorithm</a>
     */
    public boolean isSimple(List<Point> vertices) {
        if (vertices == null || vertices.size() < 3) {
            return false;
        }

        List<Segment> segments = new ArrayList<Segment>(vertices.size());
        Point last = vertices.get(vertices.size() - 1);
        for (int i = 0; i < vertices.size(); i++) {
            Point current = vertices.get(i);
            if (current.equals(last)) {
                return false;
            }
            segments.add(new Segment(last, current));
            last = current;
        }

        Segment prev = segments.get(0);
        Map<Point, Segment[]> endPoints = new TreeMap<Point, Segment[]>(new PointComparator());
        for (int i = vertices.size() - 1; i >= 0; i--) {
            Segment next = segments.get(i);
            Segment[] edges = new Segment[] { prev, next };
            Segment[] existing = endPoints.put(vertices.get(i), edges);
            // duplicated point found
            if (existing != null) {
                return false;
            }
            prev = next;
        }

        List<Segment> checkingList = new ArrayList<Segment>();
        for (Map.Entry<Point, Segment[]> endPoint : endPoints.entrySet()) {
            Point vertex = endPoint.getKey();
            Segment[] edges = endPoint.getValue();
            if (!check(vertex, edges[0], checkingList)) {
                return false;
            }
            if (!check(vertex, edges[1], checkingList)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if an end point against the specified checking list.
     * @return true if the end point won't break the simplicity.
     */
    private boolean check(Point vertex, Segment segment, List<Segment> checkingList) {
        if (vertex.equals(segment.left())) {
            int index = insert(checkingList, segment);
            assert (index >= 0);
            Segment sa = above(checkingList, index);
            Segment sb = below(checkingList, index);
            if (intersect(sa, segment)) {
                return false;
            } else if (intersect(sb, segment)) {
                return false;
            }
        } else {
            int index = checkingList.indexOf(segment);
            if (index < 0) {
                throw new IllegalStateException("missing segment " + segment + " for " + vertex);
            }
            Segment sa = above(checkingList, index);
            Segment sb = below(checkingList, index);
            if (intersect(sa, sb)) {
                return false;
            }
            checkingList.remove(index);
        }
        return true;
    }

    private int insert(List<Segment> segments, Segment segment) {
        int index = Collections.binarySearch(segments, segment, new SegComparator(segment.left().x()));
        if (index < 0) {
            index = -1 - index;
        }
        segments.add(index, segment);
        return index;
    }

    private Segment above(List<Segment> segments, int index) {
        index++;
        if (index >= segments.size()) {
            return null;
        }

        return segments.get(index);
    }

    private Segment below(List<Segment> segments, int index) {
        index--;
        if (index < 0) {
            return null;
        }

        return segments.get(index);
    }

    private boolean intersect(Segment s1, Segment s2) {
        if (s1 == null || s2 == null) {
            return false;
        }

        if (s1.left() == s2.left() || s1.left() == s2.right() || s1.right() == s2.left() || s1.right() == s2.right()) {
            return false;
        }

        Point p = s1.intersect(s2);
        return (p != null);
    }
}
