package ru.nsu.tokarev.snake.model.config;

import org.junit.jupiter.api.Test;
import ru.nsu.tokarev.snake.model.GameConfig;
import ru.nsu.tokarev.snake.model.GameMessages;
import ru.nsu.tokarev.snake.model.LevelConfig;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    @Test
    void testLevelConfig() {
        LevelConfig lvl = new LevelConfig();
        lvl.setMaxFoodCount(5);
        lvl.setObstacleCount(15);
        lvl.setGameSpeedMs(100);
        lvl.setWinLength(30);

        assertEquals(5, lvl.getMaxFoodCount());
        assertEquals(15, lvl.getObstacleCount());
        assertEquals(100, lvl.getGameSpeedMs());
        assertEquals(30, lvl.getWinLength());
    }

    @Test
    void testGameConfig() {
        GameConfig config = new GameConfig();
        config.setCols(10);
        config.setRows(10);
        config.setCellSize(20);

        List<LevelConfig> levels = new ArrayList<>();
        levels.add(new LevelConfig());
        config.setLevels(levels);

        assertEquals(10, config.getCols());
        assertEquals(10, config.getRows());
        assertEquals(20, config.getCellSize());
        assertEquals(1, config.getLevels().size());
    }

    @Test
    void testGameConfigLoad() {
        GameConfig config = GameConfig.load();
        assertNotNull(config);
        assertTrue(config.getCols() > 0);
        assertTrue(config.getRows() > 0);
        assertTrue(config.getCellSize() > 0);
        assertNotNull(config.getLevels());
    }

    @Test
    void testGameMessagesLoad() {
        GameMessages msg = GameMessages.load();
        assertNotNull(msg);
        
        assertNotNull(msg.getLengthFormat());
        assertNotNull(msg.getGameOver());
        assertNotNull(msg.getGameWon());
        assertNotNull(msg.getControlsHint());
    }
    
    @Test
    void testGameMessagesSetters() {
        GameMessages msg = new GameMessages();
        
        msg.setLengthFormat("Len");
        msg.setGameOver("Over");
        msg.setGameWon("Won");
        msg.setControlsHint("Help");
        
        
        assertEquals("Len", msg.getLengthFormat());
        assertEquals("Over", msg.getGameOver());
        assertEquals("Won", msg.getGameWon());
        assertEquals("Help", msg.getControlsHint());
    }
}
