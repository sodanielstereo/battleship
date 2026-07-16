package com.battleship.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

/**
* Controlador inicial de la aplicación.
* Se encarga de manejar los eventos de la primera vista cargada.
 */
public class MainController {

    @FXML
    private Label statusLabel;

    /**
     * Acción temporal para verificar que los eventos de JavaFX funcionan.
     */
    @FXML
    private void onNewGameClicked() {
        statusLabel.setText("Proyecto inicial cargado correctamente.");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Batalla Naval");
        alert.setHeaderText("Estructura inicial lista");
        alert.setContentText("El proyecto Maven JavaFX está funcionando correctamente.");
        alert.showAndWait();
    }
}