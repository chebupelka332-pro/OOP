package ru.nsu.tokarev.snake.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.nsu.tokarev.snake.model.GameModel;
import ru.nsu.tokarev.snake.model.Point;
import ru.nsu.tokarev.snake.model.Snake;

import java.util.ArrayList;
import java.util.List;

public class GameRenderer {
    private static final Color BG_COLOR        = Color.web("#1a1a2e");
    private static final Color GRID_COLOR      = Color.web("#16213e").deriveColor(0, 1, 1, 0.4);
    private static final Color OBSTACLE_COLOR  = Color.web("#e94560");
    private static final Color FOOD_COLOR      = Color.web("#f5a623");
    private static final Color HEAD_COLOR      = Color.web("#00d4aa");
    private static final Color BODY_COLOR      = Color.web("#0f9b8e");
    private static final Color TAIL_COLOR      = Color.web("#0a6b60");

    private final Canvas canvas;
    private final int cellSize;

    public GameRenderer(Canvas canvas, int cellSize) {
        this.canvas = canvas;
        this.cellSize = cellSize;
    }

    public void render(GameModel model) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int cols = model.getCols();
        int rows = model.getRows();

        gc.setFill(BG_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(GRID_COLOR);
        gc.setLineWidth(0.5);
        for (int x = 0; x <= cols; x++) gc.strokeLine(x * cellSize, 0, x * cellSize, rows * cellSize);
        for (int y = 0; y <= rows; y++) gc.strokeLine(0, y * cellSize, cols * cellSize, y * cellSize);

        gc.setFill(OBSTACLE_COLOR);
        for (Point p : model.getObstacles()) {
            gc.fillRoundRect(p.x * cellSize + 1, p.y * cellSize + 1,
                    cellSize - 2, cellSize - 2, 3, 3);
        }

        gc.setFill(FOOD_COLOR);
        for (Point p : model.getFood()) {
            double margin = cellSize * 0.2;
            double size   = cellSize - 2 * margin;
            gc.fillOval(p.x * cellSize + margin, p.y * cellSize + margin, size, size);
        }

        Snake snake = model.getSnake();
        List<Point> body = new ArrayList<>(snake.getBody());
        for (int i = 0; i < body.size(); i++) {
            Point p = body.get(i);
            Color color;
            if (i == 0) {
                color = HEAD_COLOR;
            } else if (i == body.size() - 1) {
                color = TAIL_COLOR;
            } else {
                // Gradient from head colour to tail colour
                double t = (double) i / (body.size() - 1);
                color = BODY_COLOR.interpolate(TAIL_COLOR, t);
            }
            gc.setFill(color);
            int margin = (i == 0) ? 0 : 1;
            gc.fillRoundRect(
                    p.x * cellSize + margin, p.y * cellSize + margin,
                    cellSize - 2 * margin, cellSize - 2 * margin,
                    4, 4);
        }

        // Eye on the head
        if (!body.isEmpty()) {
            drawEyes(gc, body.get(0), snake.getDirection());
        }
    }

    private void drawEyes(GraphicsContext gc, Point head, ru.nsu.tokarev.snake.model.Direction dir) {
        double cx = head.x * cellSize + cellSize / 2.0;
        double cy = head.y * cellSize + cellSize / 2.0;
        double eyeR = cellSize * 0.13;
        double eyeOffset = cellSize * 0.27;
        double eyeSpread = cellSize * 0.22;

        double ex1, ey1, ex2, ey2;
        switch (dir) {
            case UP -> {
                ex1 = cx - eyeSpread; ey1 = cy - eyeOffset;
                ex2 = cx + eyeSpread; ey2 = cy - eyeOffset;
            }
            case DOWN -> {
                ex1 = cx - eyeSpread; ey1 = cy + eyeOffset;
                ex2 = cx + eyeSpread; ey2 = cy + eyeOffset;
            }
            case LEFT -> {
                ex1 = cx - eyeOffset; ey1 = cy - eyeSpread;
                ex2 = cx - eyeOffset; ey2 = cy + eyeSpread;
            }
            default -> { // RIGHT
                ex1 = cx + eyeOffset; ey1 = cy - eyeSpread;
                ex2 = cx + eyeOffset; ey2 = cy + eyeSpread;
            }
        }

        gc.setFill(Color.WHITE);
        gc.fillOval(ex1 - eyeR, ey1 - eyeR, eyeR * 2, eyeR * 2);
        gc.fillOval(ex2 - eyeR, ey2 - eyeR, eyeR * 2, eyeR * 2);

        gc.setFill(Color.BLACK);
        double pupilR = eyeR * 0.55;
        gc.fillOval(ex1 - pupilR, ey1 - pupilR, pupilR * 2, pupilR * 2);
        gc.fillOval(ex2 - pupilR, ey2 - pupilR, pupilR * 2, pupilR * 2);
    }
}
