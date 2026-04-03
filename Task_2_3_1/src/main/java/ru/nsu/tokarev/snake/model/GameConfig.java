package ru.nsu.tokarev.snake.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
public class GameConfig {
    private int cols;
    private int rows;
    private int cellSize;

    private List<LevelConfig> levels;

    public GameConfig() {
        levels = new ArrayList<>();
        levels.add(new LevelConfig());
    }

    public static GameConfig load() {
        try (InputStream is = GameConfig.class.getResourceAsStream("/config.json")) {
            if (is != null) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(is, GameConfig.class);
            }
        } catch (Exception e) {
            System.err.println("Could not load config.json, using defaults.");
            e.printStackTrace();
        }
        return new GameConfig();
    }
}

