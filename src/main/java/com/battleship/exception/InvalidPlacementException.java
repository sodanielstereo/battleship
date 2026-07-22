package com.battleship.exception;

/**
 * Unchecked exception used when a ship placement operation is invalid.
 *
 * It is thrown when a ship is placed outside the board, overlaps another ship,
 * uses invalid placement data, or cannot be moved to the requested position.
 */
public class InvalidPlacementException extends RuntimeException {

    /**
     * Creates an exception with a user-readable placement error message.
     *
     * @param message explanation of the invalid placement.
     */
    public InvalidPlacementException(String message) {
        super(message);
    }
}
