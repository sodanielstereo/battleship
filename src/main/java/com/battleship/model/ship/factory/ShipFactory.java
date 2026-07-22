package com.battleship.model.ship.factory;

import com.battleship.model.ship.Ship;

/**
 * Product interface of the Factory Method pattern (GoF).
 *
 * Each concrete implementation of this interface knows how to create a
 * specific type of {@link Ship}. The factory interface decouples the
 * client code from the concrete ship classes, so that new ship types can
 * be added without modifying existing code.
 *
 * To register a new ship type, create a class implementing this interface
 * and register it in {@link ShipFactoryRegistry}.
 *
 * @see ShipFactoryRegistry
 * @see com.battleship.model.ship.Ship
 */
public interface ShipFactory {

    /**
     * Creates a new ship instance of the type handled by this factory.
     *
     * @return a new ship instance, never {@code null}.
     */
    Ship createShip();
}

