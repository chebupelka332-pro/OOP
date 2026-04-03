package ru.nsu.tokarev.snake.model.entities;

import javafx.scene.canvas.GraphicsContext;
import ru.nsu.tokarev.snake.model.Point;
import ru.nsu.tokarev.snake.model.GameModel;
import ru.nsu.tokarev.snake.model.Snake;


public interface Food {
    Point getPosition();
    void applyEffect(GameModel model, Snake snake);
    void render(GraphicsContext gc, int cellSize);
}