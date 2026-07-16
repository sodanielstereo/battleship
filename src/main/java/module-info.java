module com.battleship {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    exports com.battleship.app;

    opens com.battleship.controller to javafx.fxml;
}