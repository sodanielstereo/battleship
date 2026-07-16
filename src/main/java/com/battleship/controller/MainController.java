package com.battleship.controller;

import com.battleship.model.Game;
import com.battleship.service.GameService;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controlador de la pantalla inicial del juego.
 *
 * En este PR se conecta la interfaz inicial con el servicio principal
 * para crear una partida a partir del nickname ingresado.
 */
public class MainController {

    private final GameService gameService;
    private Game currentGame;

    @FXML
    private TextField nicknameTextField;

    @FXML
    private Button newGameButton;

    @FXML
    private Button loadGameButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label gameInfoLabel;

    public MainController() {
        this.gameService = new GameService();
    }

    /**
     * Configuración inicial de la pantalla.
     */
    @FXML
    private void initialize() {
        loadGameButton.setDisable(true);
        statusLabel.setText("Ingresa tu nickname para iniciar una nueva partida.");
        gameInfoLabel.setText("No hay partida activa en esta sesión.");
    }

    /**
     * Crea una nueva partida usando el nickname ingresado por el usuario.
     */
    @FXML
    private void onNewGameClicked() {
        String nickname = nicknameTextField.getText();

        if (nickname == null || nickname.isBlank()) {
            showWarning("Nickname requerido", "Debes ingresar un nickname para iniciar la partida.");
            statusLabel.setText("No se pudo iniciar la partida: nickname vacío.");
            return;
        }

        currentGame = gameService.createGame(nickname);

        statusLabel.setText("Partida creada correctamente.");
        gameInfoLabel.setText(
                "Jugador: " + currentGame.getHumanPlayer().getNickname()
                        + " | Oponente: " + currentGame.getMachinePlayer().getNickname()
                        + " | Fase: " + currentGame.getPhase());

        showInformation(
                "Nueva partida",
                "La partida fue creada correctamente para " + currentGame.getHumanPlayer().getNickname() + ".");
    }

    /**
     * Acción temporal. La carga real de partida se implementará en el PR de
     * persistencia.
     */
    @FXML
    private void onLoadGameClicked() {
        showInformation("Cargar partida", "Esta opción se implementará en el PR de persistencia.");
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Batalla Naval");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInformation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Batalla Naval");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}