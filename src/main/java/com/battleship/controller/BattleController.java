package com.battleship.controller;

import java.io.IOException;

import com.battleship.model.Game;
import com.battleship.model.board.Board;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Controlador inicial de la pantalla de batalla.
 *
 * En este PR solamente construye visualmente los tableros 10x10.
 * La interacción de disparos y colocación se implementará después.
 */
public class BattleController {

    private static final String MAIN_VIEW_PATH = "/com/battleship/view/main-view.fxml";

    private Game currentGame;

    @FXML
    private Label playerInfoLabel;

    @FXML
    private Label phaseInfoLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private GridPane humanBoardGrid;

    @FXML
    private GridPane machineBoardGrid;

    /**
     * Recibe la partida creada desde la pantalla inicial.
     *
     * @param game partida actual.
     */
    public void initializeGame(Game game) {
        this.currentGame = game;

        playerInfoLabel.setText(
                "Jugador: " + currentGame.getHumanPlayer().getNickname()
                        + " | Oponente: " + currentGame.getMachinePlayer().getNickname());

        phaseInfoLabel.setText("Fase actual: " + currentGame.getPhase());
        statusLabel.setText("Vista inicial de tableros cargada correctamente.");

        buildBoard(humanBoardGrid, "human-board-cell");
        buildBoard(machineBoardGrid, "machine-board-cell");
    }

    /**
     * Regresa a la pantalla inicial.
     */
    @FXML
    private void onBackToStartClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_VIEW_PATH));
            Parent root = loader.load();

            Stage stage = (Stage) playerInfoLabel.getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);

            stage.setScene(scene);
            stage.setTitle("Batalla Naval");
            stage.show();
        } catch (IOException exception) {
            statusLabel.setText("No fue posible regresar a la pantalla inicial.");
        }
    }

    private void buildBoard(GridPane boardGrid, String cellStyleClass) {
        boardGrid.getChildren().clear();

        for (int row = 0; row < Board.DEFAULT_SIZE; row++) {
            for (int column = 0; column < Board.DEFAULT_SIZE; column++) {
                StackPane cell = createBoardCell(row, column, cellStyleClass);
                boardGrid.add(cell, column, row);
            }
        }
    }

    private StackPane createBoardCell(int row, int column, String cellStyleClass) {
        StackPane cell = new StackPane();

        cell.setPrefSize(34, 34);
        cell.setMinSize(34, 34);
        cell.setMaxSize(34, 34);
        cell.setAlignment(Pos.CENTER);

        cell.getStyleClass().add("board-cell");
        cell.getStyleClass().add(cellStyleClass);

        Label coordinateLabel = new Label(String.valueOf(row + 1) + "," + String.valueOf(column + 1));
        coordinateLabel.getStyleClass().add("coordinate-label");

        cell.getChildren().add(coordinateLabel);

        return cell;
    }
}