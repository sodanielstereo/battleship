package com.battleship.exception;

/**
 * Unchecked exception used when a shot operation is invalid.
 *
 * It is thrown when the target board or coordinate is invalid, when the shot is
 * outside the board, or when the selected cell was already attacked.
 */
public class InvalidShotException extends RuntimeException {

    /**
     * Creates an exception with a user-readable shot error message.
     *
     * @param message explanation of the invalid shot.
     */
    public InvalidShotException(String message) {
        super(message);
    }
}
