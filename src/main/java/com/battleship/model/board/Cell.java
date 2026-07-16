package com.battleship.model.board;

import java.io.Serializable;

import com.battleship.model.enums.CellState;
import com.battleship.model.ship.Ship;

/**
 * Represents a cell on the Battleship game board.
 */
public class Cell implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Coordinate coordinate;
    private CellState state;
    private Ship ship;

    public Cell(Coordinate coordinate) {
        this.coordinate = coordinate;
        this.state = CellState.EMPTY;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }

    public Ship getShip() {
        return ship;
    }

    public void placeShip(Ship ship) {
        this.ship = ship;
        this.state = CellState.SHIP;
    }

    public boolean hasShip() {
        return ship != null;
    }

    public boolean wasShot() {
        return state == CellState.WATER || state == CellState.HIT || state == CellState.SUNK;
    }

    public void markAsWater() {
        this.state = CellState.WATER;
    }

    public void markAsHit() {
        this.state = CellState.HIT;
    }

    public void markAsSunk() {
        this.state = CellState.SUNK;
    }
}