package com.battleship.model.ship;

import com.battleship.model.enums.ShipType;

/**
 * Represents a destroyer ship, which occupies two board cells.
 */
public class Destroyer extends Ship {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a destroyer with the configured type and size.
     */
    public Destroyer() {
        super(ShipType.DESTROYER);
    }
}
