package ru.nsu.tokarev.snake.model;

import ru.nsu.tokarev.snake.model.entities.*;
import lombok.Getter;
import java.util.*;

public class GameModel {

    public enum State { RUNNING, GAME_OVER, WON }

    @Getter
    private final int cols;
    @Getter
    private final int rows;
    private final int maxFoodCount;

    @Getter
    private final Snake snake;
    private final Map<Point, Food> foods = new HashMap<>();
    private final EntityGenerator<Food> foodGenerator = new RandomFoodFactory();
    private final Map<Point, Obstacle> obstacles = new HashMap<>();
    private final EntityGenerator<Obstacle> obstacleGenerator = new ObstacleGenerator();
    @Getter
    private State state = State.RUNNING;

    private final Random random = new Random();
    @Getter
    private final WinCondition winCondition;

    public GameModel(int cols, int rows, int maxFoodCount, int obstacleCount, WinCondition winCondition) {
        this.cols = cols;
        this.rows = rows;
        this.maxFoodCount = maxFoodCount;
        this.winCondition = winCondition;

        snake = new Snake(cols / 2, rows / 2);
        placeObstacles(obstacleCount);
        refillFood();
    }

    public boolean tick() {
        if (state == State.GAME_OVER || state == State.WON) return false;

        Point head = snake.getHead();
        int nx = head.x + dx(snake.getDirection());
        int ny = head.y + dy(snake.getDirection());
        Point next = new Point(nx, ny).wrap(cols, rows);

        if (obstacles.containsKey(next)) {
            state = State.GAME_OVER;
            return false;
        }

        Food eatingFood = foods.get(next);
        boolean eating = (eatingFood != null);

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

        if (eating) {
            eatingFood.applyEffect(this, snake);
            foods.remove(next);
        }

        snake.move(cols, rows);

        if (eating) {
            refillFood();
        }

        if (winCondition.isMet(this)) {
            state = State.WON;
            return false;
        }

        return true;
    }

    public void setDirection(Direction d) {
        snake.setDirection(d);
    }

    private void placeObstacles(int count) {
        for (int i = 0; i < count; i++) {
            List<Point> emptyCells = getEmptyCells();

            // Keep a clear zone around snake start
            emptyCells.removeIf(p -> Math.abs(p.x - cols / 2) <= 5 && Math.abs(p.y - rows / 2) <= 5);
            
            if (emptyCells.isEmpty()) break;
            
            Obstacle obs = obstacleGenerator.generate(emptyCells);
            if (obs != null) {
                obstacles.put(obs.getPosition(), obs);
            }
        }
    }

    private List<Point> getEmptyCells() {
        List<Point> empty = new ArrayList<>();
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Point p = new Point(x, y);
                if (!snake.contains(p) && !obstacles.containsKey(p) && !foods.containsKey(p)) {
                    empty.add(p);
                }
            }
        }
        return empty;
    }

    private void refillFood() {
        while (foods.size() < maxFoodCount) {
            List<Point> emptyCells = getEmptyCells();
            if (emptyCells.isEmpty()) break;
            Food newFood = foodGenerator.generate(emptyCells);
            if (newFood != null) {
                foods.put(newFood.getPosition(), newFood);
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

    public Collection<Food> getFoods() {
        return Collections.unmodifiableCollection(foods.values());
    }

    public Collection<Obstacle> getObstacles() {
        return Collections.unmodifiableCollection(obstacles.values());
    }
}
