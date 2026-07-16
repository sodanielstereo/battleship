module com.battleship {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    exports com.battleship.app;
    exports com.battleship.exception;
    exports com.battleship.model;
    exports com.battleship.model.board;
    exports com.battleship.model.enums;
    exports com.battleship.model.player;
    exports com.battleship.model.ship;
    exports com.battleship.service;

    opens com.battleship.controller to javafx.fxml;
}