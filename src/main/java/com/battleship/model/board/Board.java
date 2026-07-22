package com.battleship.model.board;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a Battleship board.
 *
 * The default board size is 10x10. Cells are stored in a {@link java.util.Map}
 * using {@link Coordinate} as key, which makes coordinate-based lookup direct and
 * clear for the service layer and JavaFX controller.
 */
public class Board implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_SIZE = 10;

    private final int size;
    private final Map<Coordinate, Cell> cells;

    /**
     * Creates a default 10x10 board.
     */
    public Board() {
        this(DEFAULT_SIZE);
    }

    /**
     * Creates a square board using the provided size.
     *
     * @param size number of rows and columns in the board.
     */
    public Board(int size) {
        this.size = size;
        this.cells = new LinkedHashMap<>();
        initializeCells();
    }

    /**
     * Creates every coordinate and cell required by the board.
     */
    private void initializeCells() {
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                Coordinate coordinate = new Coordinate(row, column);
                cells.put(coordinate, new Cell(coordinate));
            }
        }
    }

    /**
     * Returns the size of the square board.
     *
     * @return number of rows and columns.
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the map of cells indexed by coordinate.
     *
     * @return board cell map.
     */
    public Map<Coordinate, Cell> getCells() {
        return cells;
    }

    /**
     * Returns the cell located at the given coordinate.
     *
     * @param coordinate coordinate to search.
     * @return matching cell, or {@code null} if the coordinate is not stored.
     */
    public Cell getCell(Coordinate coordinate) {
        return cells.get(coordinate);
    }

    /**
     * Checks whether a coordinate is inside the board boundaries.
     *
     * @param coordinate coordinate to validate.
     * @return {@code true} if the coordinate belongs to this board; otherwise {@code false}.
     */
    public boolean isInside(Coordinate coordinate) {
        return coordinate != null
                && coordinate.getRow() >= 0
                && coordinate.getRow() < size
                && coordinate.getColumn() >= 0
                && coordinate.getColumn() < size;
    }
}
