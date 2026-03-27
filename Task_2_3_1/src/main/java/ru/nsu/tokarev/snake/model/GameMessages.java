package ru.nsu.tokarev.snake.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import java.io.InputStream;

@Getter
@Setter
public class GameMessages {
    private String controlsHint;
    private String lengthFormat;
    private String gameOver;
    private String gameWon;
    private String levelLabel;
    private String levelStart;

    public static GameMessages load() {
        try (InputStream is = GameMessages.class.getResourceAsStream("/messages_ru.json")) {
            if (is != null) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(is, GameMessages.class);
            }
        } catch (Exception e) {
            System.err.println("Could not load messages_ru.json, using defaults.");
            e.printStackTrace();
        }
        return new GameMessages();
    }
}