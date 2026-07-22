package com.battleship.model.ship.factory;

import com.battleship.model.ship.AircraftCarrier;
import com.battleship.model.ship.Ship;

/**
 * Concrete factory that creates {@link AircraftCarrier} instances.
 *
 * The aircraft carrier is the largest ship in the game, occupying four cells
 * on the board. This factory is automatically registered in
 * {@link ShipFactoryRegistry} for
 * {@link com.battleship.model.enums.ShipType#AIRCRAFT_CARRIER}.
 *
 * @see ShipFactory
 * @see ShipFactoryRegistry
 * @see AircraftCarrier
 */
public class AircraftCarrierFactory implements ShipFactory {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@link AircraftCarrier} instance.
     *
     * @return a new aircraft carrier ship, never {@code null}.
     */
    @Override
    public Ship createShip() {
        return new AircraftCarrier();
    }
}

