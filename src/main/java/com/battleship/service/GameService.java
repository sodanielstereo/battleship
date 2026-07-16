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
 * Servicio principal para manejar las reglas generales de una partida de Batalla Naval.
 */
public class GameService {

    private final PlacementService placementService;
    private final ShotService shotService;

    public GameService() {
        this.placementService = new PlacementService();
        this.shotService = new ShotService();
    }

    /**
     * Crea una nueva partida.
     *
     * @param humanNickname nickname del jugador humano.
     * @return nueva partida.
     */
    public Game createGame(String humanNickname) {
        if (humanNickname == null || humanNickname.isBlank()) {
            throw new IllegalArgumentException("El nickname del jugador no puede estar vacío.");
        }

        return new Game(humanNickname.trim());
    }

    /**
     * Coloca un barco en el tablero de un jugador y lo agrega a su flota.
     *
     * @param player jugador propietario del barco.
     * @param ship barco a colocar.
     * @param startCoordinate coordenada inicial del barco.
     * @param orientation orientación del barco.
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
     * Cambia la partida de fase de colocación a fase de juego.
     *
     * @param game partida actual.
     * @throws InvalidGameStateException si la partida no puede iniciar por su estado actual.
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
     * Ejecuta un disparo del jugador humano contra la máquina.
     *
     * @param game partida actual.
     * @param coordinate coordenada atacada.
     * @return resultado del disparo.
     * @throws InvalidGameStateException si no se puede disparar por el estado o turno actual.
     */
    public ShotResult humanShoots(Game game, Coordinate coordinate) throws InvalidGameStateException {
        if (game == null) {
            throw new IllegalArgumentException("La partida no puede ser nula.");
        }

        return shoot(game, game.getHumanPlayer(), game.getMachinePlayer(), coordinate);
    }

    /**
     * Ejecuta un disparo de la máquina contra el jugador humano usando una coordenada específica.
     *
     * Este método es útil para pruebas unitarias o para una futura estrategia de disparo.
     *
     * @param game partida actual.
     * @param coordinate coordenada atacada.
     * @return resultado del disparo.
     * @throws InvalidGameStateException si no se puede disparar por el estado o turno actual.
     */
    public ShotResult machineShoots(Game game, Coordinate coordinate) throws InvalidGameStateException {
        if (game == null) {
            throw new IllegalArgumentException("La partida no puede ser nula.");
        }

        return shoot(game, game.getMachinePlayer(), game.getHumanPlayer(), coordinate);
    }

    /**
     * Ejecuta un disparo automático de la máquina usando su cola de disparos disponibles.
     *
     * @param game partida actual.
     * @return resultado del disparo.
     * @throws InvalidGameStateException si no se puede disparar por el estado o turno actual.
     */
    public ShotResult machineShoots(Game game) throws InvalidGameStateException {
        if (game == null) {
            throw new IllegalArgumentException("La partida no puede ser nula.");
        }

        Coordinate coordinate = game.getMachinePlayer().nextAvailableShot();
        return machineShoots(game, coordinate);
    }

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

    private void validateGameForShot(Game game, Player attacker) throws InvalidGameStateException {
        if (game.getPhase() != GamePhase.IN_PROGRESS) {
            throw new InvalidGameStateException("No se puede disparar si la partida no está en progreso.");
        }

        Turn attackerTurn = getTurnForPlayer(game, attacker);

        if (game.getCurrentTurn() != attackerTurn) {
            throw new InvalidGameStateException("No es el turno del jugador atacante.");
        }
    }

    private Turn getTurnForPlayer(Game game, Player player) throws InvalidGameStateException {
        if (player == game.getHumanPlayer()) {
            return Turn.HUMAN;
        }

        if (player == game.getMachinePlayer()) {
            return Turn.MACHINE;
        }

        throw new InvalidGameStateException("El jugador no pertenece a esta partida.");
    }

    private void switchTurn(Game game) {
        if (game.getCurrentTurn() == Turn.HUMAN) {
            game.setCurrentTurn(Turn.MACHINE);
        } else {
            game.setCurrentTurn(Turn.HUMAN);
        }
    }
}