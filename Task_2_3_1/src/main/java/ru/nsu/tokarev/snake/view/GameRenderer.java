package ru.nsu.tokarev.snake.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.nsu.tokarev.snake.model.entities.Food;
import ru.nsu.tokarev.snake.model.entities.Obstacle;
import ru.nsu.tokarev.snake.model.GameModel;
import ru.nsu.tokarev.snake.model.Point;
import ru.nsu.tokarev.snake.model.Snake;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GameRenderer {
    private static final Color BG_COLOR        = Color.web("#1a1a2e");
    private static final Color GRID_COLOR      = Color.web("#16213e").deriveColor(0, 1, 1, 0.4);
    private static final Color HEAD_COLOR      = Color.web("#00d4aa");
    private static final Color BODY_COLOR      = Color.web("#0f9b8e");
    private static final Color TAIL_COLOR      = Color.web("#0a6b60");

    private final Canvas canvas;
    private final int cellSize;

    private boolean firstRender = true;
    private final Set<Point> lastObstaclesPositions = new HashSet<>();
    private final Set<Point> lastFoodPositions = new HashSet<>();
    private final List<Point> lastSnakeBody = new ArrayList<>();
    private ru.nsu.tokarev.snake.model.Direction lastDir = null;

    public GameRenderer(Canvas canvas, int cellSize) {
        this.canvas = canvas;
        this.cellSize = cellSize;
    }

    public void render(GameModel model) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int cols = model.getCols();
        int rows = model.getRows();
        Snake snake = model.getSnake();
        List<Point> currentBody = new ArrayList<>(snake.getBody());
        
        Collection<Food> currentFoodItems = model.getFoods();
        Set<Point> currentFoodPositions = currentFoodItems.stream().map(Food::getPosition).collect(Collectors.toSet());
        
        Collection<Obstacle> currentObstacles = model.getObstacles();
        Set<Point> currentObstaclesPositions = currentObstacles.stream().map(Obstacle::getPosition).collect(Collectors.toSet());

        if (firstRender) {
            gc.setFill(BG_COLOR);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            drawGrid(gc, cols, rows);
            firstRender = false;
        } else {
            // Determine cells to clear: cells occupied previously but not anymore
            Set<Point> cellsToClear = new HashSet<>();
            
            for (Point p : lastSnakeBody) {
                if (!currentBody.contains(p)) cellsToClear.add(p);
            }
            for (Point p : lastFoodPositions) {
                if (!currentFoodPositions.contains(p)) cellsToClear.add(p);
            }
            for (Point p : lastObstaclesPositions) {
                if (!currentObstaclesPositions.contains(p)) cellsToClear.add(p);
            }

            // Always clear old and new head to redraw eyes properly
            if (!lastSnakeBody.isEmpty()) cellsToClear.add(lastSnakeBody.get(0));
            if (!currentBody.isEmpty()) cellsToClear.add(currentBody.get(0));

            // Clear those cells
            gc.setFill(BG_COLOR);
            for (Point p : cellsToClear) {
                gc.fillRect(p.x * cellSize, p.y * cellSize, cellSize, cellSize);
                // redraw grid line over cleared cell
                gc.setStroke(GRID_COLOR);
                gc.setLineWidth(0.5);
                gc.strokeLine(p.x * cellSize, p.y * cellSize, (p.x + 1) * cellSize, p.y * cellSize); // Top
                gc.strokeLine(p.x * cellSize, p.y * cellSize, p.x * cellSize, (p.y + 1) * cellSize); // Left
            }
        }

        // Redraw only what's needed. For simplicity of delta, just redraw all active entities over their cells since we cleared trailing tails
        
        for (Obstacle obs : currentObstacles) {
            if (firstRender || !lastObstaclesPositions.contains(obs.getPosition())) {
                obs.render(gc, cellSize);
            }
        }

        for (Food f : currentFoodItems) {
            if (firstRender || !lastFoodPositions.contains(f.getPosition())) {
                f.render(gc, cellSize);
            }
        }

        for (int i = 0; i < currentBody.size(); i++) {
            Point p = currentBody.get(i);
            // Re-draw if it's new, it's the head (with eyes), or we just cleared it
            Color color;
            if (i == 0) {
                color = HEAD_COLOR;
            } else if (i == currentBody.size() - 1) {
                color = TAIL_COLOR;
            } else {
                double t = (double) i / (currentBody.size() - 1);
                color = BODY_COLOR.interpolate(TAIL_COLOR, t);
            }
            // To ensure smooth shading update without missing cells, just redraw the body.
            // Since it only redraws small rects, the main cost was BG filling the whole screen.
            gc.setFill(color);
            int margin = (i == 0) ? 0 : 1;
            // First clear it lightly to prevent artifacts from overdrawing with gradients
            gc.clearRect(p.x * cellSize, p.y * cellSize, cellSize, cellSize);
            gc.setFill(BG_COLOR);
            gc.fillRect(p.x * cellSize, p.y * cellSize, cellSize, cellSize);
            gc.setFill(color);
            gc.fillRoundRect(
                    p.x * cellSize + margin, p.y * cellSize + margin,
                    cellSize - 2 * margin, cellSize - 2 * margin,
                    4, 4);
        }

        // Eye on the head
        if (!currentBody.isEmpty()) {
            drawEyes(gc, currentBody.get(0), snake.getDirection());
        }

        // Update cache
        lastObstaclesPositions.clear(); lastObstaclesPositions.addAll(currentObstaclesPositions);
        lastFoodPositions.clear(); lastFoodPositions.addAll(currentFoodPositions);
        lastSnakeBody.clear(); lastSnakeBody.addAll(currentBody);
        lastDir = snake.getDirection();
    }

    private void drawGrid(GraphicsContext gc, int cols, int rows) {
        gc.setStroke(GRID_COLOR);
        gc.setLineWidth(0.5);
        for (int x = 0; x <= cols; x++) gc.strokeLine(x * cellSize, 0, x * cellSize, rows * cellSize);
        for (int y = 0; y <= rows; y++) gc.strokeLine(0, y * cellSize, cols * cellSize, y * cellSize);
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
