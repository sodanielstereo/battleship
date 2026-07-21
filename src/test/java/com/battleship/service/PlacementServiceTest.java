package com.battleship.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.battleship.exception.InvalidPlacementException;
import com.battleship.model.board.Board;
import com.battleship.model.board.Coordinate;
import com.battleship.model.enums.CellState;
import com.battleship.model.enums.Orientation;
import com.battleship.model.ship.Destroyer;
import com.battleship.model.ship.Frigate;

class PlacementServiceTest {

    private final PlacementService placementService = new PlacementService();

    @Test
    void shouldPlaceShipHorizontally() {
        Board board = new Board();
        Destroyer destroyer = new Destroyer();

        placementService.placeShip(board, destroyer, new Coordinate(0, 0), Orientation.HORIZONTAL);

        assertEquals(2, destroyer.getPositions().size());
        assertEquals(CellState.SHIP, board.getCell(new Coordinate(0, 0)).getState());
        assertEquals(CellState.SHIP, board.getCell(new Coordinate(0, 1)).getState());
        assertTrue(board.getCell(new Coordinate(0, 0)).hasShip());
        assertTrue(board.getCell(new Coordinate(0, 1)).hasShip());
    }

    @Test
    void shouldPlaceShipVertically() {
        Board board = new Board();
        Destroyer destroyer = new Destroyer();

        placementService.placeShip(board, destroyer, new Coordinate(0, 0), Orientation.VERTICAL);

        assertEquals(2, destroyer.getPositions().size());
        assertEquals(CellState.SHIP, board.getCell(new Coordinate(0, 0)).getState());
        assertEquals(CellState.SHIP, board.getCell(new Coordinate(1, 0)).getState());
    }

    @Test
    void shouldRejectShipOutsideBoard() {
        Board board = new Board();
        Destroyer destroyer = new Destroyer();

        boolean canPlace = placementService.canPlaceShip(
                board,
                destroyer,
                new Coordinate(0, 9),
                Orientation.HORIZONTAL);

        assertFalse(canPlace);
    }

    @Test
    void shouldRejectOverlappedShip() {
        Board board = new Board();

        placementService.placeShip(board, new Destroyer(), new Coordinate(0, 0), Orientation.HORIZONTAL);

        boolean canPlace = placementService.canPlaceShip(
                board,
                new Frigate(),
                new Coordinate(0, 0),
                Orientation.HORIZONTAL);

        assertFalse(canPlace);
    }

    @Test
    void shouldThrowCustomExceptionWhenPlacementIsInvalid() {
        Board board = new Board();
        Destroyer destroyer = new Destroyer();

        assertThrows(
                InvalidPlacementException.class,
                () -> placementService.placeShip(board, destroyer, new Coordinate(0, 9), Orientation.HORIZONTAL));
    }

    @Test
    void shouldMoveShipToNewPosition() {
        Board board = new Board();
        Destroyer destroyer = new Destroyer();

        placementService.placeShip(board, destroyer, new Coordinate(0, 0), Orientation.HORIZONTAL);
        placementService.moveShip(board, destroyer, new Coordinate(2, 3), Orientation.VERTICAL);

        assertFalse(board.getCell(new Coordinate(0, 0)).hasShip());
        assertFalse(board.getCell(new Coordinate(0, 1)).hasShip());
        assertTrue(board.getCell(new Coordinate(2, 3)).hasShip());
        assertTrue(board.getCell(new Coordinate(3, 3)).hasShip());
        assertEquals(Orientation.VERTICAL, destroyer.getOrientation());
    }

    @Test
    void shouldRejectMoveWhenShipOverlapsAnother() {
        Board board = new Board();
        Destroyer firstDestroyer = new Destroyer();
        Destroyer secondDestroyer = new Destroyer();

        placementService.placeShip(board, firstDestroyer, new Coordinate(0, 0), Orientation.HORIZONTAL);
        placementService.placeShip(board, secondDestroyer, new Coordinate(2, 0), Orientation.HORIZONTAL);

        assertThrows(
                InvalidPlacementException.class,
                () -> placementService.moveShip(board, firstDestroyer, new Coordinate(2, 0), Orientation.HORIZONTAL));
    }

    @Test
    void shouldAllowMoveIgnoringOwnPreviousCells() {
        Board board = new Board();
        Destroyer destroyer = new Destroyer();

        placementService.placeShip(board, destroyer, new Coordinate(0, 0), Orientation.HORIZONTAL);

        assertTrue(placementService.canPlaceShip(
                board,
                destroyer,
                new Coordinate(0, 0),
                Orientation.VERTICAL,
                destroyer));
    }
}