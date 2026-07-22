package com.battleship.model.ship.factory;

import com.battleship.model.ship.Ship;
import com.battleship.model.ship.Submarine;

/**
 * Concrete factory that creates {@link Submarine} instances.
 *
 * The submarine occupies three cells on the board and is the second largest
 * ship type in the game. This factory is automatically registered in
 * {@link ShipFactoryRegistry} for
 * {@link com.battleship.model.enums.ShipType#SUBMARINE}.
 *
 * @see ShipFactory
 * @see ShipFactoryRegistry
 * @see Submarine
 */
public class SubmarineFactory implements ShipFactory {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@link Submarine} instance.
     *
     * @return a new submarine ship, never {@code null}.
     */
    @Override
    public Ship createShip() {
        return new Submarine();
    }
}

