package com.battleship.model.history;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.battleship.model.board.Coordinate;
import com.battleship.model.enums.ShotResult;
import com.battleship.model.enums.Turn;

/**
 * Representa el registro de un disparo realizado durante la partida.
 */
public class ShotRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Turn shooter;
    private final Coordinate coordinate;
    private final ShotResult result;
    private final LocalDateTime timestamp;

    public ShotRecord(Turn shooter, Coordinate coordinate, ShotResult result) {
        this.shooter = shooter;
        this.coordinate = coordinate;
        this.result = result;
        this.timestamp = LocalDateTime.now();
    }

    public Turn getShooter() {
        return shooter;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public ShotResult getResult() {
        return result;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}