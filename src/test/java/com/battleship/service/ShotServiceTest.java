package com.battleship.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.battleship.model.board.Board;
import com.battleship.model.board.Coordinate;
import com.battleship.model.enums.CellState;
import com.battleship.model.enums.Orientation;
import com.battleship.model.enums.ShotResult;
import com.battleship.model.ship.Destroyer;
import com.battleship.model.ship.Frigate;

class ShotServiceTest {

    private final PlacementService placementService = new PlacementService();
    private final ShotService shotService = new ShotService();

    @Test
    void shouldMarkWaterWhenThereIsNoShip() {
        Board board = new Board();

        ShotResult result = shotService.shoot(board, new Coordinate(5, 5));

        assertEquals(ShotResult.WATER, result);
        assertEquals(CellState.WATER, board.getCell(new Coordinate(5, 5)).getState());
    }

    @Test
    void shouldMarkHitWhenShipIsNotSunk() {
        Board board = new Board();
        Destroyer destroyer = new Destroyer();

        placementService.placeShip(board, destroyer, new Coordinate(0, 0), Orientation.HORIZONTAL);

        ShotResult result = shotService.shoot(board, new Coordinate(0, 0));

        assertEquals(ShotResult.HIT, result);
        assertEquals(CellState.HIT, board.getCell(new Coordinate(0, 0)).getState());
        assertFalse(destroyer.isSunk());
    }

    @Test
    void shouldMarkSunkWhenAllShipPositionsAreHit() {
        Board board = new Board();
        Destroyer destroyer = new Destroyer();

        placementService.placeShip(board, destroyer, new Coordinate(0, 0), Orientation.HORIZONTAL);

        ShotResult firstResult = shotService.shoot(board, new Coordinate(0, 0));
        ShotResult secondResult = shotService.shoot(board, new Coordinate(0, 1));

        assertEquals(ShotResult.HIT, firstResult);
        assertEquals(ShotResult.SUNK, secondResult);
        assertTrue(destroyer.isSunk());
        assertEquals(CellState.SUNK, board.getCell(new Coordinate(0, 0)).getState());
        assertEquals(CellState.SUNK, board.getCell(new Coordinate(0, 1)).getState());
    }

    @Test
    void shouldSinkFrigateWithOneShot() {
        Board board = new Board();
        Frigate frigate = new Frigate();

        placementService.placeShip(board, frigate, new Coordinate(3, 3), Orientation.HORIZONTAL);

        ShotResult result = shotService.shoot(board, new Coordinate(3, 3));

        assertEquals(ShotResult.SUNK, result);
        assertTrue(frigate.isSunk());
        assertEquals(CellState.SUNK, board.getCell(new Coordinate(3, 3)).getState());
    }

    @Test
    void shouldRejectRepeatedShot() {
        Board board = new Board();

        shotService.shoot(board, new Coordinate(5, 5));

        assertThrows(
                IllegalStateException.class,
                () -> shotService.shoot(board, new Coordinate(5, 5))
        );
    }
}