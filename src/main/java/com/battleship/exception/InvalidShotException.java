package com.battleship.exception;

/**
 * Excepción no marcada para representar errores al realizar disparos.
 *
 * Se usa cuando el disparo está fuera del tablero, la casilla ya fue atacada
 * o los datos del disparo son inválidos.
 */
public class InvalidShotException extends RuntimeException {

    public InvalidShotException(String message) {
        super(message);
    }
}