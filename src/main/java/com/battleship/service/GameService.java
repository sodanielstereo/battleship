package com.battleship.service;

import com.battleship.model.Game;
import com.battleship.model.board.Coordinate;
import com.battleship.model.enums.GamePhase;
import com.battleship.model.enums.Orientation;
import com.battleship.model.enums.ShotResult;
import com.battleship.model.enums.Turn;
import com.battleship.model.player.Player;
import com.battleship.model.ship.Ship;

/**
 * Servicio principal para manejar reglas generales de una partida.
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
     * @param startCoordinate coordenada inicial.
     * @param orientation orientación del barco.
     */
    public void placeShip(Player player, Ship ship, Coordinate startCoordinate, Orientation orientation) {
        if (player == null) {
            throw new IllegalArgumentException("El jugador no puede ser nulo.");
        }

        placementService.placeShip(player.getBoard(), ship, startCoordinate, orientation);
        player.addShip(ship);
    }

    /**
     * Cambia la partida de fase de colocación a fase de juego.
     *
     * @param game partida actual.
     */
    public void startGame(Game game) {
        if (game == null) {
            throw new IllegalArgumentException("La partida no puede ser nula.");
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
     */
    public ShotResult humanShoots(Game game, Coordinate coordinate) {
        return shoot(game, game.getHumanPlayer(), game.getMachinePlayer(), coordinate);
    }

    /**
     * Ejecuta un disparo de la máquina contra el jugador humano.
     *
     * @param game partida actual.
     * @param coordinate coordenada atacada.
     * @return resultado del disparo.
     */
    public ShotResult machineShoots(Game game, Coordinate coordinate) {
        return shoot(game, game.getMachinePlayer(), game.getHumanPlayer(), coordinate);
    }

    private ShotResult shoot(Game game, Player attacker, Player target, Coordinate coordinate) {
        validateGameForShot(game, attacker);

        ShotResult result = shotService.shoot(target.getBoard(), coordinate);

        if (target.hasLost()) {
            game.setPhase(GamePhase.FINISHED);
            return ShotResult.WIN;
        }

        if (result == ShotResult.WATER) {
            switchTurn(game);
        }

        return result;
    }

    private void validateGameForShot(Game game, Player attacker) {
        if (game == null) {
            throw new IllegalArgumentException("La partida no puede ser nula.");
        }

        if (game.getPhase() != GamePhase.IN_PROGRESS) {
            throw new IllegalStateException("No se puede disparar si la partida no está en progreso.");
        }

        Turn attackerTurn = getTurnForPlayer(game, attacker);

        if (game.getCurrentTurn() != attackerTurn) {
            throw new IllegalStateException("No es el turno del jugador atacante.");
        }
    }

    private Turn getTurnForPlayer(Game game, Player player) {
        if (player == game.getHumanPlayer()) {
            return Turn.HUMAN;
        }

        if (player == game.getMachinePlayer()) {
            return Turn.MACHINE;
        }

        throw new IllegalArgumentException("El jugador no pertenece a esta partida.");
    }

    private void switchTurn(Game game) {
        if (game.getCurrentTurn() == Turn.HUMAN) {
            game.setCurrentTurn(Turn.MACHINE);
        } else {
            game.setCurrentTurn(Turn.HUMAN);
        }
    }
}