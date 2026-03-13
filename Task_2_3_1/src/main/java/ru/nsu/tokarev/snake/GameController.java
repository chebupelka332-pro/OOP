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
import ru.nsu.tokarev.snake.model.GameModel;
import ru.nsu.tokarev.snake.view.GameRenderer;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    @FXML private BorderPane root;
    @FXML private Canvas gameCanvas;
    @FXML private Label lengthLabel;
    @FXML private Label statusLabel;

    private GameModel model;
    private GameRenderer renderer;
    private AnimationTimer timer;
    private long lastTick = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int canvasW = GameConfig.COLS * GameConfig.CELL_SIZE;
        int canvasH = GameConfig.ROWS * GameConfig.CELL_SIZE;
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
        model = new GameModel(
                GameConfig.COLS,
                GameConfig.ROWS,
                GameConfig.MAX_FOOD_COUNT,
                GameConfig.OBSTACLE_COUNT
        );
        renderer = new GameRenderer(gameCanvas, GameConfig.CELL_SIZE);
        statusLabel.setText("Используй стрелки для управления. R — перезапуск.");

        if (timer != null) timer.stop();
        lastTick = 0;

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTick == 0) { lastTick = now; return; }
                long elapsedMs = (now - lastTick) / 1_000_000;
                if (elapsedMs >= GameConfig.GAME_SPEED_MS) {
                    lastTick = now;
                    boolean alive = model.tick();
                    renderer.render(model);
                    lengthLabel.setText("Длина: " + model.getSnake().getLength());
                    if (!alive) {
                        statusLabel.setText("Игра окончена! Длина: "
                                + model.getSnake().getLength() + "  |  R — перезапуск");
                        timer.stop();
                    }
                }
            }
        };

        renderer.render(model);
        lengthLabel.setText("Длина: " + model.getSnake().getLength());
        timer.start();
    }

    @FXML
    public void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        if (code == KeyCode.R) {
            startNewGame();
            return;
        }
        if (model.getState() == GameModel.State.GAME_OVER) return;
        switch (code) {
            case UP, W    -> model.setDirection(Direction.UP);
            case DOWN, S  -> model.setDirection(Direction.DOWN);
            case LEFT, A  -> model.setDirection(Direction.LEFT);
            case RIGHT, D -> model.setDirection(Direction.RIGHT);
            default -> {}
        }
    }
}
