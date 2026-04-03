module ru.nsu.tokarev.snake {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires static lombok;

    opens ru.nsu.tokarev.snake to javafx.fxml;
    opens ru.nsu.tokarev.snake.model to javafx.fxml, com.fasterxml.jackson.databind;
    opens ru.nsu.tokarev.snake.view to javafx.fxml;

    exports ru.nsu.tokarev.snake;
    exports ru.nsu.tokarev.snake.model;
    exports ru.nsu.tokarev.snake.view;
}