package com.battleship.model.ship;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.battleship.model.board.Coordinate;
import com.battleship.model.enums.Orientation;
import com.battleship.model.enums.ShipType;

/**
 * Base class for all ships in the Battleship game.
 *
 * A ship stores its type, size, orientation, occupied positions, and hit positions.
 * Concrete ship classes only define the specific ship type.
 */
public abstract class Ship implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ShipType type;
    private final int size;
    private Orientation orientation;
    private final List<Coordinate> positions;
    private final List<Coordinate> hitPositions;

    /**
     * Creates a ship using the data stored by its type.
     *
     * @param type specific ship type.
     */
    protected Ship(ShipType type) {
        this.type = type;
        this.size = type.getSize();
        this.orientation = Orientation.HORIZONTAL;
        this.positions = new ArrayList<>();
        this.hitPositions = new ArrayList<>();
    }

    /**
     * Returns the ship type.
     *
     * @return ship type.
     */
    public ShipType getType() {
        return type;
    }

    /**
     * Returns the number of cells occupied by this ship.
     *
     * @return ship size.
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the current ship orientation.
     *
     * @return ship orientation.
     */
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * Updates the current ship orientation.
     *
     * @param orientation new orientation.
     */
    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    /**
     * Returns the coordinates currently occupied by the ship.
     *
     * @return immutable list of occupied coordinates.
     */
    public List<Coordinate> getPositions() {
        return Collections.unmodifiableList(positions);
    }

    /**
     * Replaces the occupied coordinates of the ship.
     *
     * @param newPositions new coordinate list.
     */
    public void setPositions(List<Coordinate> newPositions) {
        positions.clear();
        positions.addAll(newPositions);
    }

    /**
     * Registers a hit on this ship if the coordinate belongs to it.
     *
     * @param coordinate coordinate hit by a shot.
     */
    public void registerHit(Coordinate coordinate) {
        if (positions.contains(coordinate) && !hitPositions.contains(coordinate)) {
            hitPositions.add(coordinate);
        }
    }

    /**
     * Indicates whether every occupied position has been hit.
     *
     * @return {@code true} if the ship is sunk; otherwise {@code false}.
     */
    public boolean isSunk() {
        return hitPositions.size() == size;
    }

    /**
     * Checks whether the ship occupies a coordinate.
     *
     * @param coordinate coordinate to check.
     * @return {@code true} if the coordinate is part of the ship positions; otherwise {@code false}.
     */
    public boolean occupies(Coordinate coordinate) {
        return positions.contains(coordinate);
    }

    /**
     * Returns the coordinates that have been hit.
     *
     * @return immutable list of hit coordinates.
     */
    public List<Coordinate> getHitPositions() {
        return Collections.unmodifiableList(hitPositions);
    }

    /**
     * Returns the display name of the ship type.
     *
     * @return user-facing ship name.
     */
    public String getDisplayName() {
        return type.getDisplayName();
    }
}
