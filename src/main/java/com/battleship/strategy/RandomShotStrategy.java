package com.battleship.strategy;

import java.util.Queue;

import com.battleship.model.board.Board;
import com.battleship.model.board.Coordinate;

/**
 * Basic machine shooting strategy.
 *
 * It selects the next coordinate from the shuffled queue of available shots without
 * using information from the target board.
 */
public class RandomShotStrategy implements ShotStrategy {

    private static final long serialVersionUID = 1L;

    /**
     * Selects the next coordinate from the queue.
     *
     * @param targetBoard opponent board. This implementation does not use it.
     * @param availableShots queue of coordinates that have not been selected yet.
     * @return next coordinate, or {@code null} when the queue is empty.
     */
    @Override
    public Coordinate selectShot(Board targetBoard, Queue<Coordinate> availableShots) {
        if (availableShots == null || availableShots.isEmpty()) {
            return null;
        }

        return availableShots.poll();
    }
}
