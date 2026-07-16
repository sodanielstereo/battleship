package com.battleship.model.board;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Representa un tablero de Batalla Naval.
 *
 * Por defecto el tablero es de 10x10, como exige el enunciado.
 */
public class Board implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_SIZE = 10;

    private final int size;
    private final Map<Coordinate, Cell> cells;

    public Board() {
        this(DEFAULT_SIZE);
    }

    public Board(int size) {
        this.size = size;
        this.cells = new LinkedHashMap<>();
        initializeCells();
    }

    private void initializeCells() {
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                Coordinate coordinate = new Coordinate(row, column);
                cells.put(coordinate, new Cell(coordinate));
            }
        }
    }

    public int getSize() {
        return size;
    }

    public Map<Coordinate, Cell> getCells() {
        return cells;
    }

    public Cell getCell(Coordinate coordinate) {
        return cells.get(coordinate);
    }

    public boolean isInside(Coordinate coordinate) {
        return coordinate != null
                && coordinate.getRow() >= 0
                && coordinate.getRow() < size
                && coordinate.getColumn() >= 0
                && coordinate.getColumn() < size;
    }
}