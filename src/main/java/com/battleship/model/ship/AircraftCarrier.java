package com.battleship.model.ship;

import com.battleship.model.enums.ShipType;

/**
 * Representa el portaaviones, que ocupa 4 casillas.
 */
public class AircraftCarrier extends Ship {

    private static final long serialVersionUID = 1L;

    public AircraftCarrier() {
        super(ShipType.AIRCRAFT_CARRIER);
    }
}