package com.battleship.model.ship.factory;

import com.battleship.model.ship.Destroyer;
import com.battleship.model.ship.Ship;

/**
 * Concrete factory that creates {@link Destroyer} instances.
 *
 * The destroyer occupies two cells on the board and is one of the smaller
 * ship types available. This factory is automatically registered in
 * {@link ShipFactoryRegistry} for
 * {@link com.battleship.model.enums.ShipType#DESTROYER}.
 *
 * @see ShipFactory
 * @see ShipFactoryRegistry
 * @see Destroyer
 */
public class DestroyerFactory implements ShipFactory {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@link Destroyer} instance.
     *
     * @return a new destroyer ship, never {@code null}.
     */
    @Override
    public Ship createShip() {
        return new Destroyer();
    }
}

