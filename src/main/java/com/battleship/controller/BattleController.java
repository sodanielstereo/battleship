package com.battleship.controller;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.List;

import com.battleship.exception.InvalidGameStateException;
import com.battleship.exception.InvalidPlacementException;
import com.battleship.exception.InvalidShotException;
import com.battleship.model.Game;
import com.battleship.model.board.Board;
import com.battleship.model.board.Cell;
import com.battleship.model.board.Coordinate;
import com.battleship.model.enums.CellState;
import com.battleship.model.enums.GamePhase;
import com.battleship.model.enums.Orientation;
import com.battleship.model.enums.ShipType;
import com.battleship.model.enums.ShotResult;
import com.battleship.model.enums.Turn;
import com.battleship.model.player.Player;
import com.battleship.model.ship.AircraftCarrier;
import com.battleship.model.ship.Destroyer;
import com.battleship.model.ship.Frigate;
import com.battleship.model.ship.Ship;
import com.battleship.model.ship.Submarine;
import com.battleship.service.GameService;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controlador de la pantalla de batalla.
 */
public class BattleController {

    private static final String MAIN_VIEW_PATH = "/com/battleship/view/main-view.fxml";
    private static final Duration MACHINE_TURN_DELAY = Duration.millis(650);

    private final GameService gameService;
    private Game currentGame;
    private boolean machineTurnRunning;

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

    public BattleController() {
        this.gameService = new GameService();
    }

    /**
     * Recibe la partida creada desde la pantalla inicial.
     *
     * @param game partida actual.
     */
    public void initializeGame(Game game) {
        this.currentGame = game;
        this.shipsSprite = new Image(getClass().getResourceAsStream(SHIPS_SPRITE_PATH));

        currentGame.setPhase(GamePhase.PLAYER_POSITIONING_SHIPS);
        loadInitialFleetCounts();

        playerInfoLabel.setText(
                "Jugador: " + currentGame.getHumanPlayer().getNickname()
                        + " | Oponente: " + currentGame.getMachinePlayer().getNickname());

        try {
            prepareGame();
            statusLabel.setText("Partida lista. Dispara en el territorio enemigo.");
        } catch (InvalidPlacementException | InvalidGameStateException exception) {
            statusLabel.setText("No fue posible preparar la partida: " + exception.getMessage());
        }

        refreshBoards();
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

    private void prepareGame() throws InvalidGameStateException {
        if (currentGame.getPhase() != GamePhase.PLACEMENT) {
            return;
        }

        placeDefaultFleet(currentGame.getHumanPlayer(), List.of(
                new ShipPlacement(new AircraftCarrier(), new Coordinate(0, 0), Orientation.HORIZONTAL),
                new ShipPlacement(new Submarine(), new Coordinate(2, 0), Orientation.HORIZONTAL),
                new ShipPlacement(new Submarine(), new Coordinate(2, 5), Orientation.HORIZONTAL),
                new ShipPlacement(new Destroyer(), new Coordinate(4, 0), Orientation.HORIZONTAL),
                new ShipPlacement(new Destroyer(), new Coordinate(4, 4), Orientation.HORIZONTAL),
                new ShipPlacement(new Destroyer(), new Coordinate(4, 8), Orientation.VERTICAL),
                new ShipPlacement(new Frigate(), new Coordinate(6, 0), Orientation.HORIZONTAL),
                new ShipPlacement(new Frigate(), new Coordinate(6, 2), Orientation.HORIZONTAL),
                new ShipPlacement(new Frigate(), new Coordinate(6, 4), Orientation.HORIZONTAL),
                new ShipPlacement(new Frigate(), new Coordinate(6, 6), Orientation.HORIZONTAL)));

        placeDefaultFleet(currentGame.getMachinePlayer(), List.of(
                new ShipPlacement(new AircraftCarrier(), new Coordinate(0, 5), Orientation.VERTICAL),
                new ShipPlacement(new Submarine(), new Coordinate(3, 1), Orientation.HORIZONTAL),
                new ShipPlacement(new Submarine(), new Coordinate(5, 0), Orientation.VERTICAL),
                new ShipPlacement(new Destroyer(), new Coordinate(6, 6), Orientation.VERTICAL),
                new ShipPlacement(new Destroyer(), new Coordinate(8, 1), Orientation.HORIZONTAL),
                new ShipPlacement(new Destroyer(), new Coordinate(8, 5), Orientation.HORIZONTAL),
                new ShipPlacement(new Frigate(), new Coordinate(0, 0), Orientation.HORIZONTAL),
                new ShipPlacement(new Frigate(), new Coordinate(2, 9), Orientation.HORIZONTAL),
                new ShipPlacement(new Frigate(), new Coordinate(5, 9), Orientation.HORIZONTAL),
                new ShipPlacement(new Frigate(), new Coordinate(9, 9), Orientation.HORIZONTAL)));

        gameService.startGame(currentGame);
    }

    private void placeDefaultFleet(Player player, List<ShipPlacement> placements) {
        for (ShipPlacement placement : placements) {
            gameService.placeShip(player, placement.ship(), placement.coordinate(), placement.orientation());
        }
    }

    private void refreshBoards() {
        phaseInfoLabel.setText("Fase actual: " + currentGame.getPhase() + " | Turno: " + currentGame.getCurrentTurn());
        buildBoard(humanBoardGrid, currentGame.getHumanPlayer().getBoard(), "human-board-cell", true, false);
        buildBoard(machineBoardGrid, currentGame.getMachinePlayer().getBoard(), "machine-board-cell", false, true);
        machineBoardGrid.setDisable(currentGame.getCurrentTurn() != Turn.HUMAN
                || currentGame.getPhase() != GamePhase.IN_PROGRESS
                || machineTurnRunning);
    }

    private void buildBoard(GridPane boardGrid, Board board, String cellStyleClass, boolean revealShips, boolean enemyBoard) {
        boardGrid.getChildren().clear();

        for (int row = 0; row < Board.DEFAULT_SIZE; row++) {
            for (int column = 0; column < Board.DEFAULT_SIZE; column++) {
                Coordinate coordinate = new Coordinate(row, column);
                StackPane cell = createBoardCell(board.getCell(coordinate), cellStyleClass, revealShips, enemyBoard);
                boardGrid.add(cell, column, row);
            }
        }
    }

    private StackPane createBoardCell(Cell boardCell, String cellStyleClass, boolean revealShips, boolean enemyBoard) {
        StackPane visualCell = new StackPane();

        visualCell.setPrefSize(34, 34);
        visualCell.setMinSize(34, 34);
        visualCell.setMaxSize(34, 34);
        visualCell.setAlignment(Pos.CENTER);

        visualCell.getStyleClass().add("board-cell");
        visualCell.getStyleClass().add(cellStyleClass);
        visualCell.getStyleClass().add(resolveCellStateStyle(boardCell, revealShips));

        Coordinate coordinate = boardCell.getCoordinate();
        Label coordinateLabel = new Label(String.valueOf(coordinate.getRow() + 1)
                + "," + String.valueOf(coordinate.getColumn() + 1));
        coordinateLabel.getStyleClass().add("coordinate-label");
        visualCell.getChildren().add(coordinateLabel);

        if (currentGame.getPhase() == GamePhase.PLAYER_POSITIONING_SHIPS && !enemyBoard) {
            configurePlacementDrop(visualCell, coordinate);
        }

        if (enemyBoard) {
            visualCell.setOnMouseClicked(event -> onEnemyCellClicked(coordinate));
        }

        return visualCell;
    }

    private void configurePlacementDrop(StackPane visualCell, Coordinate coordinate) {
        visualCell.setOnDragOver(event -> {
            if (event.getGestureSource() != visualCell && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        visualCell.setOnDragDropped(event -> {
            boolean success = placeDraggedShip(event, coordinate);
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private boolean placeDraggedShip(DragEvent event, Coordinate coordinate) {
        Dragboard dragboard = event.getDragboard();

        if (!dragboard.hasString() || currentGame.getPhase() != GamePhase.PLAYER_POSITIONING_SHIPS) {
            return false;
        }

        ShipType shipType = ShipType.valueOf(dragboard.getString());

        if (remainingShips.getOrDefault(shipType, 0) <= 0) {
            return false;
        }

        try {
            gameService.placeShip(
                    currentGame.getHumanPlayer(),
                    createShip(shipType),
                    coordinate,
                    currentOrientation
            );
            remainingShips.put(shipType, remainingShips.get(shipType) - 1);
            statusLabel.setText(shipType.getDisplayName() + " ubicado en " + formatCoordinate(coordinate) + ".");
            finishPlacementIfReady();
            refreshView();
            return true;
        } catch (InvalidPlacementException exception) {
            statusLabel.setText("No puedes ubicar esa nave ahi.");
            return false;
        }
    }

    private void finishPlacementIfReady() {
        if (!allHumanShipsPlaced()) {
            return;
        }

        visualCell.getChildren().add(coordinateLabel);

        if (enemyBoard) {
            visualCell.setOnMouseClicked(event -> onEnemyCellClicked(coordinate));
        }

        return visualCell;
    }

    private String resolveCellStateStyle(Cell cell, boolean revealShips) {
        CellState state = cell.getState();

        if (state == CellState.WATER) {
            return "cell-water";
        }

        if (state == CellState.HIT) {
            return "cell-hit";
        }

        if (state == CellState.SUNK) {
            return "cell-sunk";
        }

        if (state == CellState.SHIP && revealShips) {
            return "cell-ship";
        }

        return "cell-empty";
    }

    private void onEnemyCellClicked(Coordinate coordinate) {
        if (currentGame == null || currentGame.getPhase() != GamePhase.IN_PROGRESS) {
            return;
        }

        if (currentGame.getCurrentTurn() != Turn.HUMAN || machineTurnRunning) {
            statusLabel.setText("Espera el turno del jugador.");
            return;
        }

        try {
            ShotResult result = gameService.humanShoots(currentGame, coordinate);
            statusLabel.setText("Tu disparo en " + formatCoordinate(coordinate) + ": " + describeShotResult(result));
            refreshBoards();
            handleEndOrMachineTurn();
        } catch (InvalidGameStateException | InvalidShotException exception) {
            statusLabel.setText(exception.getMessage());
        }
    }

    private void handleEndOrMachineTurn() {
        if (currentGame.getPhase() == GamePhase.FINISHED) {
            showFinishedMessage();
            refreshBoards();
            return;
        }

        if (currentGame.getCurrentTurn() == Turn.MACHINE) {
            playMachineTurn();
        }
    }

    private void playMachineTurn() {
        machineTurnRunning = true;
        machineBoardGrid.setDisable(true);
        statusLabel.setText("Turno de la maquina...");

        PauseTransition pause = new PauseTransition(MACHINE_TURN_DELAY);
        pause.setOnFinished(event -> {
            try {
                ShotResult result = gameService.machineShoots(currentGame);
                Coordinate coordinate = currentGame.getShotHistory().get(0).getCoordinate();
                statusLabel.setText("La maquina disparo en " + formatCoordinate(coordinate)
                        + ": " + describeShotResult(result));
            } catch (InvalidGameStateException | InvalidShotException exception) {
                statusLabel.setText(exception.getMessage());
            } finally {
                machineTurnRunning = false;
                refreshBoards();

                if (currentGame.getPhase() == GamePhase.FINISHED) {
                    showFinishedMessage();
                } else if (currentGame.getCurrentTurn() == Turn.MACHINE) {
                    playMachineTurn();
                }
            }
        });
        pause.play();
    }

    private void showFinishedMessage() {
        if (currentGame.getMachinePlayer().hasLost()) {
            statusLabel.setText("Ganaste la partida.");
        } else if (currentGame.getHumanPlayer().hasLost()) {
            statusLabel.setText("Perdiste la partida.");
        }
    }

    private String describeShotResult(ShotResult result) {
        return switch (result) {
            case WATER -> "agua";
            case HIT -> "tocado";
            case SUNK -> "hundido";
            case WIN -> "victoria";
        };
    }

    private String formatCoordinate(Coordinate coordinate) {
        return "(" + (coordinate.getRow() + 1) + "," + (coordinate.getColumn() + 1) + ")";
    }

    private record ShipPlacement(Ship ship, Coordinate coordinate, Orientation orientation) {
    }
}
