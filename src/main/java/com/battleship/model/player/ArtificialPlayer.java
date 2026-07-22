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
 * Represents the artificial Battleship opponent.
 *
 * The machine keeps a shuffled queue of available shots and delegates shot
 * selection to a {@link com.battleship.strategy.ShotStrategy}. This allows the
 * shooting behavior to change without modifying the game service.
 */
public class ArtificialPlayer extends Player {

    private static final long serialVersionUID = 1L;

    private final Queue<Coordinate> availableShots;
    private ShotStrategy shotStrategy;

    /**
     * Creates the machine player with the default smart shooting strategy.
     */
    public ArtificialPlayer() {
        super("Máquina");
        this.availableShots = new ArrayDeque<>();
        this.shotStrategy = new SmartShotStrategy();
        loadAvailableShots(Board.DEFAULT_SIZE);
    }

    /**
     * Selects the next machine shot without using board information.
     *
     * @return next available coordinate, or {@code null} if none exists.
     */
    public Coordinate nextAvailableShot() {
        return nextAvailableShot(null);
    }

    /**
     * Selects the next machine shot using the configured strategy.
     *
     * @param targetBoard opponent board used by smart strategies.
     * @return selected coordinate, or {@code null} if no shots are available.
     */
    public Coordinate nextAvailableShot(Board targetBoard) {
        ensureShotStrategy();

        if (availableShots.isEmpty()) {
            loadAvailableShots(Board.DEFAULT_SIZE);
        }

        return shotStrategy.selectShot(targetBoard, availableShots);
    }

    /**
     * Returns how many coordinates remain available for the machine.
     *
     * @return remaining shot count.
     */
    public int getRemainingShotsCount() {
        return availableShots.size();
    }

    /**
     * Rebuilds the shuffled queue of coordinates for a board size.
     *
     * @param boardSize size of the square board.
     */
    public void reloadAvailableShots(int boardSize) {
        loadAvailableShots(boardSize);
    }

    /**
     * Returns the current shooting strategy.
     *
     * @return active shot strategy.
     */
    public ShotStrategy getShotStrategy() {
        ensureShotStrategy();
        return shotStrategy;
    }

    /**
     * Changes the shooting strategy used by the machine.
     *
     * If the provided strategy is {@code null}, the smart strategy is restored as the
     * default behavior.
     *
     * @param shotStrategy strategy to assign.
     */
    public void setShotStrategy(ShotStrategy shotStrategy) {
        if (shotStrategy == null) {
            this.shotStrategy = new SmartShotStrategy();
            return;
        }

        this.shotStrategy = shotStrategy;
    }

    /**
     * Ensures that deserialized machine players always have a valid shot strategy.
     */
    private void ensureShotStrategy() {
        if (shotStrategy == null) {
            shotStrategy = new SmartShotStrategy();
        }
    }

    /**
     * Loads and shuffles every coordinate that the machine can still shoot.
     *
     * @param boardSize size of the square board.
     */
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
