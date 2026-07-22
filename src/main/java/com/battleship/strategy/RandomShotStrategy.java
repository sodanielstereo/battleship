package com.battleship.strategy;

import java.util.Queue;

import com.battleship.model.board.Board;
import com.battleship.model.board.Coordinate;

/**
 * Basic strategy: the machine fires randomly using the queue
 * of available coordinates.
 */
public class RandomShotStrategy implements ShotStrategy {

    private static final long serialVersionUID = 1L;

    @Override
    public Coordinate selectShot(Board targetBoard, Queue<Coordinate> availableShots) {
        if (availableShots == null || availableShots.isEmpty()) {
            return null;
        }

        return availableShots.poll();
    }
}