package com.battleship.model.history;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.battleship.model.board.Coordinate;
import com.battleship.model.enums.ShotResult;
import com.battleship.model.enums.Turn;

/**
 * Represents one shot registered during a match.
 *
 * The record stores the player who shot, the selected coordinate, the result, and
 * the timestamp when the shot was created.
 */
public class ShotRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Turn shooter;
    private final Coordinate coordinate;
    private final ShotResult result;
    private final LocalDateTime timestamp;

    /**
     * Creates a new shot record using the current timestamp.
     *
     * @param shooter player that performed the shot.
     * @param coordinate coordinate selected for the shot.
     * @param result result produced by the shot.
     */
    public ShotRecord(Turn shooter, Coordinate coordinate, ShotResult result) {
        this.shooter = shooter;
        this.coordinate = coordinate;
        this.result = result;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Returns the player who performed the shot.
     *
     * @return shooter turn value.
     */
    public Turn getShooter() {
        return shooter;
    }

    /**
     * Returns the coordinate selected by the shot.
     *
     * @return shot coordinate.
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Returns the result produced by the shot.
     *
     * @return shot result.
     */
    public ShotResult getResult() {
        return result;
    }

    /**
     * Returns the timestamp when the record was created.
     *
     * @return shot timestamp.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
