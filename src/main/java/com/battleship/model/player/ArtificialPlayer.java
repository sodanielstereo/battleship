package com.battleship.model.player;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import com.battleship.model.board.Board;
import com.battleship.model.board.Coordinate;

/**
 * Representa al jugador artificial o máquina.
 */
public class ArtificialPlayer extends Player {

    private static final long serialVersionUID = 1L;

    private final Queue<Coordinate> availableShots;

    public ArtificialPlayer() {
        super("Máquina");
        this.availableShots = new ArrayDeque<>();
        reloadAvailableShots(Board.DEFAULT_SIZE);
    }

    public Coordinate nextAvailableShot() {
        if (availableShots.isEmpty()) {
            reloadAvailableShots(Board.DEFAULT_SIZE);
        }

        return availableShots.poll();
    }

    public int getRemainingShotsCount() {
        return availableShots.size();
    }

    public void reloadAvailableShots(int boardSize) {
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