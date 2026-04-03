package ru.nsu.tokarev.snake.model.entities;

import java.util.List;
import ru.nsu.tokarev.snake.model.Point;
import ru.nsu.tokarev.snake.model.GameModel;
import ru.nsu.tokarev.snake.model.Snake;

import java.util.Random;

public class ObstacleGenerator implements EntityGenerator<Obstacle> {
    private final Random random = new Random();

    @Override
    public Obstacle generate(List<Point> emptyCells) {
        if (emptyCells.isEmpty()) return null;
        Point p = emptyCells.get(random.nextInt(emptyCells.size()));
        return new BasicObstacle(p);
    }
}