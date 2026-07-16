package com.battleship.model;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.battleship.model.board.Board;
import com.battleship.model.board.Coordinate;
import com.battleship.model.player.ArtificialPlayer;
import com.battleship.model.player.RealPlayer;
import com.battleship.model.ship.Frigate;

class DataStructuresTest {

    @Test
    void boardShouldUseCoordinateTableWithOneHundredCells() {
        Board board = new Board();

        assertEquals(100, board.getCells().size());
        assertTrue(board.getCells().containsKey(new Coordinate(0, 0)));
        assertTrue(board.getCells().containsKey(new Coordinate(9, 9)));
    }

    @Test
    void playerFleetShouldStoreShipsInList() {
        RealPlayer player = new RealPlayer("Daniel");

        player.addShip(new Frigate());
        player.addShip(new Frigate());

        assertEquals(2, player.getFleet().size());
    }

    @Test
    void artificialPlayerShotQueueShouldAvoidRepeatedCoordinates() {
        ArtificialPlayer artificialPlayer = new ArtificialPlayer();
        Set<Coordinate> shots = new HashSet<>();

        for (int i = 0; i < 100; i++) {
            Coordinate coordinate = artificialPlayer.nextAvailableShot();
            assertTrue(shots.add(coordinate));
        }

        assertEquals(100, shots.size());
        assertEquals(0, artificialPlayer.getRemainingShotsCount());
    }
}