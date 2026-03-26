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
        snake = new Snake(25, 25);
    }

    @Test
    void testInitialState() {
        assertEquals(3, snake.getLength());
        assertEquals(Direction.RIGHT, snake.getDirection());
        assertEquals(new Point(25, 25), snake.getHead());
    }

    @Test
    void testMoveSimple() {
        Point headBefore = snake.getHead();
        Point removed = snake.move(COLS, ROWS);

        assertNotNull(removed);
        assertEquals(3, snake.getLength());

        Point headAfter = snake.getHead();
        assertEquals(headBefore.x + 1, headAfter.x); // Should move RIGHT initially
        assertEquals(headBefore.y, headAfter.y);
    }

    @Test
    void testGrow() {
        snake.addGrowth(1);
        snake.move(COLS, ROWS);
        assertEquals(4, snake.getLength());
        // Body should include new head and keep tail
    }

    @Test
    void testSetDirection() {
        snake.setDirection(Direction.DOWN);
        assertEquals(Direction.DOWN, snake.getDirection());

        snake.addGrowth(1);
        snake.move(COLS, ROWS); // Move down, locks in the DOWN move

        // Attempting to reverse immediately should be ignored
        snake.setDirection(Direction.UP);
        assertEquals(Direction.DOWN, snake.getDirection()); // Remains down
    }

    @Test
    void testDoubleSetDirection() {
        snake.setDirection(Direction.UP);
        snake.setDirection(Direction.LEFT);
        // It should drop LEFT and keep UP, since UP hasn't been moved yet
        // However my implementation respects `lastMovedDirection`, which is RIGHT
        // Setting UP is valid, setting LEFT is valid (LEFT is opposite of RIGHT, so it will ignore left)
        assertEquals(Direction.UP, snake.getDirection()); // LEFT is ignored as it is opposite to lastMovedDirection
    }

    @Test
    void testWrapRight() {
        Snake s = new Snake(49, 25);
        s.move(COLS, ROWS);
        assertEquals(0, s.getHead().x, "Should wrap around to x=0");
    }

    @Test
    void testWrapLeft() {
        Snake s = new Snake(3, 25);
        s.setDirection(Direction.UP);
        s.move(COLS, ROWS); // now safe to go UP

        s.setDirection(Direction.LEFT);
        while (s.getHead().x > 0) s.move(COLS, ROWS);
        // currently at x=0
        s.move(COLS, ROWS); // cross the left border
        assertEquals(49, s.getHead().x, "Should wrap around to x=COLS-1");
    }

    @Test
    void testWrapUp() {
        Snake s = new Snake(25, 0);
        s.setDirection(Direction.UP);
        s.move(COLS, ROWS);
        assertEquals(49, s.getHead().y, "Should wrap around to y=ROWS-1");
    }

    @Test
    void testWrapDown() {
        Snake s = new Snake(25, 49);
        s.setDirection(Direction.DOWN);
        s.move(COLS, ROWS);
        assertEquals(0, s.getHead().y, "Should wrap around to y=0");
    }
}
