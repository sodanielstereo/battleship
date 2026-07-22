package com.battleship.model.enums;

/**
 * Represents the logical state of a board cell.
 *
 * The state is used both by the game services and by the JavaFX layer to decide
 * which visual style or sprite should be displayed.
 */
public enum CellState {
    EMPTY,
    SHIP,
    WATER,
    HIT,
    SUNK
}
