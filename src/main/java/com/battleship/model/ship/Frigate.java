package com.battleship.model.ship;

import com.battleship.model.enums.ShipType;

/**
 * Representa una fragata, que ocupa 1 casilla.
 */
public class Frigate extends Ship {

    private static final long serialVersionUID = 1L;

    public Frigate() {
        super(ShipType.FRIGATE);
    }
}