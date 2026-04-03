package ru.nsu.tokarev.snake.model.entities;

import ru.nsu.tokarev.snake.model.Point;
import java.util.List;
import java.util.Random;

public class RandomFoodFactory implements EntityGenerator<Food> {
    private final Random random = new Random();
    private final int basicChance;
    private final int bonusChance;
    private final int poisonChance;

    public RandomFoodFactory(int basicChance, int bonusChance, int poisonChance) {
        this.basicChance = basicChance;
        this.bonusChance = bonusChance;
        this.poisonChance = poisonChance;
    }

    @Override
    public Food generate(List<Point> emptyCells) {
        if (emptyCells.isEmpty()) return null;
        int idx = random.nextInt(emptyCells.size());
        Point pos = emptyCells.remove(idx);

        int totalChance = basicChance + bonusChance + poisonChance;
        if (totalChance <= 0) totalChance = 1; // fallback
        
        int chance = random.nextInt(totalChance);
        if (chance < basicChance) {
            return new BasicFood(pos);
        } else if (chance < basicChance + bonusChance) {
            return new BonusFood(pos);
        } else {
            return new PoisonFood(pos);
        }
    }
}
