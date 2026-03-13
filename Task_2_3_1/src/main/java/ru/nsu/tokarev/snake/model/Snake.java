package ru.nsu.tokarev.snake.model;

import java.util.ArrayDeque;
import java.util.Deque;

public class Snake {
    private final Deque<Point> body = new ArrayDeque<>();
    private Direction direction;
    private final int cols;
    private final int rows;

    public Snake(int startX, int startY, int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        this.direction = Direction.RIGHT;

        body.addFirst(new Point(startX, startY));
        body.addLast(new Point(startX - 1, startY));
        body.addLast(new Point(startX - 2, startY));
    }

    public void setDirection(Direction newDir) {
        if (newDir != direction.opposite()) {
            direction = newDir;
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public Point move(boolean grow) {
        Point head = body.peekFirst();
        assert head != null;
        int nx = head.x + dx();
        int ny = head.y + dy();
        Point newHead = new Point(nx, ny).wrap(cols, rows);
        body.addFirst(newHead);
        if (!grow) {
            return body.removeLast();
        }
        return null;
    }

    public Point getHead() {
        return body.peekFirst();
    }

    public Point getTail() {
        return body.peekLast();
    }

    public Deque<Point> getBody() {
        return body;
    }

    public int getLength() {
        return body.size();
    }

    public boolean bodyContains(Point p) {
        int skip = 0;
        for (Point segment : body) {
            if (skip++ == 0) continue; // skip head
            if (segment.equals(p)) return true;
        }
        return false;
    }

    /** Check if any part (including head) contains point. */
    public boolean contains(Point p) {
        return body.contains(p);
    }

    private int dx() {
        return switch (direction) {
            case LEFT -> -1;
            case RIGHT -> 1;
            default -> 0;
        };
    }

    private int dy() {
        return switch (direction) {
            case UP -> -1;
            case DOWN -> 1;
            default -> 0;
        };
    }
}

