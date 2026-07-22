package com.battleship.model.ship;

import com.battleship.model.enums.ShipType;

/**
 * Represents the aircraft carrier ship, which occupies four board cells.
 */
public class AircraftCarrier extends Ship {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an aircraft carrier with the configured type and size.
     */
    public AircraftCarrier() {
        super(ShipType.AIRCRAFT_CARRIER);
    }
}
