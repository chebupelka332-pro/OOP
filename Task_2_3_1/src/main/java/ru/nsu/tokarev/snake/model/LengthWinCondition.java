package ru.nsu.tokarev.snake.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class LengthWinCondition implements WinCondition {
    @Getter
    private final int targetLength;

    @Override
    public boolean isMet(GameModel model) {
        return model.getSnake().getLength() >= targetLength;
    }
}
