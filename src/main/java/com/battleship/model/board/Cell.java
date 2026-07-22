package com.battleship.model.board;

import java.io.Serializable;

import com.battleship.model.enums.CellState;
import com.battleship.model.ship.Ship;

/**
 * Represents one cell of a Battleship board.
 *
 * A cell stores its coordinate, visible logical state, and the ship occupying it
 * when applicable. The state changes as ships are placed and shots are resolved.
 */
public class Cell implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Coordinate coordinate;
    private CellState state;
    private Ship ship;

    /**
     * Creates an empty cell for the provided coordinate.
     *
     * @param coordinate board coordinate represented by this cell.
     */
    public Cell(Coordinate coordinate) {
        this.coordinate = coordinate;
        this.state = CellState.EMPTY;
    }

    /**
     * Returns this cell coordinate.
     *
     * @return cell coordinate.
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Returns the current logical state.
     *
     * @return current cell state.
     */
    public CellState getState() {
        return state;
    }

    /**
     * Updates the current logical state.
     *
     * @param state new cell state.
     */
    public void setState(CellState state) {
        this.state = state;
    }

    /**
     * Returns the ship placed in this cell.
     *
     * @return ship occupying the cell, or {@code null} if there is none.
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Places a ship on this cell and marks the cell as occupied.
     *
     * @param ship ship assigned to the cell.
     */
    public void placeShip(Ship ship) {
        this.ship = ship;
        this.state = CellState.SHIP;
    }

    /**
     * Removes the current ship reference and returns the cell to the empty state.
     */
    public void clearShip() {
        this.ship = null;
        this.state = CellState.EMPTY;
    }

    /**
     * Indicates whether this cell contains a ship.
     *
     * @return {@code true} if a ship is assigned; otherwise {@code false}.
     */
    public boolean hasShip() {
        return ship != null;
    }

    /**
     * Indicates whether this cell has already received a shot.
     *
     * @return {@code true} for water, hit, or sunk states; otherwise {@code false}.
     */
    public boolean wasShot() {
        return state == CellState.WATER || state == CellState.HIT || state == CellState.SUNK;
    }

    /**
     * Marks this cell as a shot that landed in water.
     */
    public void markAsWater() {
        this.state = CellState.WATER;
    }

    /**
     * Marks this cell as a successful hit on a ship.
     */
    public void markAsHit() {
        this.state = CellState.HIT;
    }

    /**
     * Marks this cell as part of a ship that has been fully sunk.
     */
    public void markAsSunk() {
        this.state = CellState.SUNK;
    }
}
