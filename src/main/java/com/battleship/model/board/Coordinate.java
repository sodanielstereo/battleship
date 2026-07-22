package com.battleship.model.board;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a row and column position inside a board.
 *
 * Rows and columns are handled with zero-based indexes. Equality and hashing are
 * implemented so coordinates can be safely used as keys in maps and collections.
 */
public class Coordinate implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int row;
    private final int column;

    /**
     * Creates a board coordinate.
     *
     * @param row zero-based row index.
     * @param column zero-based column index.
     */
    public Coordinate(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Returns the row index.
     *
     * @return zero-based row.
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column index.
     *
     * @return zero-based column.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Compares this coordinate with another object.
     *
     * @param object object to compare.
     * @return {@code true} if both coordinates have the same row and column; otherwise {@code false}.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Coordinate coordinate)) {
            return false;
        }

        return row == coordinate.row && column == coordinate.column;
    }

    /**
     * Computes a hash code based on row and column.
     *
     * @return hash code for map and set usage.
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    /**
     * Returns a readable coordinate representation.
     *
     * @return coordinate formatted as {@code (row, column)}.
     */
    @Override
    public String toString() {
        return "(" + row + ", " + column + ")";
    }
}
