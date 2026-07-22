package com.battleship.model.ship;

import com.battleship.model.enums.ShipType;

/**
 * Represents a frigate ship, which occupies one board cell.
 */
public class Frigate extends Ship {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a frigate with the configured type and size.
     */
    public Frigate() {
        super(ShipType.FRIGATE);
    }
}
