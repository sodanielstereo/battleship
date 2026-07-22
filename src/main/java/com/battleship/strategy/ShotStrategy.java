package com.battleship.strategy;

import java.io.Serializable;
import java.util.Queue;

import com.battleship.model.board.Board;
import com.battleship.model.board.Coordinate;

/**
 * Defines the strategy for selecting shots in the Battleship game.
 */
public interface ShotStrategy extends Serializable {

    Coordinate selectShot(Board targetBoard, Queue<Coordinate> availableShots);
}