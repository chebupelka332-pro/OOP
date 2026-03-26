package ru.nsu.tokarev.snake.model.entities;

import javafx.scene.canvas.GraphicsContext;
import ru.nsu.tokarev.snake.model.Point;
import ru.nsu.tokarev.snake.model.GameModel;
import ru.nsu.tokarev.snake.model.Snake;


public interface Obstacle {
    Point getPosition();
    void render(GraphicsContext gc, int cellSize);
}