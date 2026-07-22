package com.battleship.model.enums;

/**
 * Represents the ship types available in the game.
 *
 * Each type stores a display name and the number of cells occupied by the ship.
 */
public enum ShipType {
    AIRCRAFT_CARRIER("Portaaviones", 4),
    SUBMARINE("Submarino", 3),
    DESTROYER("Destructor", 2),
    FRIGATE("Fragata", 1);

    private final String displayName;
    private final int size;

    ShipType(String displayName, int size) {
        this.displayName = displayName;
        this.size = size;
    }

    /**
     * Returns the user-facing ship name.
     *
     * @return display name shown in the interface.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the number of cells occupied by this ship type.
     *
     * @return ship size.
     */
    public int getSize() {
        return size;
    }
}
