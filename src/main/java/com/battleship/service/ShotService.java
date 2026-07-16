package com.battleship.service;

import com.battleship.exception.InvalidShotException;
import com.battleship.model.board.Board;
import com.battleship.model.board.Cell;
import com.battleship.model.board.Coordinate;
import com.battleship.model.enums.ShotResult;
import com.battleship.model.ship.Ship;

/**
 * Servicio encargado de resolver los disparos sobre un tablero.
 */
public class ShotService {

    /**
     * Realiza un disparo sobre el tablero indicado.
     *
     * @param targetBoard tablero del jugador atacado.
     * @param coordinate coordenada del disparo.
     * @return resultado del disparo.
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

    private void markShipAsSunk(Board board, Ship ship) {
        for (Coordinate coordinate : ship.getPositions()) {
            board.getCell(coordinate).markAsSunk();
        }
    }

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