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
 * Smart strategy: the machine fires intelligently by targeting adjacent cells
 */
public class SmartShotStrategy implements ShotStrategy {

    private static final long serialVersionUID = 1L;

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