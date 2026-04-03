package ru.nsu.tokarev.snake.model.entities;

import java.util.List;
import ru.nsu.tokarev.snake.model.Point;
import ru.nsu.tokarev.snake.model.GameModel;
import ru.nsu.tokarev.snake.model.Snake;


public interface EntityGenerator<T> {
    T generate(List<Point> emptyCells);
}