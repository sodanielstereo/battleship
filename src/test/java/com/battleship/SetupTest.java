package com.battleship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.battleship.model.Game;
import com.battleship.model.board.Board;
import com.battleship.model.board.Coordinate;
import com.battleship.model.enums.GamePhase;
import com.battleship.model.enums.ShipType;
import com.battleship.model.enums.Turn;
import com.battleship.model.ship.AircraftCarrier;
import com.battleship.model.ship.Destroyer;
import com.battleship.model.ship.Frigate;
import com.battleship.model.ship.Submarine;

/**
 * Pruebas iniciales para validar que el modelo base fue creado correctamente.
 */
class SetupTest {

    @Test
    void boardShouldHaveDefaultSizeTen() {
        Board board = new Board();

        assertEquals(10, board.getSize());
        assertEquals(100, board.getCells().size());
    }

    @Test
    void boardShouldValidateCoordinatesInsideLimits() {
        Board board = new Board();

        assertTrue(board.isInside(new Coordinate(0, 0)));
        assertTrue(board.isInside(new Coordinate(9, 9)));
        assertFalse(board.isInside(new Coordinate(-1, 0)));
        assertFalse(board.isInside(new Coordinate(10, 0)));
        assertFalse(board.isInside(new Coordinate(0, 10)));
    }

    @Test
    void shipTypesShouldHaveCorrectSizes() {
        assertEquals(4, ShipType.AIRCRAFT_CARRIER.getSize());
        assertEquals(3, ShipType.SUBMARINE.getSize());
        assertEquals(2, ShipType.DESTROYER.getSize());
        assertEquals(1, ShipType.FRIGATE.getSize());
    }

    @Test
    void concreteShipsShouldHaveCorrectTypeAndSize() {
        assertEquals(ShipType.AIRCRAFT_CARRIER, new AircraftCarrier().getType());
        assertEquals(4, new AircraftCarrier().getSize());

        assertEquals(ShipType.SUBMARINE, new Submarine().getType());
        assertEquals(3, new Submarine().getSize());

        assertEquals(ShipType.DESTROYER, new Destroyer().getType());
        assertEquals(2, new Destroyer().getSize());

        assertEquals(ShipType.FRIGATE, new Frigate().getType());
        assertEquals(1, new Frigate().getSize());
    }

    @Test
    void gameShouldStartInPlacementPhaseWithHumanTurn() {
        Game game = new Game("Daniel");

        assertEquals(GamePhase.PLACEMENT, game.getPhase());
        assertEquals(Turn.HUMAN, game.getCurrentTurn());
        assertEquals("Daniel", game.getHumanPlayer().getNickname());
        assertEquals("Máquina", game.getMachinePlayer().getNickname());
    }
}