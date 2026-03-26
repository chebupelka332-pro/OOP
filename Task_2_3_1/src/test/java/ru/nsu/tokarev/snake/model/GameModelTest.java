package ru.nsu.tokarev.snake.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameModelTest {

    private static final int COLS = 50;
    private static final int ROWS = 50;
    private static final int FOOD_COUNT = 3;
    private static final int OBSTACLE_COUNT = 0;
    private static final LengthWinCondition WIN_CONDITION = new LengthWinCondition(100);

    private GameModel model;

    @BeforeEach
    void setUp() {
        model = new GameModel(COLS, ROWS, FOOD_COUNT, OBSTACLE_COUNT, WIN_CONDITION);
    }

    @Test
    void testInitialStateIsRunning() {
        assertEquals(GameModel.State.RUNNING, model.getState());
    }

    @Test
    void testInitialSnakeLength() {
        assertEquals(3, model.getSnake().getLength());
    }

    @Test
    void testInitialFoodCount() {
        assertEquals(FOOD_COUNT, model.getFoods().size());
    }

    @Test
    void testNoObstaclesWhenZero() {
        assertTrue(model.getObstacles().isEmpty());
    }

    @Test
    void testTickReturnsTrue() {
        assertTrue(model.tick());
    }

    @Test
    void testTickMovesSnake() {
        Point headBefore = model.getSnake().getHead();
        model.tick();
        assertNotEquals(headBefore, model.getSnake().getHead());
    }

    @Test
    void testEatingFoodGrowsSnake() {
        Snake snake = model.getSnake();
        Point food = model.getFoods().iterator().next().getPosition();
        Point head = snake.getHead();

        int lengthBefore = snake.getLength();
        driveSnakeToPoint(model, head, food);

        assertTrue(model.getSnake().getLength() > lengthBefore);
    }

    @Test
    void testFoodRefillsAfterEating() {
        Snake snake = model.getSnake();
        Point food = model.getFoods().iterator().next().getPosition();
        driveSnakeToPoint(model, snake.getHead(), food);

        assertEquals(FOOD_COUNT, model.getFoods().size());
    }

    @Test
    void testObstaclesPlaced() {
        GameModel m = new GameModel(COLS, ROWS, 1, 10, WIN_CONDITION);
        assertEquals(10, m.getObstacles().size());
    }

    @Test
    void testObstacleNotOnSnakeStart() {
        GameModel m = new GameModel(COLS, ROWS, 1, 50, WIN_CONDITION);
        Snake s = m.getSnake();
        for (ru.nsu.tokarev.snake.model.entities.Obstacle obsEntity : m.getObstacles()) {
            Point obs = obsEntity.getPosition();
            assertFalse(s.contains(obs),
                    "Obstacle must not overlap snake start: " + obs);
        }
    }

    @Test
    void testHittingObstacleCausesGameOver() {
        GameModel m = new GameModel(COLS, ROWS, 1, 20, WIN_CONDITION);
        Point target = null;
        for (ru.nsu.tokarev.snake.model.entities.Obstacle obsEntity : m.getObstacles()) {
            Point obs = obsEntity.getPosition();
            target = obs;
            break;
        }
        assertNotNull(target, "Expected at least one obstacle");

        int maxSteps = COLS * ROWS;
        int steps = 0;
        while (m.getState() == GameModel.State.RUNNING && steps++ < maxSteps) {
            Point head = m.getSnake().getHead();
            if (head.x != target.x) {
                m.setDirection(target.x > head.x ? Direction.RIGHT : Direction.LEFT);
            } else {
                m.setDirection(target.y > head.y ? Direction.DOWN : Direction.UP);
            }
            m.tick();
        }
        assertEquals(GameModel.State.GAME_OVER, m.getState());
    }

    @Test
    void testTickReturnsFalseOnGameOver() {
        forceSelfCollision(model);
        assertFalse(model.tick());
    }

    @Test
    void testStateGameOverAfterSelfCollision() {
        forceSelfCollision(model);
        assertEquals(GameModel.State.GAME_OVER, model.getState());
    }

    @Test
    void testSnakeWrapsHorizontally() {
        GameModel m = new GameModel(COLS, ROWS, 1, 0, WIN_CONDITION);
        Snake s = m.getSnake();

        int stepsToEdge = COLS - 1 - s.getHead().x;
        for (int i = 0; i < stepsToEdge; i++) m.tick();

        int headXBefore = m.getSnake().getHead().x;
        m.tick();
        int headXAfter = m.getSnake().getHead().x;

        assertTrue(headXBefore > headXAfter || headXAfter == 0,
                "Snake should wrap around the right edge");
    }

    private void driveSnakeToPoint(GameModel m, Point from, Point target) {
        int maxSteps = COLS + ROWS + 10;
        int steps = 0;
        while (!m.getSnake().getHead().equals(target)
                && m.getState() == GameModel.State.RUNNING
                && steps++ < maxSteps) {

            Point h = m.getSnake().getHead();
            if (h.x != target.x) {
                m.setDirection(target.x > h.x ? Direction.RIGHT : Direction.LEFT);
            } else if (h.y != target.y) {
                m.setDirection(target.y > h.y ? Direction.DOWN : Direction.UP);
            }
            m.tick();
        }
    }

    private void forceSelfCollision(GameModel m) {
        m.getSnake().addGrowth(10);
        tickN(m, 10);
        
        int steps = 0;
        while (m.getState() == GameModel.State.RUNNING && steps++ < 5000) {
            m.setDirection(Direction.UP);    tickN(m, 1);
            m.setDirection(Direction.RIGHT); tickN(m, 1);
            m.setDirection(Direction.DOWN);  tickN(m, 1);
            m.setDirection(Direction.LEFT);  tickN(m, 1);
        }
    }

    private void tickN(GameModel m, int n) {
        for (int i = 0; i < n && m.getState() == GameModel.State.RUNNING; i++) {
            m.tick();
        }
    }
}
