package com.battleship.service;

import java.util.ArrayList;
import java.util.List;

import com.battleship.model.board.Board;
import com.battleship.model.board.Cell;
import com.battleship.model.board.Coordinate;
import com.battleship.model.enums.Orientation;
import com.battleship.model.ship.Ship;

/**
 * Servicio encargado de calcular, validar y realizar la colocación de barcos.
 */
public class PlacementService {

    /**
     * Calcula las coordenadas que ocuparía un barco según su posición inicial,
     * tamaño y orientación.
     *
     * @param startCoordinate coordenada inicial.
     * @param shipSize tamaño del barco.
     * @param orientation orientación del barco.
     * @return lista de coordenadas ocupadas por el barco.
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
     * Indica si un barco puede ubicarse en el tablero.
     *
     * @param board tablero donde se desea ubicar.
     * @param ship barco que se desea colocar.
     * @param startCoordinate coordenada inicial.
     * @param orientation orientación del barco.
     * @return true si la posición es válida; false en caso contrario.
     */
    public boolean canPlaceShip(Board board, Ship ship, Coordinate startCoordinate, Orientation orientation) {
        if (board == null || ship == null || startCoordinate == null || orientation == null) {
            return false;
        }

        List<Coordinate> positions = calculatePositions(startCoordinate, ship.getSize(), orientation);

        for (Coordinate coordinate : positions) {
            if (!board.isInside(coordinate)) {
                return false;
            }

            Cell cell = board.getCell(coordinate);

            if (cell == null || cell.hasShip()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Coloca un barco en el tablero.
     *
     * @param board tablero donde se colocará el barco.
     * @param ship barco a colocar.
     * @param startCoordinate coordenada inicial.
     * @param orientation orientación del barco.
     */
    public void placeShip(Board board, Ship ship, Coordinate startCoordinate, Orientation orientation) {
        if (!canPlaceShip(board, ship, startCoordinate, orientation)) {
            throw new IllegalArgumentException("El barco no puede ubicarse en la posición indicada.");
        }

        List<Coordinate> positions = calculatePositions(startCoordinate, ship.getSize(), orientation);

        ship.setOrientation(orientation);
        ship.setPositions(positions);

        for (Coordinate coordinate : positions) {
            board.getCell(coordinate).placeShip(ship);
        }
    }

    private void validatePlacementInput(Coordinate startCoordinate, int shipSize, Orientation orientation) {
        if (startCoordinate == null) {
            throw new IllegalArgumentException("La coordenada inicial no puede ser nula.");
        }

        if (shipSize <= 0) {
            throw new IllegalArgumentException("El tamaño del barco debe ser mayor que cero.");
        }

        if (orientation == null) {
            throw new IllegalArgumentException("La orientación no puede ser nula.");
        }
    }
}