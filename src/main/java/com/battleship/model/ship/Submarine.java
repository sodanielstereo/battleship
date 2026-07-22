package com.battleship.model.ship;

import com.battleship.model.enums.ShipType;

/**
 * Represents a submarine ship, which occupies three board cells.
 */
public class Submarine extends Ship {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a submarine with the configured type and size.
     */
    public Submarine() {
        super(ShipType.SUBMARINE);
    }
}
