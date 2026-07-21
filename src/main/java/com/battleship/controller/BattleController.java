package com.battleship.controller;

import java.io.IOException;
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
import com.battleship.model.player.Player;
import com.battleship.model.ship.AircraftCarrier;
import com.battleship.model.ship.Destroyer;
import com.battleship.model.ship.Frigate;
import com.battleship.model.ship.Ship;
import com.battleship.model.ship.Submarine;
import com.battleship.persistence.GameStatePersistenceService;
import com.battleship.persistence.PlayerStatsFileService;
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
import javafx.util.Duration;

/**
 * Controlador de la pantalla de batalla.
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
    private static final Duration MACHINE_TURN_DELAY = Duration.millis(650);
    private static final int CELL_SIZE = 34;

    private final GameService gameService;
    private final GameStatePersistenceService gameStatePersistenceService;
    private final PlayerStatsFileService playerStatsFileService;
    private final Map<ShipType, Integer> remainingShips;

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

    public BattleController() {
        this.gameService = new GameService();
        this.gameStatePersistenceService = new GameStatePersistenceService();
        this.playerStatsFileService = new PlayerStatsFileService();
        this.remainingShips = new EnumMap<>(ShipType.class);
        this.currentOrientation = Orientation.HORIZONTAL;
        this.showMachineShips = false;
    }

    /**
     * Recibe la partida creada o cargada desde la pantalla inicial.
     *
     * @param game partida actual.
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
     * Muestra u oculta las naves del oponente en su tablero.
     */
    @FXML
    private void onShowMachineShipsToggled() {
        showMachineShips = showMachineShipsCheckBox.isSelected();
        refreshView();
    }

    /**
     * Confirma la ubicación de las naves del jugador e inicia la partida.
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
            refreshView();
        } catch (InvalidPlacementException | InvalidGameStateException exception) {
            statusLabel.setText("No fue posible iniciar la batalla: " + exception.getMessage());
        }
    }

    /**
     * Guarda la partida actual usando serialización y guarda estadísticas básicas
     * en archivo plano.
     */
    @FXML
    private void onSaveGameClicked() {
        if (currentGame == null) {
            statusLabel.setText("No hay partida activa para guardar.");
            return;
        }

        try {
            gameStatePersistenceService.saveGame(currentGame, GAME_SAVE_PATH);
            playerStatsFileService.savePlayerStats(currentGame, PLAYER_STATS_PATH);
            statusLabel.setText("Partida guardada correctamente.");
        } catch (IOException exception) {
            statusLabel.setText("No fue posible guardar la partida.");
        }
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
        showMachineShipsCheckBox.setDisable(!positioningShips);
        showMachineShipsCheckBox.setVisible(positioningShips);
        showMachineShipsCheckBox.setManaged(positioningShips);

        machineBoardGrid.setDisable(
                currentGame.getPhase() != GamePhase.IN_PROGRESS
                        || currentGame.getCurrentTurn() != Turn.HUMAN
                        || machineTurnRunning);
    }

    private void buildShipSelection() {
        shipSelectionBox.getChildren().clear();

        for (ShipType shipType : ShipType.values()) {
            HBox row = createShipOption(shipType);
            shipSelectionBox.getChildren().add(row);
        }
    }

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

    private void toggleCurrentOrientation() {
        currentOrientation = currentOrientation == Orientation.HORIZONTAL
                ? Orientation.VERTICAL
                : Orientation.HORIZONTAL;
    }

    private void configureShipPreview(ImageView shipImage, ShipType shipType) {
        shipImage.setViewport(getSpriteViewport(shipType));
        shipImage.setPreserveRatio(true);
        shipImage.setFitWidth(getShipPreviewWidth(shipType));
        shipImage.setRotate(0);
    }

    private void buildBoard(
            GridPane boardGrid,
            Board board,
            String cellStyleClass,
            boolean revealShips,
            boolean enemyBoard) {

        boardGrid.getChildren().clear();

        // Add column headers (A-J) at the top
        for (int column = 0; column < Board.DEFAULT_SIZE; column++) {
            Label headerLabel = new Label(String.valueOf((char) ('A' + column)));
            headerLabel.getStyleClass().add("board-header-label");
            StackPane headerCell = new StackPane(headerLabel);
            headerCell.setPrefSize(CELL_SIZE, CELL_SIZE);
            headerCell.setMinSize(CELL_SIZE, CELL_SIZE);
            headerCell.setMaxSize(CELL_SIZE, CELL_SIZE);
            headerCell.setAlignment(Pos.CENTER);
            boardGrid.add(headerCell, column + 1, 0);
        }

        // Add row numbers (1-10) on the left and fill the board cells
        for (int row = 0; row < Board.DEFAULT_SIZE; row++) {
            Label rowLabel = new Label(String.valueOf(row + 1));
            rowLabel.getStyleClass().add("board-header-label");
            StackPane rowHeaderCell = new StackPane(rowLabel);
            rowHeaderCell.setPrefSize(CELL_SIZE, CELL_SIZE);
            rowHeaderCell.setMinSize(CELL_SIZE, CELL_SIZE);
            rowHeaderCell.setMaxSize(CELL_SIZE, CELL_SIZE);
            rowHeaderCell.setAlignment(Pos.CENTER);
            boardGrid.add(rowHeaderCell, 0, row + 1);

            for (int column = 0; column < Board.DEFAULT_SIZE; column++) {
                Coordinate coordinate = new Coordinate(row, column);
                StackPane cell = createBoardCell(board.getCell(coordinate), cellStyleClass, revealShips, enemyBoard);
                boardGrid.add(cell, column + 1, row + 1);
            }
        }

        if (revealShips) {
            overlayShipSprites(boardGrid, board);
        }

        overlayShotMarkers(boardGrid, board);
    }

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

    private void configureBoardShipDrag(StackPane visualCell, Ship ship) {
        visualCell.setOnDragDetected(event -> startBoardShipDrag(event, visualCell, ship));
        visualCell.setOnDragDone(event -> {
            draggedShip = null;
            event.consume();
        });
    }

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
            refreshView();

            return true;
        } catch (InvalidPlacementException | InvalidGameStateException exception) {
            statusLabel.setText("No puedes ubicar esa nave ahí.");
            return false;
        }
    }

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
            refreshView();
        } catch (InvalidPlacementException | InvalidGameStateException exception) {
            statusLabel.setText("No se puede rotar esa nave en esa posición.");
        }

        event.consume();
    }

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
            refreshView();

            return true;
        } catch (InvalidPlacementException | InvalidGameStateException exception) {
            statusLabel.setText("No puedes reubicar esa nave ahí.");
            return false;
        }
    }

    private void finishPlacementIfReady() {
        if (!allHumanShipsPlaced()) {
            return;
        }

        statusLabel.setText("Todas tus naves están ubicadas. Puedes acomodarlas o confirmar la selección.");
    }

    private boolean allHumanShipsPlaced() {
        return remainingShips.values().stream().allMatch(count -> count == 0);
    }

    private void placeMachineFleet() {
        Random random = new Random();
        Player machinePlayer = currentGame.getMachinePlayer();
        Board machineBoard = machinePlayer.getBoard();

        List<Ship> shipsToPlace = new java.util.ArrayList<>();
        shipsToPlace.add(new AircraftCarrier());
        shipsToPlace.add(new Submarine());
        shipsToPlace.add(new Submarine());
        shipsToPlace.add(new Destroyer());
        shipsToPlace.add(new Destroyer());
        shipsToPlace.add(new Destroyer());
        shipsToPlace.add(new Frigate());
        shipsToPlace.add(new Frigate());
        shipsToPlace.add(new Frigate());
        shipsToPlace.add(new Frigate());

        for (Ship ship : shipsToPlace) {
            boolean placed = false;
            int attempts = 0;

            while (!placed && attempts < 1000) {
                int row = random.nextInt(Board.DEFAULT_SIZE);
                int col = random.nextInt(Board.DEFAULT_SIZE);
                Orientation orientation = random.nextBoolean()
                        ? Orientation.HORIZONTAL
                        : Orientation.VERTICAL;

                Coordinate startCoordinate = new Coordinate(row, col);

                try {
                    gameService.placeShip(
                            currentGame,
                            machinePlayer,
                            ship,
                            startCoordinate,
                            orientation);
                    placed = true;
                } catch (InvalidPlacementException | InvalidGameStateException e) {
                    attempts++;
                }
            }

            if (!placed) {
                throw new InvalidPlacementException(
                        "No fue posible ubicar aleatoriamente la flota de la máquina.");
            }
        }
    }

    private Ship createShip(ShipType shipType) {
        return switch (shipType) {
            case AIRCRAFT_CARRIER -> new AircraftCarrier();
            case SUBMARINE -> new Submarine();
            case DESTROYER -> new Destroyer();
            case FRIGATE -> new Frigate();
        };
    }

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

            refreshView();
            handleEndOrMachineTurn();
        } catch (InvalidGameStateException | InvalidShotException exception) {
            statusLabel.setText(exception.getMessage());
        }
    }

    private void handleEndOrMachineTurn() {
        if (currentGame.getPhase() == GamePhase.FINISHED) {
            showFinishedMessage();
            refreshView();
            return;
        }

        if (currentGame.getCurrentTurn() == Turn.MACHINE) {
            playMachineTurn();
        }
    }

    private void playMachineTurn() {
        machineTurnRunning = true;
        machineBoardGrid.setDisable(true);
        statusLabel.setText("Turno de la máquina...");

        PauseTransition pause = new PauseTransition(MACHINE_TURN_DELAY);

        pause.setOnFinished(event -> {
            try {
                ShotResult result = gameService.machineShoots(currentGame);
                Coordinate coordinate = currentGame.getShotHistory().get(0).getCoordinate();

                statusLabel.setText(
                        "La máquina disparó en " + formatCoordinate(coordinate) + ": " + describeShotResult(result));
            } catch (InvalidGameStateException | InvalidShotException exception) {
                statusLabel.setText(exception.getMessage());
            } finally {
                machineTurnRunning = false;
                refreshView();

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

    private Rectangle2D getSpriteViewport(ShipType shipType) {
        return switch (shipType) {
            case AIRCRAFT_CARRIER -> new Rectangle2D(405, 28, 705, 200);
            case SUBMARINE -> new Rectangle2D(532, 276, 414, 114);
            case DESTROYER -> new Rectangle2D(483, 608, 210, 92);
            case FRIGATE -> new Rectangle2D(508, 910, 76, 48);
        };
    }

    private Rectangle2D getShotSpriteViewport(CellState state) {
        return switch (state) {
            case WATER -> new Rectangle2D(0, 0, SHOT_FRAME_WIDTH, SHOT_FRAME_HEIGHT);
            case HIT -> new Rectangle2D(SHOT_FRAME_WIDTH, 0, SHOT_FRAME_WIDTH, SHOT_FRAME_HEIGHT);
            case SUNK -> new Rectangle2D(SHOT_FRAME_WIDTH * 2, 0, SHOT_FRAME_WIDTH, SHOT_FRAME_HEIGHT);
            default -> null;
        };
    }

    private double getShipPreviewWidth(ShipType shipType) {
        return switch (shipType) {
            case AIRCRAFT_CARRIER -> 95;
            case SUBMARINE -> 85;
            case DESTROYER -> 75;
            case FRIGATE -> 60;
        };
    }

    private String describeShotResult(ShotResult result) {
        return switch (result) {
            case WATER -> "agua";
            case HIT -> "tocado";
            case SUNK -> "hundido";
            case WIN -> "victoria";
        };
    }

    private String describeOrientation(Orientation orientation) {
        return orientation == Orientation.HORIZONTAL ? "Horizontal" : "Vertical";
    }

    private String describePhase(GamePhase phase) {
        return switch (phase) {
            case PLACEMENT -> "Colocación";
            case PLAYER_POSITIONING_SHIPS -> "Jugador posicionando naves";
            case IN_PROGRESS -> "En progreso";
            case FINISHED -> "Finalizada";
        };
    }

    private String formatCoordinate(Coordinate coordinate) {
        return "(" + (coordinate.getRow() + 1) + "," + (coordinate.getColumn() + 1) + ")";
    }

    private record ShipPlacement(Ship ship, Coordinate coordinate, Orientation orientation) {
    }
}