package com.battleship.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import com.battleship.model.board.Board;
import com.battleship.model.board.Cell;
import com.battleship.model.board.Coordinate;
import com.battleship.model.enums.CellState;

/**
 * Smart machine shooting strategy.
 *
 * It scans the target board looking for hit cells and prioritizes adjacent cells
 * that have not been attacked yet. If no useful candidate exists, it falls back to
 * the shuffled queue of available shots.
 */
public class SmartShotStrategy implements ShotStrategy {

    private static final long serialVersionUID = 1L;

    /**
     * Selects the next shot using hit-neighbor prioritization.
     *
     * @param targetBoard opponent board used to find previous hits.
     * @param availableShots queue of coordinates that have not been selected yet.
     * @return selected coordinate, or {@code null} when the queue is empty.
     */
    @Override
    public Coordinate selectShot(Board targetBoard, Queue<Coordinate> availableShots) {
        if (availableShots == null || availableShots.isEmpty()) {
            return null;
        }

        if (targetBoard == null) {
            return availableShots.poll();
        }

        List<Coordinate> targetCandidates = findTargetCandidates(targetBoard, availableShots);

        if (!targetCandidates.isEmpty()) {
            Collections.shuffle(targetCandidates);
            Coordinate selectedCoordinate = targetCandidates.get(0);
            availableShots.remove(selectedCoordinate);
            return selectedCoordinate;
        }

        return availableShots.poll();
    }

    /**
     * Finds valid neighboring coordinates around cells already marked as hit.
     *
     * @param targetBoard opponent board to scan.
     * @param availableShots remaining coordinates that can still be selected.
     * @return candidate coordinates that should be prioritized.
     */
    private List<Coordinate> findTargetCandidates(Board targetBoard, Queue<Coordinate> availableShots) {
        List<Coordinate> candidates = new ArrayList<>();

        for (int row = 0; row < targetBoard.getSize(); row++) {
            for (int column = 0; column < targetBoard.getSize(); column++) {
                Coordinate coordinate = new Coordinate(row, column);
                Cell cell = targetBoard.getCell(coordinate);

                if (cell != null && cell.getState() == CellState.HIT) {
                    addValidNeighbors(targetBoard, availableShots, candidates, coordinate);
                }
            }
        }

        return candidates;
    }

    /**
     * Adds the four orthogonal neighbors of a hit coordinate when they are valid.
     *
     * @param targetBoard opponent board.
     * @param availableShots remaining coordinates that can still be selected.
     * @param candidates current candidate list.
     * @param coordinate hit coordinate used as center.
     */
    private void addValidNeighbors(
            Board targetBoard,
            Queue<Coordinate> availableShots,
            List<Coordinate> candidates,
            Coordinate coordinate) {

        addCandidate(targetBoard, availableShots, candidates,
                new Coordinate(coordinate.getRow() - 1, coordinate.getColumn()));
        addCandidate(targetBoard, availableShots, candidates,
                new Coordinate(coordinate.getRow() + 1, coordinate.getColumn()));
        addCandidate(targetBoard, availableShots, candidates,
                new Coordinate(coordinate.getRow(), coordinate.getColumn() - 1));
        addCandidate(targetBoard, availableShots, candidates,
                new Coordinate(coordinate.getRow(), coordinate.getColumn() + 1));
    }

    /**
     * Adds one coordinate to the candidate list when it is valid and still available.
     *
     * @param targetBoard opponent board.
     * @param availableShots remaining coordinates that can still be selected.
     * @param candidates current candidate list.
     * @param coordinate coordinate to evaluate.
     */
    private void addCandidate(
            Board targetBoard,
            Queue<Coordinate> availableShots,
            List<Coordinate> candidates,
            Coordinate coordinate) {

        if (!targetBoard.isInside(coordinate)) {
            return;
        }

        Cell cell = targetBoard.getCell(coordinate);

        if (cell == null || cell.wasShot()) {
            return;
        }

        if (availableShots.contains(coordinate) && !candidates.contains(coordinate)) {
            candidates.add(coordinate);
        }
    }
}
