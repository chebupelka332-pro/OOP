package ru.nsu.tokarev.snake;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ru.nsu.tokarev.snake.model.Direction;
import ru.nsu.tokarev.snake.model.GameConfig;
import ru.nsu.tokarev.snake.model.GameMessages;
import ru.nsu.tokarev.snake.model.GameModel;
import ru.nsu.tokarev.snake.model.LevelConfig;
import ru.nsu.tokarev.snake.view.GameRenderer;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    @FXML private BorderPane root;
    @FXML private Canvas gameCanvas;
    @FXML private Label levelLabel;
    @FXML private Label lengthLabel;
    @FXML private Label statusLabel;

    private GameModel model;
    private GameRenderer renderer;
    private AnimationTimer timer;
    private long lastTick = 0;
    private GameConfig config;
    private GameMessages messages;
    private int currentLevelIndex = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        config = GameConfig.load();
        messages = GameMessages.load();
        
        int canvasW = config.getCols() * config.getCellSize();
        int canvasH = config.getRows() * config.getCellSize();
        gameCanvas.setWidth(canvasW);
        gameCanvas.setHeight(canvasH);

        gameCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs2, oldWin, newWin) -> {
                    if (newWin instanceof Stage stage) {
                        stage.sizeToScene();
                    }
                });
            }
        });

        startNewGame();
    }

    private void startNewGame() {
        if (config.getLevels() == null || config.getLevels().isEmpty()) {
            System.err.println("No levels found in config.");
            return;
        }
        LevelConfig level = config.getLevels().get(currentLevelIndex);

        model = new GameModel(
                config.getCols(),
                config.getRows(),
                level.getMaxFoodCount(),
                level.getObstacleCount(),
                new ru.nsu.tokarev.snake.model.LengthWinCondition(level.getWinLength())
        );
        renderer = new GameRenderer(gameCanvas, config.getCellSize());
        levelLabel.setText("Уровень: " + (currentLevelIndex + 1));
        statusLabel.setText("Уровень " + (currentLevelIndex + 1) + "! " + messages.getControlsHint());

        if (timer != null) timer.stop();
        lastTick = 0;

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTick == 0) { lastTick = now; return; }
                long elapsedMs = (now - lastTick) / 1_000_000;
                if (elapsedMs >= level.getGameSpeedMs()) {
                    lastTick = now;
                    boolean alive = model.tick();
                    renderer.render(model);
                    lengthLabel.setText(String.format(messages.getLengthFormat(), model.getSnake().getLength(), level.getWinLength()));
                    if (!alive) {
                        if (model.getState() == GameModel.State.WON) {
                            if (currentLevelIndex + 1 < config.getLevels().size()) {
                                currentLevelIndex++;
                                startNewGame();
                            } else {
                                statusLabel.setText(messages.getGameWon());
                                timer.stop();
                            }
                        } else {
                            statusLabel.setText(String.format(messages.getGameOver(), model.getSnake().getLength()));
                            timer.stop();
                        }
                    }
                }
            }
        };

        renderer.render(model);
        lengthLabel.setText(String.format(messages.getLengthFormat(), model.getSnake().getLength(), level.getWinLength()));
        timer.start();
    }

    @FXML
    public void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        if (code == KeyCode.R) {
            currentLevelIndex = 0;
            startNewGame();
            return;
        }
        if (model.getState() == GameModel.State.GAME_OVER || model.getState() == GameModel.State.WON) return;
        switch (code) {
            case UP, W    -> model.setDirection(Direction.UP);
            case DOWN, S  -> model.setDirection(Direction.DOWN);
            case LEFT, A  -> model.setDirection(Direction.LEFT);
            case RIGHT, D -> model.setDirection(Direction.RIGHT);
            default -> {}
        }
    }
}
