package com.battleship.service;

import java.util.ArrayList;
import java.util.List;

import com.battleship.exception.InvalidPlacementException;
import com.battleship.model.board.Board;
import com.battleship.model.board.Cell;
import com.battleship.model.board.Coordinate;
import com.battleship.model.enums.Orientation;
import com.battleship.model.ship.Ship;

/**
 * Service responsible for calculating, validating, placing, moving, and removing ships on a board.
 *
 * The service contains the board-positioning rules and keeps them independent
 * from JavaFX and from the general game orchestration service.
 */
public class PlacementService {

    /**
     * Calculates every coordinate that would be occupied by a ship.
     *
     * @param startCoordinate first coordinate of the ship.
     * @param shipSize number of cells occupied by the ship.
     * @param orientation direction used to extend the ship from the start coordinate.
     * @return list of occupied coordinates.
     * @throws InvalidPlacementException if the coordinate, size, or orientation is invalid.
     */
    public List<Coordinate> calculatePositions(Coordinate startCoordinate, int shipSize, Orientation orientation) {
        validatePlacementInput(startCoordinate, shipSize, orientation);

        List<Coordinate> positions = new ArrayList<>();

        for (int index = 0; index < shipSize; index++) {
            int row = startCoordinate.getRow();
            int column = startCoordinate.getColumn();

            if (orientation == Orientation.HORIZONTAL) {
                column += index;
            } else {
                row += index;
            }

            positions.add(new Coordinate(row, column));
        }

        return positions;
    }

    /**
     * Checks whether a ship can be placed on a board.
     *
     * @param board board where the ship would be placed.
     * @param ship ship to place.
     * @param startCoordinate first coordinate of the ship.
     * @param orientation placement orientation.
     * @return {@code true} if the ship can be placed; otherwise {@code false}.
     */
    public boolean canPlaceShip(Board board, Ship ship, Coordinate startCoordinate, Orientation orientation) {
        return canPlaceShip(board, ship, startCoordinate, orientation, null);
    }

    /**
     * Checks whether a ship can be placed while ignoring the cells occupied by
     * another ship.
     *
     * This overload is used when a ship is being moved or rotated. The current
     * cells of the same ship can be ignored so the validation does not reject
     * its own previous position.
     *
     * @param board board where the ship would be placed.
     * @param ship ship to place.
     * @param startCoordinate first coordinate of the ship.
     * @param orientation placement orientation.
     * @param ignoredShip ship whose cells must be ignored, or {@code null} when no ship should be ignored.
     * @return {@code true} if the ship can be placed; otherwise {@code false}.
     */
    public boolean canPlaceShip(
            Board board,
            Ship ship,
            Coordinate startCoordinate,
            Orientation orientation,
            Ship ignoredShip) {

        if (board == null || ship == null || startCoordinate == null || orientation == null) {
            return false;
        }

        List<Coordinate> positions = calculatePositions(startCoordinate, ship.getSize(), orientation);

        for (Coordinate coordinate : positions) {
            if (!board.isInside(coordinate)) {
                return false;
            }

            Cell cell = board.getCell(coordinate);

            if (cell == null) {
                return false;
            }

            if (cell.hasShip() && cell.getShip() != ignoredShip) {
                return false;
            }
        }

        return true;
    }

    /**
     * Places a ship on the board after validating the requested position.
     *
     * @param board board where the ship will be placed.
     * @param ship ship to place.
     * @param startCoordinate first coordinate of the ship.
     * @param orientation placement orientation.
     * @throws InvalidPlacementException if the ship cannot be placed.
     */
    public void placeShip(Board board, Ship ship, Coordinate startCoordinate, Orientation orientation) {
        if (!canPlaceShip(board, ship, startCoordinate, orientation)) {
            throw new InvalidPlacementException("El barco no puede ubicarse en la posición indicada.");
        }

        List<Coordinate> positions = calculatePositions(startCoordinate, ship.getSize(), orientation);

        ship.setOrientation(orientation);
        ship.setPositions(positions);

        for (Coordinate coordinate : positions) {
            board.getCell(coordinate).placeShip(ship);
        }
    }

    /**
     * Moves an already placed ship to a new valid position.
     *
     * @param board board where the ship is currently placed.
     * @param ship ship to move.
     * @param startCoordinate new first coordinate.
     * @param orientation new orientation.
     * @throws InvalidPlacementException if the board, ship, or new position is invalid.
     */
    public void moveShip(Board board, Ship ship, Coordinate startCoordinate, Orientation orientation) {
        if (board == null || ship == null) {
            throw new InvalidPlacementException("El tablero y el barco no pueden ser nulos.");
        }

        if (!canPlaceShip(board, ship, startCoordinate, orientation, ship)) {
            throw new InvalidPlacementException("El barco no puede reubicarse en la posición indicada.");
        }

        removeShipFromBoard(board, ship);
        placeShip(board, ship, startCoordinate, orientation);
    }

    /**
     * Removes a ship from the board without deleting it from the player fleet.
     *
     * @param board board that currently contains the ship.
     * @param ship ship to remove.
     * @throws InvalidPlacementException if the board or ship is {@code null}.
     */
    public void removeShipFromBoard(Board board, Ship ship) {
        if (board == null || ship == null) {
            throw new InvalidPlacementException("El tablero y el barco no pueden ser nulos.");
        }

        for (Coordinate coordinate : ship.getPositions()) {
            Cell cell = board.getCell(coordinate);

            if (cell != null && cell.getShip() == ship) {
                cell.clearShip();
            }
        }

        ship.setPositions(List.of());
    }

    /**
     * Validates the base information required to calculate a ship placement.
     *
     * @param startCoordinate first coordinate of the ship.
     * @param shipSize number of occupied cells.
     * @param orientation placement orientation.
     * @throws InvalidPlacementException if any required value is invalid.
     */
    private void validatePlacementInput(Coordinate startCoordinate, int shipSize, Orientation orientation) {
        if (startCoordinate == null) {
            throw new InvalidPlacementException("La coordenada inicial no puede ser nula.");
        }

        if (shipSize <= 0) {
            throw new InvalidPlacementException("El tamaño del barco debe ser mayor que cero.");
        }

        if (orientation == null) {
            throw new InvalidPlacementException("La orientación no puede ser nula.");
        }
    }
}
