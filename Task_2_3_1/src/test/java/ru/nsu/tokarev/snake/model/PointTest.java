package ru.nsu.tokarev.snake.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    @Test
    void testEquality() {
        assertEquals(new Point(3, 7), new Point(3, 7));
        assertNotEquals(new Point(3, 7), new Point(7, 3));
    }

    @Test
    void testHashCodeConsistentWithEquals() {
        Point a = new Point(5, 10);
        Point b = new Point(5, 10);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testWrapInsideBounds() {
        Point p = new Point(10, 20);
        assertEquals(p, p.wrap(50, 50));
    }

    @Test
    void testWrapRightEdge() {
        assertEquals(new Point(0, 5), new Point(50, 5).wrap(50, 50));
    }

    @Test
    void testWrapLeftEdge() {
        assertEquals(new Point(49, 5), new Point(-1, 5).wrap(50, 50));
    }

    @Test
    void testWrapBottomEdge() {
        assertEquals(new Point(5, 0), new Point(5, 50).wrap(50, 50));
    }

    @Test
    void testWrapTopEdge() {
        assertEquals(new Point(5, 49), new Point(5, -1).wrap(50, 50));
    }

    @Test
    void testWrapLargeNegative() {
        assertEquals(new Point(48, 48), new Point(-2, -2).wrap(50, 50));
    }
}

