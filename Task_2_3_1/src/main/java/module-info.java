module ru.nsu.tokarev.snake {
    requires javafx.controls;
    requires javafx.fxml;

    opens ru.nsu.tokarev.snake to javafx.fxml;
    opens ru.nsu.tokarev.snake.model to javafx.fxml;
    opens ru.nsu.tokarev.snake.view to javafx.fxml;

    exports ru.nsu.tokarev.snake;
    exports ru.nsu.tokarev.snake.model;
    exports ru.nsu.tokarev.snake.view;
}