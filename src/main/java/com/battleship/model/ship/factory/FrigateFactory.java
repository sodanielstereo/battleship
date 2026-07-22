package com.battleship.model.ship.factory;

import com.battleship.model.ship.Frigate;
import com.battleship.model.ship.Ship;

/**
 * Concrete factory that creates {@link Frigate} instances.
 *
 * The frigate is the smallest ship in the game, occupying a single cell on
 * the board. This factory is automatically registered in
 * {@link ShipFactoryRegistry} for
 * {@link com.battleship.model.enums.ShipType#FRIGATE}.
 *
 * @see ShipFactory
 * @see ShipFactoryRegistry
 * @see Frigate
 */
public class FrigateFactory implements ShipFactory {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@link Frigate} instance.
     *
     * @return a new frigate ship, never {@code null}.
     */
    @Override
    public Ship createShip() {
        return new Frigate();
    }
}

