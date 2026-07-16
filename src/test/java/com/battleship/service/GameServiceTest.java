package com.battleship.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.battleship.exception.InvalidGameStateException;
import com.battleship.model.Game;
import com.battleship.model.board.Coordinate;
import com.battleship.model.enums.GamePhase;
import com.battleship.model.enums.Orientation;
import com.battleship.model.enums.ShotResult;
import com.battleship.model.enums.Turn;
import com.battleship.model.ship.Destroyer;
import com.battleship.model.ship.Frigate;

class GameServiceTest {

    private final GameService gameService = new GameService();

    @Test
    void shouldCreateGameWithHumanNickname() {
        Game game = gameService.createGame("Daniel");

        assertEquals("Daniel", game.getHumanPlayer().getNickname());
        assertEquals("Máquina", game.getMachinePlayer().getNickname());
        assertEquals(GamePhase.PLACEMENT, game.getPhase());
        assertEquals(Turn.HUMAN, game.getCurrentTurn());
    }

    @Test
    void shouldStartGameInProgressWithHumanTurn() throws InvalidGameStateException {
        Game game = gameService.createGame("Daniel");

        gameService.startGame(game);

        assertEquals(GamePhase.IN_PROGRESS, game.getPhase());
        assertEquals(Turn.HUMAN, game.getCurrentTurn());
    }

    @Test
    void waterShotShouldSwitchTurn() throws InvalidGameStateException {
        Game game = gameService.createGame("Daniel");
        gameService.startGame(game);

        ShotResult result = gameService.humanShoots(game, new Coordinate(9, 9));

        assertEquals(ShotResult.WATER, result);
        assertEquals(Turn.MACHINE, game.getCurrentTurn());
    }

    @Test
    void hitShotShouldKeepTurn() throws InvalidGameStateException {
        Game game = gameService.createGame("Daniel");

        gameService.placeShip(
                game.getMachinePlayer(),
                new Destroyer(),
                new Coordinate(0, 0),
                Orientation.HORIZONTAL
        );

        gameService.startGame(game);

        ShotResult result = gameService.humanShoots(game, new Coordinate(0, 0));

        assertEquals(ShotResult.HIT, result);
        assertEquals(Turn.HUMAN, game.getCurrentTurn());
    }

    @Test
    void sinkingAllEnemyShipsShouldFinishGame() throws InvalidGameStateException {
        Game game = gameService.createGame("Daniel");

        gameService.placeShip(
                game.getMachinePlayer(),
                new Frigate(),
                new Coordinate(0, 0),
                Orientation.HORIZONTAL
        );

        gameService.startGame(game);

        ShotResult result = gameService.humanShoots(game, new Coordinate(0, 0));

        assertEquals(ShotResult.WIN, result);
        assertEquals(GamePhase.FINISHED, game.getPhase());
    }

    @Test
    void shouldNotAllowShotBeforeGameStarts() {
        Game game = gameService.createGame("Daniel");

        assertThrows(
                InvalidGameStateException.class,
                () -> gameService.humanShoots(game, new Coordinate(0, 0))
        );
    }

    @Test
    void shouldNotAllowMachineShotWhenItIsHumanTurn() throws InvalidGameStateException {
        Game game = gameService.createGame("Daniel");
        gameService.startGame(game);

        assertThrows(
                InvalidGameStateException.class,
                () -> gameService.machineShoots(game, new Coordinate(0, 0))
        );
    }
}