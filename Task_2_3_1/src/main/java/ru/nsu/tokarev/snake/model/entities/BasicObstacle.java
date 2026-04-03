package ru.nsu.tokarev.snake.model.entities;

import javafx.scene.canvas.GraphicsContext;
import ru.nsu.tokarev.snake.model.Point;
import ru.nsu.tokarev.snake.model.GameModel;
import ru.nsu.tokarev.snake.model.Snake;

import javafx.scene.paint.Color;

public class BasicObstacle implements Obstacle {
    private static final Color OBSTACLE_COLOR = Color.web("#e94560");
    private final Point position;

    public BasicObstacle(Point position) {
        this.position = position;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public void render(GraphicsContext gc, int cellSize) {
        gc.setFill(OBSTACLE_COLOR);
        gc.fillRoundRect(position.x * cellSize + 1, position.y * cellSize + 1, cellSize - 2, cellSize - 2, 3, 3);
    }
}