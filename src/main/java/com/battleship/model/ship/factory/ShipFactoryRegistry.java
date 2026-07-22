package com.battleship.model.ship.factory;

import java.util.EnumMap;
import java.util.Map;

import com.battleship.model.enums.ShipType;
import com.battleship.model.ship.Ship;

/**
 * Registry that maps each {@link ShipType} to its corresponding
 * {@link ShipFactory} implementation.
 *
 * This is a utility class (non-instantiable) that acts as the central access
 * point for creating ships throughout the application. Client code calls
 * {@link #createShip(ShipType)} instead of using the {@code new} keyword on
 * concrete ship classes.
 *
 * The registry is initialized with the four default ship types:
 * {@link ShipType#AIRCRAFT_CARRIER}, {@link ShipType#SUBMARINE},
 * {@link ShipType#DESTROYER}, and {@link ShipType#FRIGATE}. Additional
 * factories can be added at runtime using
 * {@link #registerFactory(ShipType, ShipFactory)}.
 *
 * @see ShipFactory
 * @see ShipType
 * @see Ship
 */
public final class ShipFactoryRegistry {

    private static final Map<ShipType, ShipFactory> factories = new EnumMap<>(ShipType.class);

    static {
        factories.put(ShipType.AIRCRAFT_CARRIER, new AircraftCarrierFactory());
        factories.put(ShipType.SUBMARINE, new SubmarineFactory());
        factories.put(ShipType.DESTROYER, new DestroyerFactory());
        factories.put(ShipType.FRIGATE, new FrigateFactory());
    }

    private ShipFactoryRegistry() {
        // Utility class -- prevents instantiation.
    }

    /**
     * Creates a new ship of the given type using the registered factory.
     *
     * @param shipType the type of ship to create. Must not be {@code null}.
     * @return a new ship instance, never {@code null}.
     * @throws IllegalArgumentException if no factory is registered for the given
     *         {@code shipType}.
     */
    public static Ship createShip(ShipType shipType) {
        ShipFactory factory = factories.get(shipType);

        if (factory == null) {
            throw new IllegalArgumentException(
                    "No hay una fábrica registrada para el tipo de nave: " + shipType);
        }

        return factory.createShip();
    }

    /**
     * Returns the factory registered for a specific ship type.
     *
     * This method is useful when the factory itself needs to be passed as
     * a dependency or when additional configuration is required before
     * creating a ship.
     *
     * @param shipType the ship type whose factory is requested. Must not be
     *        {@code null}.
     * @return the factory registered for the given type, never {@code null}.
     * @throws IllegalArgumentException if no factory is registered for the given
     *         {@code shipType}.
     */
    public static ShipFactory getFactory(ShipType shipType) {
        ShipFactory factory = factories.get(shipType);

        if (factory == null) {
            throw new IllegalArgumentException(
                    "No hay una fábrica registrada para el tipo de nave: " + shipType);
        }

        return factory;
    }

    /**
     * Registers a new factory for a ship type, or replaces an existing one.
     *
     * This method allows adding new ship types at runtime or overriding the
     * default factory for testing purposes, such as mocking ship creation.
     *
     * @param shipType the ship type to associate. Must not be {@code null}.
     * @param factory  the factory that creates ships of that type. Must not be
     *        {@code null}.
     */
    public static void registerFactory(ShipType shipType, ShipFactory factory) {
        factories.put(shipType, factory);
    }
}

