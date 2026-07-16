package com.battleship.exception;

/**
 * Excepción marcada para representar acciones no permitidas según el estado actual del juego.
 *
 * Al ser marcada, obliga a las capas superiores del proyecto a manejarla
 * con try-catch o declararla con throws.
 */
public class InvalidGameStateException extends Exception {

    public InvalidGameStateException(String message) {
        super(message);
    }
}