package com.battleship.exception;

/**
 * Excepción no marcada para representar errores durante la colocación de barcos.
 *
 * Se usa cuando un barco se intenta ubicar fuera del tablero,
 * sobre otro barco o con datos inválidos.
 */
public class InvalidPlacementException extends RuntimeException {

    public InvalidPlacementException(String message) {
        super(message);
    }
}