package com.battleship.model.player;

import com.battleship.model.board.Board;
import com.battleship.model.ship.Ship;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a player in the Battleship game.
 * Player
 */
public abstract class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String nickname;
    private final Board board;
    private final List<Ship> fleet;

    protected Player(String nickname) {
        this.nickname = nickname;
        this.board = new Board();
        this.fleet = new ArrayList<>();
    }

    public String getNickname() {
        return nickname;
    }

    public Board getBoard() {
        return board;
    }

    public List<Ship> getFleet() {
        return Collections.unmodifiableList(fleet);
    }

    public void addShip(Ship ship) {
        fleet.add(ship);
    }

    public boolean hasLost() {
        return !fleet.isEmpty() && fleet.stream().allMatch(Ship::isSunk);
    }

    public int getSunkShipsCount() {
        return (int) fleet.stream()
                .filter(Ship::isSunk)
                .count();
    }
}