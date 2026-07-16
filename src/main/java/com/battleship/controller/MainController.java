package com.battleship.controller;

import java.io.IOException;

import com.battleship.model.Game;
import com.battleship.service.GameService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controlador de la pantalla inicial del juego.
 *
 * Permite crear una partida básica a partir del nickname ingresado
 * y navegar hacia la pantalla de tableros.
 */
public class MainController {

    private static final String BATTLE_VIEW_PATH = "/com/battleship/view/battle-view.fxml";

    private final GameService gameService;

    @FXML
    private TextField nicknameTextField;

    @FXML
    private Button loadGameButton;

    @FXML
    private Button newGameButton;

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
     * Crea una nueva partida y abre la pantalla inicial de tableros.
     */
    @FXML
    private void onNewGameClicked() {
        String nickname = nicknameTextField.getText();

        if (nickname == null || nickname.isBlank()) {
            showWarning("Nickname requerido", "Debes ingresar un nickname para iniciar la partida.");
            statusLabel.setText("No se pudo iniciar la partida: nickname vacío.");
            return;
        }

        Game game = gameService.createGame(nickname);

        try {
            openBattleView(game);
        } catch (IOException exception) {
            showWarning("Error al abrir la partida", "No fue posible cargar la vista de tableros.");
            statusLabel.setText("Error al cargar la pantalla de juego.");
        }
    }

    /**
     * Acción temporal. La carga real de partida se implementará en el PR de
     * persistencia.
     */
    @FXML
    private void onLoadGameClicked() {
        showInformation("Cargar partida", "Esta opción se implementará en el PR de persistencia.");
    }

    private void openBattleView(Game game) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(BATTLE_VIEW_PATH));
        Parent root = loader.load();

        BattleController battleController = loader.getController();
        battleController.initializeGame(game);

        Stage stage = (Stage) nicknameTextField.getScene().getWindow();
        Scene scene = new Scene(root, 1000, 700);

        stage.setScene(scene);
        stage.setTitle("Batalla Naval - Tableros");
        stage.show();
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