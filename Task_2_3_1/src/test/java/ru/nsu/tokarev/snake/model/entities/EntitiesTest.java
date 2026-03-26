package ru.nsu.tokarev.snake.model.entities;

import org.junit.jupiter.api.Test;
import ru.nsu.tokarev.snake.model.Point;
import ru.nsu.tokarev.snake.model.Snake;
import static org.junit.jupiter.api.Assertions.*;

class EntitiesTest {

    @Test
    void testBasicFood() {
        Point p = new Point(1, 1);
        BasicFood food = new BasicFood(p);
        assertEquals(p, food.getPosition());
        
        Snake s = new Snake(5, 5); // Length 3
        food.applyEffect(null, s);
        s.move(20, 20);
        assertEquals(4, s.getLength());
    }

    @Test
    void testBonusFood() {
        Point p = new Point(2, 2);
        BonusFood food = new BonusFood(p);
        assertEquals(p, food.getPosition());
        
        Snake s = new Snake(5, 5);
        food.applyEffect(null, s);
        // +3 growth
        s.move(20, 20);
        s.move(20, 20);
        s.move(20, 20);
        assertEquals(6, s.getLength());
    }

    @Test
    void testPoisonFood() {
        Point p = new Point(3, 3);
        PoisonFood food = new PoisonFood(p);
        assertEquals(p, food.getPosition());
        
        Snake s = new Snake(5, 5); // 3 length
        food.applyEffect(null, s); // shrink 1 -> length 2
        assertEquals(2, s.getLength());
        
        // try shrink below 2
        food.applyEffect(null, s);
        assertEquals(2, s.getLength());
    }

    @Test
    void testBasicObstacle() {
        Point p = new Point(4, 4);
        BasicObstacle obstacle = new BasicObstacle(p);
        assertEquals(p, obstacle.getPosition());
    }
}
