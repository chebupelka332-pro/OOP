package ru.nsu.tokarev.snake.model.entities;

import ru.nsu.tokarev.snake.model.Point;
import java.util.List;
import java.util.Random;

public class RandomFoodFactory implements EntityGenerator<Food> {
    private final Random random = new Random();

    @Override
    public Food generate(List<Point> emptyCells) {
        if (emptyCells.isEmpty()) return null;
        int idx = random.nextInt(emptyCells.size());
        Point pos = emptyCells.remove(idx);

        int chance = random.nextInt(100);
        if (chance < 70) {
            return new BasicFood(pos);
        } else if (chance < 90) {
            return new BonusFood(pos);
        } else {
            return new PoisonFood(pos);
        }
    }
}
