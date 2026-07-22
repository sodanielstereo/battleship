package com.battleship.model;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import com.battleship.model.enums.GamePhase;
import com.battleship.model.enums.Turn;
import com.battleship.model.history.ShotRecord;
import com.battleship.model.player.ArtificialPlayer;
import com.battleship.model.player.RealPlayer;

/**
 * Represents the complete state of a Battleship match.
 *
 * The game stores the human player, the machine player, the current phase, the
 * current turn, and a shot history stack. It is serializable so a match can be
 * saved and resumed later.
 */
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    private final RealPlayer humanPlayer;
    private final ArtificialPlayer machinePlayer;
    private final Deque<ShotRecord> shotHistory;
    private GamePhase phase;
    private Turn currentTurn;

    /**
     * Creates a new match with a human player and a machine player.
     *
     * @param humanNickname nickname assigned to the real player.
     */
    public Game(String humanNickname) {
        this.humanPlayer = new RealPlayer(humanNickname);
        this.machinePlayer = new ArtificialPlayer();
        this.shotHistory = new ArrayDeque<>();
        this.phase = GamePhase.PLACEMENT;
        this.currentTurn = Turn.HUMAN;
    }

    /**
     * Returns the real player.
     *
     * @return human player of the match.
     */
    public RealPlayer getHumanPlayer() {
        return humanPlayer;
    }

    /**
     * Returns the artificial opponent.
     *
     * @return machine player of the match.
     */
    public ArtificialPlayer getMachinePlayer() {
        return machinePlayer;
    }

    /**
     * Returns the current lifecycle phase.
     *
     * @return current game phase.
     */
    public GamePhase getPhase() {
        return phase;
    }

    /**
     * Updates the current lifecycle phase.
     *
     * @param phase new game phase.
     */
    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    /**
     * Returns the player that must act next.
     *
     * @return current turn.
     */
    public Turn getCurrentTurn() {
        return currentTurn;
    }

    /**
     * Updates the current turn.
     *
     * @param currentTurn player that must act next.
     */
    public void setCurrentTurn(Turn currentTurn) {
        this.currentTurn = currentTurn;
    }

    /**
     * Adds a shot record to the beginning of the shot history.
     *
     * @param shotRecord shot information to register.
     */
    public void addShotRecord(ShotRecord shotRecord) {
        shotHistory.push(shotRecord);
    }

    /**
     * Returns an immutable copy of the shot history.
     *
     * @return list of shot records, with the most recent record first.
     */
    public List<ShotRecord> getShotHistory() {
        return List.copyOf(shotHistory);
    }

    /**
     * Indicates whether the match has already ended.
     *
     * @return {@code true} when the current phase is {@link GamePhase#FINISHED}; otherwise {@code false}.
     */
    public boolean isFinished() {
        return phase == GamePhase.FINISHED;
    }
}
