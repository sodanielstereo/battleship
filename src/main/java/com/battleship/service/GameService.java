package com.battleship.service;

import com.battleship.exception.InvalidGameStateException;
import com.battleship.exception.InvalidPlacementException;
import com.battleship.model.Game;
import com.battleship.model.board.Coordinate;
import com.battleship.model.enums.GamePhase;
import com.battleship.model.enums.Orientation;
import com.battleship.model.enums.ShotResult;
import com.battleship.model.enums.Turn;
import com.battleship.model.history.ShotRecord;
import com.battleship.model.player.Player;
import com.battleship.model.ship.Ship;

/**
 * Main service that coordinates the general rules of a Battleship match.
 *
 * This class does not contain JavaFX code. It only receives model objects,
 * delegates ship placement and shooting operations to specialized services,
 * validates the current game state, updates turns, and records shot history.
 *
 * It is intentionally separated from the controller so that the game rules
 * can be tested without depending on the user interface.
 */
public class GameService {

    private final PlacementService placementService;
    private final ShotService shotService;

    /**
     * Creates a game service with the default placement and shot services.
     */
    public GameService() {
        this.placementService = new PlacementService();
        this.shotService = new ShotService();
    }

    /**
     * Creates a new game with a real player and an artificial player.
     *
     * @param humanNickname nickname entered by the real player.
     * @return a new game initialized in placement phase.
     * @throws IllegalArgumentException if the nickname is null or blank.
     */
    public Game createGame(String humanNickname) {
        if (humanNickname == null || humanNickname.isBlank()) {
            throw new IllegalArgumentException("El nickname del jugador no puede estar vacío.");
        }

        return new Game(humanNickname.trim());
    }

    /**
     * Places a ship on the board owned by the given player and adds it to the
     * player's fleet.
     *
     * This overload is useful for unit tests or basic placement operations
     * where the game phase does not need to be validated.
     *
     * @param player          player that owns the ship.
     * @param ship            ship to place.
     * @param startCoordinate first coordinate occupied by the ship.
     * @param orientation     ship orientation.
     * @throws InvalidPlacementException if the player, ship, or placement is
     *                                   invalid.
     */
    public void placeShip(Player player, Ship ship, Coordinate startCoordinate, Orientation orientation) {
        if (player == null) {
            throw new InvalidPlacementException("El jugador no puede ser nulo.");
        }

        if (ship == null) {
            throw new InvalidPlacementException("El barco no puede ser nulo.");
        }

        placementService.placeShip(player.getBoard(), ship, startCoordinate, orientation);
        player.addShip(ship);
    }

    /**
     * Places a ship while validating that the current game still allows ship
     * positioning.
     *
     * @param game            current game.
     * @param player          player that owns the ship.
     * @param ship            ship to place.
     * @param startCoordinate first coordinate occupied by the ship.
     * @param orientation     ship orientation.
     * @throws InvalidGameStateException if the game is no longer in a placement
     *                                   phase.
     * @throws InvalidPlacementException if the player, ship, or placement is
     *                                   invalid.
     */
    public void placeShip(
            Game game,
            Player player,
            Ship ship,
            Coordinate startCoordinate,
            Orientation orientation) throws InvalidGameStateException {

        validatePlacementAllowed(game);
        validatePlayerBelongsToGameIfPresent(game, player);
        placeShip(player, ship, startCoordinate, orientation);
    }

    /**
     * Moves a ship that is already placed on a player's board.
     * 
     * This method keeps the ship in the player's fleet. It only changes the
     * cells occupied by the ship on the board.
     *
     * @param game            current game.
     * @param player          owner of the ship.
     * @param ship            ship to move.
     * @param startCoordinate new first coordinate for the ship.
     * @param orientation     new orientation for the ship.
     * @throws InvalidGameStateException if the game is no longer in a placement
     *                                   phase.
     * @throws InvalidPlacementException if the movement is invalid.
     */
    public void moveShip(
            Game game,
            Player player,
            Ship ship,
            Coordinate startCoordinate,
            Orientation orientation) throws InvalidGameStateException {

        validatePlacementAllowed(game);
        validatePlayerBelongsToGameIfPresent(game, player);

        if (player == null) {
            throw new InvalidPlacementException("El jugador no puede ser nulo.");
        }

        if (ship == null) {
            throw new InvalidPlacementException("El barco no puede ser nulo.");
        }

        placementService.moveShip(player.getBoard(), ship, startCoordinate, orientation);
    }

    /**
     * Starts the battle after the placement phase.
     *
     * @param game current game.
     * @throws IllegalArgumentException  if the game is null.
     * @throws InvalidGameStateException if the game is already finished.
     */
    public void startGame(Game game) throws InvalidGameStateException {
        if (game == null) {
            throw new IllegalArgumentException("La partida no puede ser nula.");
        }

        if (game.getPhase() == GamePhase.FINISHED) {
            throw new InvalidGameStateException("No se puede iniciar una partida que ya finalizó.");
        }

        game.setPhase(GamePhase.IN_PROGRESS);
        game.setCurrentTurn(Turn.HUMAN);
    }

    /**
     * Executes a shot from the human player against the artificial player.
     *
     * @param game       current game.
     * @param coordinate coordinate selected by the human player.
     * @return result of the shot.
     * @throws IllegalArgumentException  if the game is null.
     * @throws InvalidGameStateException if the game is not in progress or it is not
     *                                   the human turn.
     */
    public ShotResult humanShoots(Game game, Coordinate coordinate) throws InvalidGameStateException {
        if (game == null) {
            throw new IllegalArgumentException("La partida no puede ser nula.");
        }

        return shoot(game, game.getHumanPlayer(), game.getMachinePlayer(), coordinate);
    }

    /**
     * Executes a shot from the artificial player against the human player using
     * a specific coordinate.
     *
     * This method is useful for tests or for strategies that already selected
     * the coordinate before calling the service.
     *
     * @param game       current game.
     * @param coordinate coordinate selected by the machine.
     * @return result of the shot.
     * @throws IllegalArgumentException  if the game is null.
     * @throws InvalidGameStateException if the game is not in progress or it is not
     *                                   the machine turn.
     */
    public ShotResult machineShoots(Game game, Coordinate coordinate) throws InvalidGameStateException {
        if (game == null) {
            throw new IllegalArgumentException("La partida no puede ser nula.");
        }

        return shoot(game, game.getMachinePlayer(), game.getHumanPlayer(), coordinate);
    }

    /**
     * Executes an automatic shot from the artificial player.
     *
     * The machine receives the human board as a reference. This allows its
     * current {@code ShotStrategy} to prioritize cells around previous hits
     * instead of always shooting randomly.
     *
     * @param game current game.
     * @return result of the machine shot.
     * @throws IllegalArgumentException  if the game is null.
     * @throws InvalidGameStateException if the game is not in progress or it is not
     *                                   the machine turn.
     */
    public ShotResult machineShoots(Game game) throws InvalidGameStateException {
        if (game == null) {
            throw new IllegalArgumentException("La partida no puede ser nula.");
        }

        Coordinate coordinate = game.getMachinePlayer().nextAvailableShot(game.getHumanPlayer().getBoard());
        return machineShoots(game, coordinate);
    }

    /**
     * Executes the common shot flow used by both players.
     *
     * @param game       current game.
     * @param attacker   player that shoots.
     * @param target     player that receives the shot.
     * @param coordinate coordinate selected for the shot.
     * @return final shot result.
     * @throws InvalidGameStateException if the game state or the attacker turn is
     *                                   invalid.
     */
    private ShotResult shoot(Game game, Player attacker, Player target, Coordinate coordinate)
            throws InvalidGameStateException {

        validateGameForShot(game, attacker);

        Turn attackerTurn = getTurnForPlayer(game, attacker);
        ShotResult result = shotService.shoot(target.getBoard(), coordinate);
        ShotResult finalResult = result;

        if (target.hasLost()) {
            game.setPhase(GamePhase.FINISHED);
            finalResult = ShotResult.WIN;
        }

        game.addShotRecord(new ShotRecord(attackerTurn, coordinate, finalResult));

        if (finalResult != ShotResult.WIN && result == ShotResult.WATER) {
            switchTurn(game);
        }

        return finalResult;
    }

    /**
     * Validates that ship placement is still allowed for the current game.
     *
     * @param game current game. If null, no phase validation is performed.
     * @throws InvalidGameStateException if the game phase does not allow placement
     *                                   changes.
     */
    private void validatePlacementAllowed(Game game) throws InvalidGameStateException {
        if (game == null) {
            return;
        }

        GamePhase phase = game.getPhase();

        if (phase != GamePhase.PLACEMENT && phase != GamePhase.PLAYER_POSITIONING_SHIPS) {
            throw new InvalidGameStateException(
                    "No se pueden modificar los barcos después de iniciar la partida.");
        }
    }

    /**
     * Validates that the player belongs to the given game.
     *
     * @param game   current game.
     * @param player player to validate.
     * @throws InvalidGameStateException if the player is not part of the game.
     */
    private void validatePlayerBelongsToGameIfPresent(Game game, Player player) throws InvalidGameStateException {
        if (game == null || player == null) {
            return;
        }

        if (player != game.getHumanPlayer() && player != game.getMachinePlayer()) {
            throw new InvalidGameStateException("El jugador no pertenece a esta partida.");
        }
    }

    /**
     * Validates that a player can shoot in the current state and turn.
     *
     * @param game     current game.
     * @param attacker player attempting to shoot.
     * @throws InvalidGameStateException if the game is not in progress or it is not
     *                                   the attacker's turn.
     */
    private void validateGameForShot(Game game, Player attacker) throws InvalidGameStateException {
        if (game.getPhase() != GamePhase.IN_PROGRESS) {
            throw new InvalidGameStateException("No se puede disparar si la partida no está en progreso.");
        }

        Turn attackerTurn = getTurnForPlayer(game, attacker);

        if (game.getCurrentTurn() != attackerTurn) {
            throw new InvalidGameStateException("No es el turno del jugador atacante.");
        }
    }

    /**
     * Resolves the turn value that corresponds to a player in the current game.
     *
     * @param game   current game.
     * @param player player to evaluate.
     * @return {@link Turn#HUMAN} for the real player or {@link Turn#MACHINE} for
     *         the artificial player.
     * @throws InvalidGameStateException if the player is not part of the game.
     */
    private Turn getTurnForPlayer(Game game, Player player) throws InvalidGameStateException {
        if (player == game.getHumanPlayer()) {
            return Turn.HUMAN;
        }

        if (player == game.getMachinePlayer()) {
            return Turn.MACHINE;
        }

        throw new InvalidGameStateException("El jugador no pertenece a esta partida.");
    }

    /**
     * Switches the current turn to the opposite player.
     *
     * @param game current game.
     */
    private void switchTurn(Game game) {
        if (game.getCurrentTurn() == Turn.HUMAN) {
            game.setCurrentTurn(Turn.MACHINE);
        } else {
            game.setCurrentTurn(Turn.HUMAN);
        }
    }
}