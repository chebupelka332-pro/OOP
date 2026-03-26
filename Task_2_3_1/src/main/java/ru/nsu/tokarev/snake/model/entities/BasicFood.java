package ru.nsu.tokarev.snake.model.entities;

import javafx.scene.canvas.GraphicsContext;
import ru.nsu.tokarev.snake.model.Point;
import ru.nsu.tokarev.snake.model.GameModel;
import ru.nsu.tokarev.snake.model.Snake;

import javafx.scene.paint.Color;

public class BasicFood implements Food {
    private static final Color FOOD_COLOR = Color.web("#f5a623");
    private final Point position;

    public BasicFood(Point position) {
        this.position = position;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public void applyEffect(GameModel model, Snake snake) {
        snake.addGrowth(1);
    }

    @Override
    public void render(GraphicsContext gc, int cellSize) {
        gc.setFill(FOOD_COLOR);
        double margin = cellSize * 0.2;
        double size = cellSize - 2 * margin;
        gc.fillOval(position.x * cellSize + margin, position.y * cellSize + margin, size, size);
    }
}