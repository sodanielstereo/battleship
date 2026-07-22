package com.battleship.service;

import com.battleship.exception.InvalidShotException;
import com.battleship.model.board.Board;
import com.battleship.model.board.Cell;
import com.battleship.model.board.Coordinate;
import com.battleship.model.enums.ShotResult;
import com.battleship.model.ship.Ship;

/**
 * Service responsible for resolving shots against a target board.
 *
 * It validates the selected coordinate, updates the affected cell, registers hits
 * on ships, and marks the complete ship as sunk when all its positions were hit.
 */
public class ShotService {

    /**
     * Executes a shot against a target board.
     *
     * The method returns {@link ShotResult#WATER} when no ship is present,
     * {@link ShotResult#HIT} when a ship is hit but still alive, and
     * {@link ShotResult#SUNK} when the shot sinks the complete ship.
     *
     * @param targetBoard board that receives the shot.
     * @param coordinate coordinate selected for the shot.
     * @return result produced by the shot.
     * @throws InvalidShotException if the board or coordinate is invalid, or if the cell was already shot.
     */
    public ShotResult shoot(Board targetBoard, Coordinate coordinate) {
        validateShotInput(targetBoard, coordinate);

        Cell cell = targetBoard.getCell(coordinate);

        if (cell.wasShot()) {
            throw new InvalidShotException("La casilla ya fue atacada anteriormente.");
        }

        if (!cell.hasShip()) {
            cell.markAsWater();
            return ShotResult.WATER;
        }

        Ship ship = cell.getShip();
        ship.registerHit(coordinate);

        if (ship.isSunk()) {
            markShipAsSunk(targetBoard, ship);
            return ShotResult.SUNK;
        }

        cell.markAsHit();
        return ShotResult.HIT;
    }

    /**
     * Marks every occupied coordinate of a ship as sunk.
     *
     * @param board board that contains the ship.
     * @param ship ship that has just been sunk.
     */
    private void markShipAsSunk(Board board, Ship ship) {
        for (Coordinate coordinate : ship.getPositions()) {
            board.getCell(coordinate).markAsSunk();
        }
    }

    /**
     * Validates the board and coordinate before resolving a shot.
     *
     * @param targetBoard board that receives the shot.
     * @param coordinate coordinate selected for the shot.
     * @throws InvalidShotException if the board is null, the coordinate is null, or the coordinate is outside the board.
     */
    private void validateShotInput(Board targetBoard, Coordinate coordinate) {
        if (targetBoard == null) {
            throw new InvalidShotException("El tablero objetivo no puede ser nulo.");
        }

        if (coordinate == null) {
            throw new InvalidShotException("La coordenada del disparo no puede ser nula.");
        }

        if (!targetBoard.isInside(coordinate)) {
            throw new InvalidShotException("La coordenada del disparo está fuera del tablero.");
        }
    }
}
