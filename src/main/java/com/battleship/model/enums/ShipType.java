package com.battleship.model.enums;

/**
 * Representa los tipos de barcos permitidos en Batalla Naval.
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

    public String getDisplayName() {
        return displayName;
    }

    public int getSize() {
        return size;
    }
}