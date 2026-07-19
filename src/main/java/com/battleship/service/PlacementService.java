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
        return canPlaceShip(board, ship, startCoordinate, orientation, null);
    }

    /**
     * Indica si un barco puede ubicarse en el tablero, ignorando las celdas
     * ocupadas por otro barco (util al reubicar una nave ya colocada).
     *
     * @param board tablero donde se desea ubicar.
     * @param ship barco que se desea colocar.
     * @param startCoordinate coordenada inicial.
     * @param orientation orientación del barco.
     * @param ignoredShip barco cuyas celdas se ignoran; null si no aplica.
     * @return true si la posición es válida; false en caso contrario.
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
     * Coloca un barco en el tablero.
     *
     * @param board tablero donde se colocará el barco.
     * @param ship barco a colocar.
     * @param startCoordinate coordenada inicial.
     * @param orientation orientación del barco.
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
     * Reubica un barco ya colocado en el tablero.
     *
     * @param board tablero donde se moverá el barco.
     * @param ship barco a reubicar.
     * @param startCoordinate nueva coordenada inicial.
     * @param orientation nueva orientación del barco.
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
     * Retira un barco del tablero sin eliminarlo de la flota del jugador.
     *
     * @param board tablero del jugador.
     * @param ship barco a retirar.
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