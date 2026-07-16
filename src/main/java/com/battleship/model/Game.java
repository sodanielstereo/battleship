package com.battleship.model;

import java.io.Serializable;

import com.battleship.model.enums.GamePhase;
import com.battleship.model.enums.Turn;
import com.battleship.model.player.ArtificialPlayer;
import com.battleship.model.player.RealPlayer;

/**
 * Representa el estado general de una partida de Batalla Naval.
 */
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    private final RealPlayer humanPlayer;
    private final ArtificialPlayer machinePlayer;
    private GamePhase phase;
    private Turn currentTurn;

    public Game(String humanNickname) {
        this.humanPlayer = new RealPlayer(humanNickname);
        this.machinePlayer = new ArtificialPlayer();
        this.phase = GamePhase.PLACEMENT;
        this.currentTurn = Turn.HUMAN;
    }

    public RealPlayer getHumanPlayer() {
        return humanPlayer;
    }

    public ArtificialPlayer getMachinePlayer() {
        return machinePlayer;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    public Turn getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(Turn currentTurn) {
        this.currentTurn = currentTurn;
    }

    public boolean isFinished() {
        return phase == GamePhase.FINISHED;
    }
}