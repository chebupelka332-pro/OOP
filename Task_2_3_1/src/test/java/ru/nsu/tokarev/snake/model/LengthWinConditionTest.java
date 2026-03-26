package ru.nsu.tokarev.snake.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LengthWinConditionTest {
    @Test
    void testWinConditionMet() {
        LengthWinCondition lw = new LengthWinCondition(6);
        assertEquals(6, lw.getTargetLength());

        GameModel dummyModel = new GameModel(10, 10, 1, 0, lw);
        assertFalse(lw.isMet(dummyModel));
        
        // grow
        dummyModel.getSnake().addGrowth(3);
        dummyModel.getSnake().move(10, 10);
        dummyModel.getSnake().move(10, 10);
        dummyModel.getSnake().move(10, 10);
        
        assertEquals(6, dummyModel.getSnake().getLength());
        assertTrue(lw.isMet(dummyModel));
    }
}
