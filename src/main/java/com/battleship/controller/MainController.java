package com.battleship.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.battleship.model.Game;
import com.battleship.persistence.GameStatePersistenceService;
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
 * Controller class for the main view of the Battleship game. It handles user
 * interactions for starting a new game or loading a saved game.
 */
public class MainController {

    private static final String BATTLE_VIEW_PATH = "/com/battleship/view/battle-view.fxml";
    private static final Path GAME_SAVE_PATH = Path.of("data", "game-state.ser");

    private final GameService gameService;
    private final GameStatePersistenceService gameStatePersistenceService;

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
        this.gameStatePersistenceService = new GameStatePersistenceService();
    }

    @FXML
    private void initialize() {
        boolean savedGameExists = Files.exists(GAME_SAVE_PATH);

        loadGameButton.setDisable(!savedGameExists);
        statusLabel.setText("Ingresa tu nickname para iniciar una nueva partida.");

        if (savedGameExists) {
            gameInfoLabel.setText("Hay una partida guardada disponible.");
        } else {
            gameInfoLabel.setText("No hay partida guardada todavía.");
        }
    }

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
            exception.printStackTrace();
            showWarning("Error al abrir la partida", "No fue posible cargar la vista de tableros.");
            statusLabel.setText("Error al cargar la pantalla de juego.");
        }
    }

    @FXML
    private void onLoadGameClicked() {
        try {
            Game loadedGame = gameStatePersistenceService.loadGame(GAME_SAVE_PATH);

            if (loadedGame.isFinished()) {
                showWarning("Partida finalizada",
                        "La partida guardada ya terminó. Se iniciará una nueva partida.");
                statusLabel.setText("La partida guardada ya terminó. Inicia una partida nueva.");
                return;
            }

            openBattleView(loadedGame);
        } catch (FileNotFoundException exception) {
            showWarning("Partida no encontrada", "No hay una partida guardada disponible.");
            loadGameButton.setDisable(true);
            gameInfoLabel.setText("No hay partida guardada todavía.");
        } catch (IOException | ClassNotFoundException exception) {
            showWarning("Error al cargar", "No fue posible cargar la partida guardada.");
            statusLabel.setText("Error al cargar la partida.");
        }
    }

    private void openBattleView(Game game) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainController.class.getResource(BATTLE_VIEW_PATH));
        Parent root = loader.load();

        BattleController battleController = loader.getController();
        battleController.initializeGame(game);

        Stage stage = (Stage) nicknameTextField.getScene().getWindow();
        Scene scene = new Scene(root, 1200, 800);

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
}