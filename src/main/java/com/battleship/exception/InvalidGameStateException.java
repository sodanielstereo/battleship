package com.battleship.exception;

/**
 * Checked exception used when an operation is not allowed in the current game state.
 *
 * Because it is checked, controllers and services must explicitly handle or
 * declare it when they perform operations that depend on the current phase or turn.
 */
public class InvalidGameStateException extends Exception {

    /**
     * Creates an exception with a user-readable game state error message.
     *
     * @param message explanation of the invalid state.
     */
    public InvalidGameStateException(String message) {
        super(message);
    }
}
