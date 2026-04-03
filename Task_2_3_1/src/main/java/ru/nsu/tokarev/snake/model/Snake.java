package ru.nsu.tokarev.snake.model;

import lombok.Getter;
import java.util.ArrayDeque;
import java.util.Deque;

public class Snake {
    @Getter
    private final Deque<Point> body = new ArrayDeque<>();
    @Getter
    private Direction direction;
    private Direction lastMovedDirection;
    private int pendingGrowth = 0;

    public Snake(int startX, int startY) {
        this.direction = Direction.RIGHT;
        this.lastMovedDirection = Direction.RIGHT;

        body.addFirst(new Point(startX, startY));
        body.addLast(new Point(startX - 1, startY));
        body.addLast(new Point(startX - 2, startY));
    }

    public void setDirection(Direction newDir) {
        if (newDir != lastMovedDirection.opposite()) {
            direction = newDir;
        }
    }

    public void addGrowth(int amount) {
        pendingGrowth += amount;
    }

    public void shrink(int amount) {
        for (int i = 0; i < amount && body.size() > 2; i++) {
            body.removeLast();
        }
    }

    public Point move(int cols, int rows) {
        lastMovedDirection = direction;
        Point head = body.peekFirst();
        assert head != null;
        int nx = head.x + dx();
        int ny = head.y + dy();
        Point newHead = new Point(nx, ny).wrap(cols, rows);
        body.addFirst(newHead);
        if (pendingGrowth > 0) {
            pendingGrowth--;
            return null;
        }
        return body.removeLast();
    }

    public Point getHead() {
        return body.peekFirst();
    }

    public Point getTail() {
        return body.peekLast();
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

