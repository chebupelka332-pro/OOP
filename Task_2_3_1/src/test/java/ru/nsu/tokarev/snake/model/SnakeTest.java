package ru.nsu.tokarev.snake.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SnakeTest {

    private static final int COLS = 50;
    private static final int ROWS = 50;

    private Snake snake;

    @BeforeEach
    void setUp() {
        // Snake starts at (25, 25), length 3, facing RIGHT: (25,25),(24,25),(23,25)
        snake = new Snake(25, 25, COLS, ROWS);
    }

    @Test
    void testInitialLength() {
        assertEquals(3, snake.getLength());
    }

    @Test
    void testInitialDirection() {
        assertEquals(Direction.RIGHT, snake.getDirection());
    }

    @Test
    void testInitialHeadPosition() {
        assertEquals(new Point(25, 25), snake.getHead());
    }

    @Test
    void testMoveWithoutGrowing() {
        Point oldTail = snake.getTail();
        Point removed = snake.move(false);

        assertEquals(new Point(26, 25), snake.getHead());
        assertEquals(3, snake.getLength());
        assertEquals(oldTail, removed); // removed tile must be the old tail
    }

    @Test
    void testMoveWithGrowing() {
        snake.move(true);

        assertEquals(new Point(26, 25), snake.getHead());
        assertEquals(4, snake.getLength());
    }

    @Test
    void testMoveGrowReturnsNull() {
        assertNull(snake.move(true));
    }

    @Test
    void testSetDirection() {
        snake.setDirection(Direction.UP);
        assertEquals(Direction.UP, snake.getDirection());
    }

    @Test
    void testIgnoreReverseDirection() {
        // Snake faces RIGHT — setting LEFT must be ignored
        snake.setDirection(Direction.LEFT);
        assertEquals(Direction.RIGHT, snake.getDirection());
    }

    @Test
    void testWrapAroundRightEdge() {
        Snake s = new Snake(49, 25, COLS, ROWS);
        s.move(false);
        assertEquals(new Point(0, 25), s.getHead());
    }

    @Test
    void testWrapAroundLeftEdge() {
        // Snake starts facing RIGHT, so we can't immediately go LEFT (reverse ignored).
        // Turn UP first, then LEFT, then walk to x=0 and verify wrap to x=49.
        Snake s = new Snake(3, 25, COLS, ROWS);
        s.setDirection(Direction.UP);
        s.move(false); // now safe to go LEFT
        s.setDirection(Direction.LEFT);
        // Walk until head reaches x=0
        while (s.getHead().x > 0) s.move(false);
        assertEquals(new Point(0, 24), s.getHead());
        s.move(false); // cross the left border
        assertEquals(new Point(49, 24), s.getHead());
    }

    @Test
    void testWrapAroundTopEdge() {
        Snake s = new Snake(25, 0, COLS, ROWS);
        s.setDirection(Direction.UP);
        s.move(false);
        assertEquals(new Point(25, 49), s.getHead());
    }

    @Test
    void testWrapAroundBottomEdge() {
        Snake s = new Snake(25, 49, COLS, ROWS);
        s.setDirection(Direction.DOWN);
        s.move(false);
        assertEquals(new Point(25, 0), s.getHead());
    }

    @Test
    void testBodyContainsExcludesHead() {
        // Head is (25,25) — bodyContains must skip it
        assertFalse(snake.bodyContains(new Point(25, 25)));
    }

    @Test
    void testBodyContainsBodySegment() {
        // (24,25) is the second segment
        assertTrue(snake.bodyContains(new Point(24, 25)));
    }

    @Test
    void testContainsHead() {
        assertTrue(snake.contains(new Point(25, 25)));
    }

    @Test
    void testContainsNotOccupied() {
        assertFalse(snake.contains(new Point(0, 0)));
    }

    @Test
    void testGetBodyOrder() {
        List<Point> body = List.copyOf(snake.getBody());
        assertEquals(new Point(25, 25), body.get(0)); // head
        assertEquals(new Point(23, 25), body.get(2)); // tail
    }
}
