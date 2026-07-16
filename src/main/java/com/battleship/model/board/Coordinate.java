package com.battleship.model.board;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a coordinate within the game board.
 *
 * The row and column are handled with indices from 0 to 9.
 */
public class Coordinate implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int row;
    private final int column;

    public Coordinate(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public String toString() {
        return "(" + row + ", " + column + ")";
    }
}