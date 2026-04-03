package ru.nsu.tokarev.snake.model.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.nsu.tokarev.snake.model.GameModel;
import ru.nsu.tokarev.snake.model.Point;
import ru.nsu.tokarev.snake.model.Snake;

public class PoisonFood implements Food {
    private static final Color COLOR = Color.web("#9b59b6");
    private final Point position;

    public PoisonFood(Point position) {
        this.position = position;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public void applyEffect(GameModel model, Snake snake) {
        snake.shrink(1); // Отбирает 1 сегмент (но не убивает, если 1)
    }

    @Override
    public void render(GraphicsContext gc, int cellSize) {
        gc.setFill(Color.web("#80cbc4")); // Ярко-голубой для яда
        // Можно нарисовать как ромб
        double cx = position.x * cellSize + cellSize / 2.0;
        double cy = position.y * cellSize + cellSize / 2.0;
        double r = cellSize * 0.4;
        gc.fillPolygon(new double[]{cx, cx + r, cx, cx - r},
                       new double[]{cy - r, cy, cy + r, cy}, 4);
    }
}
