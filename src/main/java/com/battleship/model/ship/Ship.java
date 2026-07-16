package com.battleship.model.ship;

import com.battleship.model.board.Coordinate;
import com.battleship.model.enums.Orientation;
import com.battleship.model.enums.ShipType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clase base para representar un barco del juego.
 */
public abstract class Ship implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ShipType type;
    private final int size;
    private Orientation orientation;
    private final List<Coordinate> positions;
    private final List<Coordinate> hitPositions;

    protected Ship(ShipType type) {
        this.type = type;
        this.size = type.getSize();
        this.orientation = Orientation.HORIZONTAL;
        this.positions = new ArrayList<>();
        this.hitPositions = new ArrayList<>();
    }

    public ShipType getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public List<Coordinate> getPositions() {
        return Collections.unmodifiableList(positions);
    }

    public void setPositions(List<Coordinate> newPositions) {
        positions.clear();
        positions.addAll(newPositions);
    }

    public void registerHit(Coordinate coordinate) {
        if (positions.contains(coordinate) && !hitPositions.contains(coordinate)) {
            hitPositions.add(coordinate);
        }
    }

    public boolean isSunk() {
        return hitPositions.size() == size;
    }

    public boolean occupies(Coordinate coordinate) {
        return positions.contains(coordinate);
    }

    public List<Coordinate> getHitPositions() {
        return Collections.unmodifiableList(hitPositions);
    }

    public String getDisplayName() {
        return type.getDisplayName();
    }
}