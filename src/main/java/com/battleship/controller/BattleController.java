package com.battleship.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
import com.battleship.model.ship.AircraftCarrier;
import com.battleship.model.ship.Destroyer;
import com.battleship.model.ship.Frigate;
import com.battleship.model.ship.Ship;
import com.battleship.model.ship.Submarine;
import com.battleship.persistence.GameStatePersistenceService;
import com.battleship.persistence.PlayerStatsFileService;
import com.battleship.service.GameService;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * Controller for the battle screen.
 *
 * 
 * This controller connects the JavaFX view with the game model and services.
 * It handles ship selection, ship placement, ship movement, right-click
 * rotation, manual placement confirmation, shots, machine turns, autosave, and
 * navigation back to the initial screen.
 * 
 *
 * 
 * The machine turn is executed through a JavaFX {@link Task}, which runs the
 * game logic in a background thread and updates the user interface when the
 * task
 * finishes.
 * 
 */
public class BattleController {

    private static final String MAIN_VIEW_PATH = "/com/battleship/view/main-view.fxml";
    private static final String SHIPS_SPRITE_PATH = "/com/battleship/sprites/ships.png";
    private static final String SHOTS_SPRITE_PATH = "/com/battleship/sprites/shoots_states.png";

    private static final Path GAME_SAVE_PATH = Path.of("data", "game-state.ser");
    private static final Path PLAYER_STATS_PATH = Path.of("data", "player-stats.txt");

    private static final double SHOT_FRAME_WIDTH = 512;
    private static final double SHOT_FRAME_HEIGHT = 1024;

    private static final String DRAG_MOVE_MARKER = "MOVE";
    private static final long MACHINE_TURN_DELAY_MILLIS = 650;
    private static final int CELL_SIZE = 34;
    private static final int RANDOM_PLACEMENT_ATTEMPTS = 300;

    private final GameService gameService;
    private final GameStatePersistenceService gameStatePersistenceService;
    private final PlayerStatsFileService playerStatsFileService;
    private final Map<ShipType, Integer> remainingShips;
    private final Random random;

    private Game currentGame;
    private Image shipsSprite;
    private Image shotsSprite;
    private Orientation currentOrientation;
    private ShipType selectedShipType;
    private Ship draggedShip;
    private boolean machineTurnRunning;
    private boolean showMachineShips;

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

    @FXML
    private VBox shipSelectionBox;

    @FXML
    private VBox shipPanel;

    @FXML
    private Button confirmPlacementButton;

    @FXML
    private Button saveGameButton;

    @FXML
    private CheckBox showMachineShipsCheckBox;

    /**
     * Creates the battle controller and initializes local services and state.
     */
    public BattleController() {
        this.gameService = new GameService();
        this.gameStatePersistenceService = new GameStatePersistenceService();
        this.playerStatsFileService = new PlayerStatsFileService();
        this.remainingShips = new EnumMap<>(ShipType.class);
        this.random = new Random();
        this.currentOrientation = Orientation.HORIZONTAL;
        this.showMachineShips = false;
    }

    /**
     * Initializes the battle screen with a created or loaded game.
     *
     * @param game game received from the initial screen.
     */
    public void initializeGame(Game game) {
        this.currentGame = game;
        this.shipsSprite = new Image(getClass().getResourceAsStream(SHIPS_SPRITE_PATH));
        this.shotsSprite = new Image(getClass().getResourceAsStream(SHOTS_SPRITE_PATH));

        if (currentGame.getPhase() == GamePhase.PLACEMENT) {
            currentGame.setPhase(GamePhase.PLAYER_POSITIONING_SHIPS);
        }

        if (currentGame.getMachinePlayer().getFleet().isEmpty()) {
            placeMachineFleet();
        }

        loadInitialFleetCounts();

        playerInfoLabel.setText(
                "Jugador: " + currentGame.getHumanPlayer().getNickname()
                        + " | Oponente: " + currentGame.getMachinePlayer().getNickname());

        if (currentGame.getPhase() == GamePhase.PLAYER_POSITIONING_SHIPS) {
            statusLabel.setText("Selecciona una nave, click derecho para rotar y click en el tablero para ubicar.");
        } else {
            statusLabel.setText("Partida cargada correctamente.");
        }

        refreshView();
    }

    /**
     * Handles the checkbox used to reveal or hide the machine fleet during the
     * placement phase.
     */
    @FXML
    private void onShowMachineShipsToggled() {
        showMachineShips = showMachineShipsCheckBox.isSelected();
        refreshView();
    }

    /**
     * Confirms the human fleet placement and starts the battle.
     */
    @FXML
    private void onConfirmPlacementClicked() {
        if (currentGame == null) {
            statusLabel.setText("No hay partida activa.");
            return;
        }

        if (currentGame.getPhase() != GamePhase.PLAYER_POSITIONING_SHIPS) {
            statusLabel.setText("La selección de naves ya fue confirmada.");
            return;
        }

        if (!allHumanShipsPlaced()) {
            statusLabel.setText("Debes ubicar todas tus naves antes de confirmar.");
            return;
        }

        try {
            gameService.startGame(currentGame);
            selectedShipType = null;
            statusLabel.setText("Selección confirmada. Dispara en el territorio enemigo.");
            autosaveGameState();
            refreshView();
        } catch (InvalidGameStateException exception) {
            statusLabel.setText("No fue posible iniciar la batalla: " + exception.getMessage());
        }
    }

    /**
     * Saves the current game state using serialization and stores basic player
     * statistics in a flat file.
     */
    @FXML
    private void onSaveGameClicked() {
        if (currentGame == null) {
            statusLabel.setText("No hay partida activa para guardar.");
            return;
        }

        try {
            if (currentGame.getPhase() == GamePhase.FINISHED) {
                playerStatsFileService.savePlayerStats(currentGame, PLAYER_STATS_PATH);
                Files.deleteIfExists(GAME_SAVE_PATH);
                statusLabel.setText("La partida finalizada fue registrada y no quedará como partida cargable.");
                return;
            }

            gameStatePersistenceService.saveGame(currentGame, GAME_SAVE_PATH);
            playerStatsFileService.savePlayerStats(currentGame, PLAYER_STATS_PATH);
            statusLabel.setText("Partida guardada correctamente.");
        } catch (IOException exception) {
            statusLabel.setText("No fue posible guardar la partida.");
        }
    }

    /**
     * Returns to the initial screen.
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

    /**
     * Loads the number of remaining ships that can still be placed by the human
     * player.
     *
     * 
     * If the game was loaded from a saved file, already placed ships are
     * discounted from the initial fleet count.
     * 
     */
    private void loadInitialFleetCounts() {
        remainingShips.clear();

        remainingShips.put(ShipType.AIRCRAFT_CARRIER, 1);
        remainingShips.put(ShipType.SUBMARINE, 2);
        remainingShips.put(ShipType.DESTROYER, 3);
        remainingShips.put(ShipType.FRIGATE, 4);

        if (currentGame == null) {
            return;
        }

        for (Ship ship : currentGame.getHumanPlayer().getFleet()) {
            ShipType shipType = ship.getType();
            int currentCount = remainingShips.getOrDefault(shipType, 0);
            remainingShips.put(shipType, Math.max(0, currentCount - 1));
        }
    }

    /**
     * Refreshes labels, boards, panels, checkboxes, and button states.
     */
    private void refreshView() {
        phaseInfoLabel.setText(
                "Fase actual: " + describePhase(currentGame.getPhase())
                        + " | Turno: " + currentGame.getCurrentTurn());

        buildBoard(humanBoardGrid, currentGame.getHumanPlayer().getBoard(), "human-board-cell", true, false);

        boolean revealMachineShips = showMachineShips && currentGame.getPhase() == GamePhase.PLAYER_POSITIONING_SHIPS;
        buildBoard(machineBoardGrid, currentGame.getMachinePlayer().getBoard(), "machine-board-cell",
                revealMachineShips,
                true);

        buildShipSelection();

        boolean positioningShips = currentGame.getPhase() == GamePhase.PLAYER_POSITIONING_SHIPS;

        shipPanel.setVisible(positioningShips);
        shipPanel.setManaged(positioningShips);

        confirmPlacementButton.setDisable(!positioningShips || !allHumanShipsPlaced());
        saveGameButton.setDisable(currentGame == null);
        showMachineShipsCheckBox.setDisable(!positioningShips);
        showMachineShipsCheckBox.setVisible(positioningShips);
        showMachineShipsCheckBox.setManaged(positioningShips);

        machineBoardGrid.setDisable(
                currentGame.getPhase() != GamePhase.IN_PROGRESS
                        || currentGame.getCurrentTurn() != Turn.HUMAN
                        || machineTurnRunning);
    }

    /**
     * Rebuilds the list of ships available for placement.
     */
    private void buildShipSelection() {
        shipSelectionBox.getChildren().clear();

        for (ShipType shipType : ShipType.values()) {
            HBox row = createShipOption(shipType);
            shipSelectionBox.getChildren().add(row);
        }
    }

    /**
     * Creates a visual row for one ship type in the available ships panel.
     *
     * @param shipType ship type represented by the row.
     * @return row with sprite preview and remaining count.
     */
    private HBox createShipOption(ShipType shipType) {
        int remaining = remainingShips.getOrDefault(shipType, 0);

        ImageView shipImage = new ImageView(shipsSprite);
        configureShipPreview(shipImage, shipType);

        Label label = new Label(shipType.getDisplayName() + " x" + remaining);

        if (shipType == selectedShipType) {
            label.setText(
                    shipType.getDisplayName()
                            + " x"
                            + remaining
                            + " | "
                            + describeOrientation(currentOrientation));
        }

        label.getStyleClass().add("normal-label");

        HBox row = new HBox(10, shipImage, label);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6));
        row.getStyleClass().add("ship-option");

        if (shipType == selectedShipType) {
            row.getStyleClass().add("selected-ship-option");
        }

        if (remaining > 0 && currentGame.getPhase() == GamePhase.PLAYER_POSITIONING_SHIPS) {
            row.setOnMouseClicked(event -> handleShipOptionClick(event, shipType));
        } else {
            row.setDisable(true);
        }

        return row;
    }

    /**
     * Handles left-click and right-click actions on a ship option.
     *
     * @param event    mouse event.
     * @param shipType ship type selected in the panel.
     */
    private void handleShipOptionClick(MouseEvent event, ShipType shipType) {
        if (currentGame.getPhase() != GamePhase.PLAYER_POSITIONING_SHIPS) {
            return;
        }

        if (event.getButton() == MouseButton.PRIMARY) {
            selectedShipType = shipType;
            statusLabel.setText(
                    "Seleccionaste " + shipType.getDisplayName()
                            + ". Click derecho para rotar o click en el tablero para ubicar.");

            refreshView();
            event.consume();
            return;
        }

        if (event.getButton() == MouseButton.SECONDARY) {
            selectedShipType = shipType;
            rotateSelectedShip();
            event.consume();
        }
    }

    /**
     * Rotates the currently selected ship type before placement.
     */
    private void rotateSelectedShip() {
        if (selectedShipType == null) {
            statusLabel.setText("Selecciona una nave para rotarla.");
            return;
        }

        toggleCurrentOrientation();

        statusLabel.setText(
                "Orientación actual: " + describeOrientation(currentOrientation)
                        + ". Nave seleccionada: " + selectedShipType.getDisplayName() + ".");

        refreshView();
    }

    /**
     * Toggles the current orientation between horizontal and vertical.
     */
    private void toggleCurrentOrientation() {
        currentOrientation = currentOrientation == Orientation.HORIZONTAL
                ? Orientation.VERTICAL
                : Orientation.HORIZONTAL;
    }

    /**
     * Configures the ship image displayed in the available ships panel.
     *
     * @param shipImage image view to configure.
     * @param shipType  ship type used to select the sprite viewport.
     */
    private void configureShipPreview(ImageView shipImage, ShipType shipType) {
        shipImage.setViewport(getSpriteViewport(shipType));
        shipImage.setPreserveRatio(true);
        shipImage.setFitWidth(getShipPreviewWidth(shipType));
        shipImage.setRotate(0);
    }

    /**
     * Builds a board grid with coordinate headers, cells, ship sprites, and shot
     * markers.
     *
     * @param boardGrid      grid pane where the board is rendered.
     * @param board          model board to render.
     * @param cellStyleClass base CSS class for the board cells.
     * @param revealShips    whether ships should be visible.
     * @param enemyBoard     whether this board belongs to the machine.
     */
    private void buildBoard(
            GridPane boardGrid,
            Board board,
            String cellStyleClass,
            boolean revealShips,
            boolean enemyBoard) {

        boardGrid.getChildren().clear();

        addColumnHeaders(boardGrid);
        addRowsAndCells(boardGrid, board, cellStyleClass, revealShips, enemyBoard);

        if (revealShips) {
            overlayShipSprites(boardGrid, board);
        }

        overlayShotMarkers(boardGrid, board);
    }

    /**
     * Adds column headers from A to J.
     *
     * @param boardGrid grid where headers are added.
     */
    private void addColumnHeaders(GridPane boardGrid) {
        for (int column = 0; column < Board.DEFAULT_SIZE; column++) {
            Label headerLabel = new Label(String.valueOf((char) ('A' + column)));
            headerLabel.getStyleClass().add("board-header-label");

            StackPane headerCell = createHeaderCell(headerLabel);
            boardGrid.add(headerCell, column + 1, 0);
        }
    }

    /**
     * Adds row headers and board cells.
     *
     * @param boardGrid      grid where rows and cells are added.
     * @param board          model board to render.
     * @param cellStyleClass base CSS class for cells.
     * @param revealShips    whether ships should be visible.
     * @param enemyBoard     whether this board belongs to the machine.
     */
    private void addRowsAndCells(
            GridPane boardGrid,
            Board board,
            String cellStyleClass,
            boolean revealShips,
            boolean enemyBoard) {

        for (int row = 0; row < Board.DEFAULT_SIZE; row++) {
            Label rowLabel = new Label(String.valueOf(row + 1));
            rowLabel.getStyleClass().add("board-header-label");

            StackPane rowHeaderCell = createHeaderCell(rowLabel);
            boardGrid.add(rowHeaderCell, 0, row + 1);

            for (int column = 0; column < Board.DEFAULT_SIZE; column++) {
                Coordinate coordinate = new Coordinate(row, column);
                StackPane cell = createBoardCell(board.getCell(coordinate), cellStyleClass, revealShips, enemyBoard);
                boardGrid.add(cell, column + 1, row + 1);
            }
        }
    }

    /**
     * Creates a fixed-size header cell.
     *
     * @param label label displayed inside the header cell.
     * @return visual header cell.
     */
    private StackPane createHeaderCell(Label label) {
        StackPane headerCell = new StackPane(label);
        headerCell.setPrefSize(CELL_SIZE, CELL_SIZE);
        headerCell.setMinSize(CELL_SIZE, CELL_SIZE);
        headerCell.setMaxSize(CELL_SIZE, CELL_SIZE);
        headerCell.setAlignment(Pos.CENTER);
        return headerCell;
    }

    /**
     * Creates a visual cell for one board coordinate.
     *
     * @param boardCell      model cell.
     * @param cellStyleClass base CSS class for the cell.
     * @param revealShips    whether ships should be visible.
     * @param enemyBoard     whether the cell belongs to the enemy board.
     * @return visual board cell.
     */
    private StackPane createBoardCell(Cell boardCell, String cellStyleClass, boolean revealShips, boolean enemyBoard) {
        StackPane visualCell = new StackPane();

        visualCell.setPrefSize(CELL_SIZE, CELL_SIZE);
        visualCell.setMinSize(CELL_SIZE, CELL_SIZE);
        visualCell.setMaxSize(CELL_SIZE, CELL_SIZE);
        visualCell.setAlignment(Pos.CENTER);

        visualCell.getStyleClass().add("board-cell");
        visualCell.getStyleClass().add(cellStyleClass);
        visualCell.getStyleClass().add(resolveCellStateStyle(boardCell, revealShips));

        Coordinate coordinate = boardCell.getCoordinate();

        if (currentGame.getPhase() == GamePhase.PLAYER_POSITIONING_SHIPS && !enemyBoard) {
            configurePlacementDrop(visualCell, coordinate);
            configurePlacementClick(visualCell, coordinate, boardCell);

            if (boardCell.hasShip()) {
                configureBoardShipDrag(visualCell, boardCell.getShip());
            }
        }

        if (enemyBoard) {
            visualCell.setOnMouseClicked(event -> onEnemyCellClicked(coordinate));
        }

        return visualCell;
    }

    /**
     * Configures click-based placement and rotation on a human board cell.
     *
     * @param visualCell cell displayed in the JavaFX grid.
     * @param coordinate coordinate represented by the cell.
     * @param boardCell  model cell represented by the visual cell.
     */
    private void configurePlacementClick(StackPane visualCell, Coordinate coordinate, Cell boardCell) {
        visualCell.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                placeSelectedShip(coordinate);
                event.consume();
                return;
            }

            if (event.getButton() == MouseButton.SECONDARY) {
                if (boardCell.hasShip()) {
                    rotatePlacedShipWithRightClick(event, boardCell.getShip());
                } else {
                    rotateSelectedShip();
                    event.consume();
                }
            }
        });
    }

    /**
     * Keeps drag-and-drop support for moving already placed ships.
     *
     * @param visualCell cell that accepts the drop.
     * @param coordinate destination coordinate.
     */
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

    /**
     * Configures drag behavior for a ship that is already placed on the board.
     *
     * @param visualCell visual cell that starts the drag.
     * @param ship       ship to move.
     */
    private void configureBoardShipDrag(StackPane visualCell, Ship ship) {
        visualCell.setOnDragDetected(event -> startBoardShipDrag(event, visualCell, ship));
        visualCell.setOnDragDone(event -> {
            draggedShip = null;
            event.consume();
        });
    }

    /**
     * Places the currently selected ship type on a coordinate.
     *
     * @param coordinate coordinate where the ship should start.
     * @return true if the ship was placed; false otherwise.
     */
    private boolean placeSelectedShip(Coordinate coordinate) {
        if (selectedShipType == null) {
            statusLabel.setText("Primero selecciona una nave disponible.");
            return false;
        }

        if (remainingShips.getOrDefault(selectedShipType, 0) <= 0) {
            statusLabel.setText("Ya no quedan naves de ese tipo.");
            selectedShipType = null;
            refreshView();
            return false;
        }

        try {
            gameService.placeShip(
                    currentGame,
                    currentGame.getHumanPlayer(),
                    createShip(selectedShipType),
                    coordinate,
                    currentOrientation);

            remainingShips.put(selectedShipType, remainingShips.get(selectedShipType) - 1);

            statusLabel.setText(
                    selectedShipType.getDisplayName()
                            + " ubicado en "
                            + formatCoordinate(coordinate)
                            + ".");

            selectedShipType = null;

            finishPlacementIfReady();
            autosaveGameState();
            refreshView();

            return true;
        } catch (InvalidPlacementException | InvalidGameStateException exception) {
            statusLabel.setText("No puedes ubicar esa nave ahí.");
            return false;
        }
    }

    /**
     * Rotates a ship that is already placed on the board.
     *
     * @param event mouse event that triggered the rotation.
     * @param ship  ship to rotate.
     */
    private void rotatePlacedShipWithRightClick(MouseEvent event, Ship ship) {
        if (event.getButton() != MouseButton.SECONDARY) {
            return;
        }

        if (currentGame.getPhase() != GamePhase.PLAYER_POSITIONING_SHIPS) {
            return;
        }

        if (ship.getPositions().isEmpty()) {
            return;
        }

        Orientation newOrientation = ship.getOrientation() == Orientation.HORIZONTAL
                ? Orientation.VERTICAL
                : Orientation.HORIZONTAL;

        Coordinate startCoordinate = ship.getPositions().get(0);

        try {
            gameService.moveShip(
                    currentGame,
                    currentGame.getHumanPlayer(),
                    ship,
                    startCoordinate,
                    newOrientation);

            currentOrientation = newOrientation;
            statusLabel.setText(ship.getDisplayName() + " rotado a " + describeOrientation(newOrientation) + ".");
            autosaveGameState();
            refreshView();
        } catch (InvalidPlacementException | InvalidGameStateException exception) {
            statusLabel.setText("No se puede rotar esa nave en esa posición.");
        }

        event.consume();
    }

    /**
     * Starts dragging a placed ship so it can be moved to another coordinate.
     *
     * @param event  mouse event.
     * @param source visual source of the drag.
     * @param ship   ship being dragged.
     */
    private void startBoardShipDrag(MouseEvent event, StackPane source, Ship ship) {
        draggedShip = ship;

        Dragboard dragboard = source.startDragAndDrop(TransferMode.MOVE);

        ClipboardContent content = new ClipboardContent();
        content.putString(DRAG_MOVE_MARKER);
        dragboard.setContent(content);

        SnapshotParameters snapshotParameters = new SnapshotParameters();
        snapshotParameters.setFill(Color.TRANSPARENT);

        dragboard.setDragView(createShipSpriteContainer(ship).snapshot(snapshotParameters, null));

        event.consume();
    }

    /**
     * Handles a drop event for a dragged placed ship.
     *
     * @param event      drag event.
     * @param coordinate destination coordinate.
     * @return true if the movement was completed; false otherwise.
     */
    private boolean placeDraggedShip(DragEvent event, Coordinate coordinate) {
        Dragboard dragboard = event.getDragboard();

        if (!dragboard.hasString() || currentGame.getPhase() != GamePhase.PLAYER_POSITIONING_SHIPS) {
            return false;
        }

        String payload = dragboard.getString();

        if (DRAG_MOVE_MARKER.equals(payload)) {
            return moveDraggedShip(coordinate);
        }

        return false;
    }

    /**
     * Moves the currently dragged ship to a new coordinate.
     *
     * @param coordinate destination coordinate.
     * @return true if the ship was moved; false otherwise.
     */
    private boolean moveDraggedShip(Coordinate coordinate) {
        if (draggedShip == null) {
            return false;
        }

        try {
            gameService.moveShip(
                    currentGame,
                    currentGame.getHumanPlayer(),
                    draggedShip,
                    coordinate,
                    draggedShip.getOrientation());

            statusLabel.setText(draggedShip.getDisplayName() + " reubicado en " + formatCoordinate(coordinate) + ".");
            autosaveGameState();
            refreshView();

            return true;
        } catch (InvalidPlacementException | InvalidGameStateException exception) {
            statusLabel.setText("No puedes reubicar esa nave ahí.");
            return false;
        }
    }

    /**
     * Updates the status label after all human ships have been placed.
     */
    private void finishPlacementIfReady() {
        if (!allHumanShipsPlaced()) {
            return;
        }

        statusLabel.setText("Todas tus naves están ubicadas. Puedes acomodarlas o confirmar la selección.");
    }

    /**
     * Checks whether all human ships have already been placed.
     *
     * @return true if there are no remaining ships to place.
     */
    private boolean allHumanShipsPlaced() {
        return remainingShips.values().stream().allMatch(count -> count == 0);
    }

    /**
     * Randomly places the complete machine fleet.
     *
     * 
     * This method is used before the battle starts and allows the opponent
     * board to be different in each new match.
     * 
     */
    private void placeMachineFleet() {
        List<Ship> machineFleet = List.of(
                new AircraftCarrier(),
                new Submarine(),
                new Submarine(),
                new Destroyer(),
                new Destroyer(),
                new Destroyer(),
                new Frigate(),
                new Frigate(),
                new Frigate(),
                new Frigate());

        for (Ship ship : machineFleet) {
            placeMachineShipRandomly(ship);
        }
    }

    /**
     * Attempts to place one machine ship in a random valid position.
     *
     * @param ship ship to place.
     * @throws InvalidPlacementException if a valid random position cannot be found.
     */
    private void placeMachineShipRandomly(Ship ship) {
        for (int attempt = 0; attempt < RANDOM_PLACEMENT_ATTEMPTS; attempt++) {
            Coordinate coordinate = new Coordinate(
                    random.nextInt(Board.DEFAULT_SIZE),
                    random.nextInt(Board.DEFAULT_SIZE));

            Orientation orientation = random.nextBoolean() ? Orientation.HORIZONTAL : Orientation.VERTICAL;

            try {
                gameService.placeShip(
                        currentGame,
                        currentGame.getMachinePlayer(),
                        ship,
                        coordinate,
                        orientation);
                return;
            } catch (InvalidPlacementException | InvalidGameStateException exception) {
                // Try another random position.
            }
        }

        throw new InvalidPlacementException("No fue posible ubicar aleatoriamente la flota de la máquina.");
    }

    /**
     * Creates a concrete ship instance from a ship type.
     *
     * @param shipType type of ship to create.
     * @return concrete ship instance.
     */
    private Ship createShip(ShipType shipType) {
        return switch (shipType) {
            case AIRCRAFT_CARRIER -> new AircraftCarrier();
            case SUBMARINE -> new Submarine();
            case DESTROYER -> new Destroyer();
            case FRIGATE -> new Frigate();
        };
    }

    /**
     * Resolves the CSS class that represents a cell state.
     *
     * @param cell        model cell.
     * @param revealShips whether ships are visible on the board.
     * @return CSS class for the cell state.
     */
    private String resolveCellStateStyle(Cell cell, boolean revealShips) {
        CellState state = cell.getState();

        if (state == CellState.WATER) {
            return revealShips ? "cell-empty" : "cell-water";
        }

        if (state == CellState.HIT) {
            return revealShips ? "cell-empty" : "cell-hit";
        }

        if (state == CellState.SUNK) {
            return revealShips ? "cell-empty" : "cell-sunk";
        }

        if (state == CellState.SHIP && revealShips) {
            return "cell-empty";
        }

        return "cell-empty";
    }

    /**
     * Draws all ship sprites that should be visible on a board.
     *
     * @param boardGrid grid where sprites are added.
     * @param board     board used to find ships.
     */
    private void overlayShipSprites(GridPane boardGrid, Board board) {
        Set<Ship> ships = new LinkedHashSet<>();

        for (Cell cell : board.getCells().values()) {
            if (cell.hasShip()) {
                ships.add(cell.getShip());
            }
        }

        for (Ship ship : ships) {
            addShipSpriteToGrid(boardGrid, ship);
        }
    }

    /**
     * Adds one ship sprite to the board grid.
     *
     * @param boardGrid grid where the sprite is added.
     * @param ship      ship represented by the sprite.
     */
    private void addShipSpriteToGrid(GridPane boardGrid, Ship ship) {
        List<Coordinate> positions = ship.getPositions();

        if (positions.isEmpty()) {
            return;
        }

        Coordinate start = positions.get(0);
        StackPane shipContainer = createShipSpriteContainer(ship);

        boardGrid.add(shipContainer, start.getColumn() + 1, start.getRow() + 1);

        if (ship.getOrientation() == Orientation.HORIZONTAL) {
            GridPane.setColumnSpan(shipContainer, ship.getSize());
        } else {
            GridPane.setRowSpan(shipContainer, ship.getSize());
        }
    }

    /**
     * Creates a visual container for a ship sprite.
     *
     * @param ship ship represented by the container.
     * @return visual ship container.
     */
    private StackPane createShipSpriteContainer(Ship ship) {
        int span = ship.getSize();
        boolean horizontal = ship.getOrientation() == Orientation.HORIZONTAL;

        double containerWidth = horizontal ? span * CELL_SIZE : CELL_SIZE;
        double containerHeight = horizontal ? CELL_SIZE : span * CELL_SIZE;

        StackPane container = new StackPane();
        container.setPrefSize(containerWidth, containerHeight);
        container.setMinSize(containerWidth, containerHeight);
        container.setMaxSize(containerWidth, containerHeight);
        container.setAlignment(Pos.CENTER);
        container.setClip(new Rectangle(containerWidth, containerHeight));

        ImageView shipImage = new ImageView(shipsSprite);
        shipImage.setViewport(getSpriteViewport(ship.getType()));
        shipImage.setPreserveRatio(true);

        if (horizontal) {
            shipImage.setFitWidth(containerWidth - 2);
            shipImage.setFitHeight(containerHeight - 2);
            shipImage.setRotate(0);
        } else {
            shipImage.setFitWidth(containerHeight - 2);
            shipImage.setFitHeight(containerWidth - 2);
            shipImage.setRotate(90);
        }

        container.getChildren().add(shipImage);
        container.setMouseTransparent(true);

        return container;
    }

    /**
     * Draws shot markers on top of water, hit, and sunk cells.
     *
     * @param boardGrid grid where markers are added.
     * @param board     board used to find shot cells.
     */
    private void overlayShotMarkers(GridPane boardGrid, Board board) {
        for (Cell cell : board.getCells().values()) {
            CellState state = cell.getState();

            if (state == CellState.WATER || state == CellState.HIT || state == CellState.SUNK) {
                Coordinate coordinate = cell.getCoordinate();

                StackPane marker = new StackPane();
                marker.setPrefSize(CELL_SIZE, CELL_SIZE);
                marker.setMinSize(CELL_SIZE, CELL_SIZE);
                marker.setMaxSize(CELL_SIZE, CELL_SIZE);
                marker.setMouseTransparent(true);

                double scale = 2.5;

                ImageView shotImage = new ImageView(shotsSprite);
                shotImage.setViewport(getShotSpriteViewport(state));
                shotImage.setPreserveRatio(true);
                shotImage.setFitWidth(CELL_SIZE * scale);
                shotImage.setFitHeight(CELL_SIZE * scale);
                shotImage.setMouseTransparent(true);

                marker.getChildren().add(shotImage);
                boardGrid.add(marker, coordinate.getColumn() + 1, coordinate.getRow() + 1);
            }
        }
    }

    /**
     * Handles human clicks on the enemy board.
     *
     * @param coordinate enemy coordinate selected by the human player.
     */
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

            autosaveGameState();
            refreshView();
            handleEndOrMachineTurn();
        } catch (InvalidGameStateException | InvalidShotException exception) {
            statusLabel.setText(exception.getMessage());
        }
    }

    /**
     * Decides whether the game ended or whether the machine must play.
     */
    private void handleEndOrMachineTurn() {
        if (currentGame.getPhase() == GamePhase.FINISHED) {
            showFinishedMessage();
            autosaveGameState();
            refreshView();
            return;
        }

        if (currentGame.getCurrentTurn() == Turn.MACHINE) {
            playMachineTurn();
        }
    }

    /**
     * Executes the machine turn in a background thread using a JavaFX Task.
     *
     * 
     * The task waits briefly to simulate thinking time, executes the machine
     * shot in the model, and then updates the UI when the task succeeds.
     * 
     */
    private void playMachineTurn() {
        machineTurnRunning = true;
        machineBoardGrid.setDisable(true);
        statusLabel.setText("La máquina está pensando...");

        Task<MachineShotResult> machineTurnTask = new Task<>() {
            @Override
            protected MachineShotResult call() throws Exception {
                Thread.sleep(MACHINE_TURN_DELAY_MILLIS);

                ShotResult result = gameService.machineShoots(currentGame);
                Coordinate coordinate = currentGame.getShotHistory().get(0).getCoordinate();

                return new MachineShotResult(coordinate, result);
            }
        };

        machineTurnTask.setOnSucceeded(event -> handleMachineTurnSuccess(machineTurnTask.getValue()));
        machineTurnTask.setOnFailed(event -> handleMachineTurnFailure(machineTurnTask.getException()));

        Thread machineThread = new Thread(machineTurnTask, "machine-turn-thread");
        machineThread.setDaemon(true);
        machineThread.start();
    }

    /**
     * Handles a successful machine turn.
     *
     * @param machineShotResult result produced by the background task.
     */
    private void handleMachineTurnSuccess(MachineShotResult machineShotResult) {
        statusLabel.setText(
                "La máquina disparó en "
                        + formatCoordinate(machineShotResult.coordinate())
                        + ": "
                        + describeShotResult(machineShotResult.result()));

        machineTurnRunning = false;
        autosaveGameState();
        refreshView();

        if (currentGame.getPhase() == GamePhase.FINISHED) {
            showFinishedMessage();
            autosaveGameState();
        } else if (currentGame.getCurrentTurn() == Turn.MACHINE) {
            playMachineTurn();
        }
    }

    /**
     * Handles an error produced while the machine turn task was running.
     *
     * @param exception exception produced by the task.
     */
    private void handleMachineTurnFailure(Throwable exception) {
        if (exception != null && exception.getMessage() != null) {
            statusLabel.setText(exception.getMessage());
        } else {
            statusLabel.setText("No fue posible ejecutar el turno de la máquina.");
        }

        machineTurnRunning = false;
        refreshView();
    }

    /**
     * Shows the final message when the game ends.
     */
    private void showFinishedMessage() {
        if (currentGame.getMachinePlayer().hasLost()) {
            statusLabel.setText("Ganaste la partida.");
        } else if (currentGame.getHumanPlayer().hasLost()) {
            statusLabel.setText("Perdiste la partida.");
        }
    }

    /**
     * Saves the current game without interrupting the user flow.
     */
    private void autosaveGameState() {
        if (currentGame == null) {
            return;
        }

        try {
            if (currentGame.getPhase() == GamePhase.FINISHED) {
                playerStatsFileService.savePlayerStats(currentGame, PLAYER_STATS_PATH);
                Files.deleteIfExists(GAME_SAVE_PATH);
                return;
            }

            gameStatePersistenceService.saveGame(currentGame, GAME_SAVE_PATH);
        } catch (IOException exception) {
            // Autosave should not break the current game flow.
        }
    }

    /**
     * Returns the sprite viewport used by each ship type.
     *
     * @param shipType ship type.
     * @return rectangle used to crop the ship sprite.
     */
    private Rectangle2D getSpriteViewport(ShipType shipType) {
        return switch (shipType) {
            case AIRCRAFT_CARRIER -> new Rectangle2D(405, 28, 705, 200);
            case SUBMARINE -> new Rectangle2D(532, 276, 414, 114);
            case DESTROYER -> new Rectangle2D(483, 608, 210, 92);
            case FRIGATE -> new Rectangle2D(508, 910, 76, 48);
        };
    }

    /**
     * Returns the sprite viewport used by each shot state.
     *
     * @param state cell state.
     * @return rectangle used to crop the shot sprite.
     */
    private Rectangle2D getShotSpriteViewport(CellState state) {
        return switch (state) {
            case WATER -> new Rectangle2D(0, 0, SHOT_FRAME_WIDTH, SHOT_FRAME_HEIGHT);
            case HIT -> new Rectangle2D(SHOT_FRAME_WIDTH, 0, SHOT_FRAME_WIDTH, SHOT_FRAME_HEIGHT);
            case SUNK -> new Rectangle2D(SHOT_FRAME_WIDTH * 2, 0, SHOT_FRAME_WIDTH, SHOT_FRAME_HEIGHT);
            default -> null;
        };
    }

    /**
     * Returns the preview width used by each ship type in the side panel.
     *
     * @param shipType ship type.
     * @return preview width.
     */
    private double getShipPreviewWidth(ShipType shipType) {
        return switch (shipType) {
            case AIRCRAFT_CARRIER -> 95;
            case SUBMARINE -> 85;
            case DESTROYER -> 75;
            case FRIGATE -> 60;
        };
    }

    /**
     * Converts a shot result into user-facing text.
     *
     * @param result shot result.
     * @return readable shot result.
     */
    private String describeShotResult(ShotResult result) {
        return switch (result) {
            case WATER -> "agua";
            case HIT -> "tocado";
            case SUNK -> "hundido";
            case WIN -> "victoria";
        };
    }

    /**
     * Converts an orientation into user-facing text.
     *
     * @param orientation orientation to describe.
     * @return readable orientation.
     */
    private String describeOrientation(Orientation orientation) {
        return orientation == Orientation.HORIZONTAL ? "Horizontal" : "Vertical";
    }

    /**
     * Converts a game phase into user-facing text.
     *
     * @param phase phase to describe.
     * @return readable phase.
     */
    private String describePhase(GamePhase phase) {
        return switch (phase) {
            case PLACEMENT -> "Colocación";
            case PLAYER_POSITIONING_SHIPS -> "Jugador posicionando naves";
            case IN_PROGRESS -> "En progreso";
            case FINISHED -> "Finalizada";
        };
    }

    /**
     * Formats a coordinate using one-based numbers.
     *
     * @param coordinate coordinate to format.
     * @return readable coordinate.
     */
    private String formatCoordinate(Coordinate coordinate) {
        return "(" + (coordinate.getRow() + 1) + "," + (coordinate.getColumn() + 1) + ")";
    }

    /**
     * Small immutable value used to return the machine shot coordinate and
     * result from the background task.
     *
     * @param coordinate coordinate selected by the machine.
     * @param result     shot result.
     */
    private record MachineShotResult(Coordinate coordinate, ShotResult result) {
    }
}