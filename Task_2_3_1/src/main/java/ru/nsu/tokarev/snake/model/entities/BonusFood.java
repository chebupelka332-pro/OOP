package ru.nsu.tokarev.snake.model.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.nsu.tokarev.snake.model.GameModel;
import ru.nsu.tokarev.snake.model.Point;
import ru.nsu.tokarev.snake.model.Snake;

public class BonusFood implements Food {
    private static final Color COLOR = Color.web("#ff4757");
    private final Point position;

    public BonusFood(Point position) {
        this.position = position;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public void applyEffect(GameModel model, Snake snake) {
        snake.addGrowth(3);
    }

    @Override
    public void render(GraphicsContext gc, int cellSize) {
        gc.setFill(COLOR);
        double margin = cellSize * 0.15;
        double size = cellSize - 2 * margin;
        gc.fillOval(position.x * cellSize + margin, position.y * cellSize + margin, size, size);
    }
}
