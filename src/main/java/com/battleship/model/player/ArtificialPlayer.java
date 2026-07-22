package com.battleship.model.player;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import com.battleship.model.board.Board;
import com.battleship.model.board.Coordinate;
import com.battleship.strategy.ShotStrategy;
import com.battleship.strategy.SmartShotStrategy;

/**
 * Represents an artificial player in the Battleship game, capable of selecting
 * shots based on a defined strategy.
 */
public class ArtificialPlayer extends Player {

    private static final long serialVersionUID = 1L;

    private final Queue<Coordinate> availableShots;
    private ShotStrategy shotStrategy;

    public ArtificialPlayer() {
        super("Máquina");
        this.availableShots = new ArrayDeque<>();
        this.shotStrategy = new SmartShotStrategy();
        loadAvailableShots(Board.DEFAULT_SIZE);
    }

    public Coordinate nextAvailableShot() {
        return nextAvailableShot(null);
    }

    public Coordinate nextAvailableShot(Board targetBoard) {
        ensureShotStrategy();

        if (availableShots.isEmpty()) {
            loadAvailableShots(Board.DEFAULT_SIZE);
        }

        return shotStrategy.selectShot(targetBoard, availableShots);
    }

    public int getRemainingShotsCount() {
        return availableShots.size();
    }

    public void reloadAvailableShots(int boardSize) {
        loadAvailableShots(boardSize);
    }

    public ShotStrategy getShotStrategy() {
        ensureShotStrategy();
        return shotStrategy;
    }

    public void setShotStrategy(ShotStrategy shotStrategy) {
        if (shotStrategy == null) {
            this.shotStrategy = new SmartShotStrategy();
            return;
        }

        this.shotStrategy = shotStrategy;
    }

    private void ensureShotStrategy() {
        if (shotStrategy == null) {
            shotStrategy = new SmartShotStrategy();
        }
    }

    private void loadAvailableShots(int boardSize) {
        List<Coordinate> coordinates = new ArrayList<>();

        for (int row = 0; row < boardSize; row++) {
            for (int column = 0; column < boardSize; column++) {
                coordinates.add(new Coordinate(row, column));
            }
        }

        Collections.shuffle(coordinates);
        availableShots.clear();
        availableShots.addAll(coordinates);
    }
}