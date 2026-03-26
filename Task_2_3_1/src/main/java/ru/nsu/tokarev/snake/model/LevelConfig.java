package ru.nsu.tokarev.snake.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LevelConfig {
    private int maxFoodCount = 3;
    private int obstacleCount = 20;
    private int gameSpeedMs = 140;
    private int winLength = 20;
}
