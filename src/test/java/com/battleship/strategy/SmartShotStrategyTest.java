package com.battleship.strategy;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.battleship.model.board.Board;
import com.battleship.model.board.Coordinate;

class SmartShotStrategyTest {

    @Test
    void strategyShouldPrioritizeNeighborsAfterHit() {
        Board targetBoard = new Board();
        SmartShotStrategy strategy = new SmartShotStrategy();
        Queue<Coordinate> availableShots = createAvailableShots();

        Coordinate hitCoordinate = new Coordinate(4, 4);
        targetBoard.getCell(hitCoordinate).markAsHit();

        Coordinate selectedCoordinate = strategy.selectShot(targetBoard, availableShots);

        Set<Coordinate> expectedNeighbors = Set.of(
                new Coordinate(3, 4),
                new Coordinate(5, 4),
                new Coordinate(4, 3),
                new Coordinate(4, 5));

        assertTrue(expectedNeighbors.contains(selectedCoordinate));
        assertFalse(availableShots.contains(selectedCoordinate));
    }

    @Test
    void strategyShouldIgnoreNeighborsAlreadyShot() {
        Board targetBoard = new Board();
        SmartShotStrategy strategy = new SmartShotStrategy();
        Queue<Coordinate> availableShots = createAvailableShots();

        Coordinate hitCoordinate = new Coordinate(4, 4);
        Coordinate alreadyShotCoordinate = new Coordinate(3, 4);

        targetBoard.getCell(hitCoordinate).markAsHit();
        targetBoard.getCell(alreadyShotCoordinate).markAsWater();

        Coordinate selectedCoordinate = strategy.selectShot(targetBoard, availableShots);

        Set<Coordinate> validNeighbors = Set.of(
                new Coordinate(5, 4),
                new Coordinate(4, 3),
                new Coordinate(4, 5));

        assertTrue(validNeighbors.contains(selectedCoordinate));
        assertFalse(selectedCoordinate.equals(alreadyShotCoordinate));
    }

    private Queue<Coordinate> createAvailableShots() {
        Queue<Coordinate> availableShots = new ArrayDeque<>();

        for (int row = 0; row < Board.DEFAULT_SIZE; row++) {
            for (int column = 0; column < Board.DEFAULT_SIZE; column++) {
                availableShots.add(new Coordinate(row, column));
            }
        }

        return availableShots;
    }
}