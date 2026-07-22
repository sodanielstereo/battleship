package com.battleship.strategy;

import java.io.Serializable;
import java.util.Queue;

import com.battleship.model.board.Board;
import com.battleship.model.board.Coordinate;

/**
 * Defines the Strategy pattern contract used by the artificial player.
 *
 * Implementations decide which coordinate the machine should attack next using
 * the target board and the queue of coordinates that have not been selected yet.
 */
public interface ShotStrategy extends Serializable {

    /**
     * Selects the next coordinate to attack.
     *
     * @param targetBoard opponent board used as context by smart strategies.
     * @param availableShots queue containing coordinates that have not been selected yet.
     * @return selected coordinate, or {@code null} when there are no available shots.
     */
    Coordinate selectShot(Board targetBoard, Queue<Coordinate> availableShots);
}
