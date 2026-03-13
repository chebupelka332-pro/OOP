package ru.nsu.tokarev.snake.model;

import java.util.*;

public class GameModel {

    public enum State { RUNNING, GAME_OVER }

    private final int cols;
    private final int rows;
    private final int maxFoodCount;

    private final Snake snake;
    private final Set<Point> food = new HashSet<>();
    private final Set<Point> obstacles = new HashSet<>();
    private State state = State.RUNNING;

    private final Random random = new Random();

    public GameModel(int cols, int rows, int maxFoodCount, int obstacleCount) {
        this.cols = cols;
        this.rows = rows;
        this.maxFoodCount = maxFoodCount;

        snake = new Snake(cols / 2, rows / 2, cols, rows);
        placeObstacles(obstacleCount);
        refillFood();
    }

    public boolean tick() {
        if (state == State.GAME_OVER) return false;

        Point head = snake.getHead();
        int nx = head.x + dx(snake.getDirection());
        int ny = head.y + dy(snake.getDirection());
        Point next = new Point(nx, ny).wrap(cols, rows);

        // Hit obstacle
        if (obstacles.contains(next)) {
            state = State.GAME_OVER;
            return false;
        }

        // Self-collision
        boolean eating = food.contains(next);
        if (!eating && snake.bodyContains(next)) {
            Point tail = snake.getTail();
            if (!next.equals(tail)) {
                state = State.GAME_OVER;
                return false;
            }
        } else if (eating && snake.bodyContains(next)) {
            state = State.GAME_OVER;
            return false;
        }

        snake.move(eating);

        if (eating) {
            food.remove(next);
            refillFood();
        }

        return true;
    }

    public void setDirection(Direction d) {
        snake.setDirection(d);
    }

    private void placeObstacles(int count) {
        int placed = 0;
        int attempts = 0;
        while (placed < count && attempts < count * 100) {
            attempts++;
            Point p = randomPoint();
            if (!snake.contains(p) && !obstacles.contains(p)) {
                // Keep a clear zone around snake start
                if (Math.abs(p.x - cols / 2) > 5 || Math.abs(p.y - rows / 2) > 5) {
                    obstacles.add(p);
                    placed++;
                }
            }
        }
    }

    private void refillFood() {
        int attempts = 0;
        while (food.size() < maxFoodCount && attempts < 1000) {
            attempts++;
            Point p = randomPoint();
            if (!snake.contains(p) && !obstacles.contains(p) && !food.contains(p)) {
                food.add(p);
            }
        }
    }

    private Point randomPoint() {
        return new Point(random.nextInt(cols), random.nextInt(rows));
    }

    private static int dx(Direction d) {
        return switch (d) { case LEFT -> -1; case RIGHT -> 1; default -> 0; };
    }

    private static int dy(Direction d) {
        return switch (d) { case UP -> -1; case DOWN -> 1; default -> 0; };
    }

    public Snake getSnake() { return snake; }
    public Set<Point> getFood() { return Collections.unmodifiableSet(food); }
    public Set<Point> getObstacles() { return Collections.unmodifiableSet(obstacles); }
    public State getState() { return state; }
    public int getCols() { return cols; }
    public int getRows() { return rows; }
}
