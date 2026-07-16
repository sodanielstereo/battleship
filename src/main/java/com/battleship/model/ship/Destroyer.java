package com.battleship.model.ship;

import com.battleship.model.enums.ShipType;

/**
 * Representa un destructor, que ocupa 2 casillas.
 */
public class Destroyer extends Ship {

    private static final long serialVersionUID = 1L;

    public Destroyer() {
        super(ShipType.DESTROYER);
    }
}