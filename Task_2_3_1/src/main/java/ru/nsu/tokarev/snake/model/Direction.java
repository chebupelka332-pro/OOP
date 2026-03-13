package ru.nsu.tokarev.snake.model;

public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    public Direction opposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }
}

