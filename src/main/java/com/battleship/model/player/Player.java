package com.battleship.model.player;

import com.battleship.model.board.Board;
import com.battleship.model.ship.Ship;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for every Battleship player.
 *
 * A player owns a board and a fleet. Concrete subclasses represent the human
 * player and the artificial machine opponent.
 */
public abstract class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String nickname;
    private final Board board;
    private final List<Ship> fleet;

    /**
     * Creates a player with an empty board and an empty fleet.
     *
     * @param nickname nickname assigned to the player.
     */
    protected Player(String nickname) {
        this.nickname = nickname;
        this.board = new Board();
        this.fleet = new ArrayList<>();
    }

    /**
     * Returns the player nickname.
     *
     * @return player nickname.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Returns the board owned by the player.
     *
     * @return player board.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Returns an immutable view of the player fleet.
     *
     * @return ships owned by the player.
     */
    public List<Ship> getFleet() {
        return Collections.unmodifiableList(fleet);
    }

    /**
     * Adds a ship to the player fleet.
     *
     * @param ship ship to add.
     */
    public void addShip(Ship ship) {
        fleet.add(ship);
    }

    /**
     * Indicates whether all ships in the fleet are sunk.
     *
     * @return {@code true} if the fleet is not empty and every ship is sunk; otherwise {@code false}.
     */
    public boolean hasLost() {
        return !fleet.isEmpty() && fleet.stream().allMatch(Ship::isSunk);
    }

    /**
     * Counts how many ships in the fleet are already sunk.
     *
     * @return number of sunk ships.
     */
    public int getSunkShipsCount() {
        return (int) fleet.stream()
                .filter(Ship::isSunk)
                .count();
    }
}
