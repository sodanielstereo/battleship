package com.battleship.model.ship;

import com.battleship.model.enums.ShipType;

/**
 * Representa un submarino, que ocupa 3 casillas.
 */
public class Submarine extends Ship {

    private static final long serialVersionUID = 1L;

    public Submarine() {
        super(ShipType.SUBMARINE);
    }
}