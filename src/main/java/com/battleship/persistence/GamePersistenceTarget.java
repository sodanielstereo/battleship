package com.battleship.persistence;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.battleship.model.Game;

/**
 * Target interface of the Adapter pattern.
 *
 * Defines the high-level persistence operations that the application
 * needs, independent of the concrete storage format (binary serialization,
 * CSV, JSON, database, etc.).
 *
 * This interface allows the controller layer to depend on an abstraction
 * instead of coupling directly to specific persistence implementations.
 *
 * @see GamePersistenceAdapter
 */
public interface GamePersistenceTarget {

    /**
     * Saves the complete game state (binary serialization) and player
     * statistics (CSV text file).
     *
     * @param game       the game state to persist.
     * @param binaryPath path for the binary serialized file.
     * @param statsPath  path for the player statistics text file.
     * @throws IOException if any of the operations fails.
     */
    void saveGame(Game game, Path binaryPath, Path statsPath) throws IOException;

    /**
     * Loads a previously saved game state from a binary file.
     *
     * @param binaryPath path for the binary serialized file.
     * @return the loaded game instance.
     * @throws IOException            if the file cannot be read.
     * @throws ClassNotFoundException if the serialized class is not found.
     */
    Game loadGame(Path binaryPath) throws IOException, ClassNotFoundException;

    /**
     * Saves only player statistics in a human-readable format.
     *
     * @param game      the game used to extract statistics.
     * @param statsPath path for the player statistics text file.
     * @throws IOException if the operation fails.
     */
    void saveStats(Game game, Path statsPath) throws IOException;

    /**
     * Deletes the binary game state file.
     *
     * @param binaryPath path for the binary serialized file.
     * @throws IOException if the file cannot be deleted.
     */
    void deleteGameState(Path binaryPath) throws IOException;

    /**
     * Reads the persisted player statistics from a text file.
     *
     * @param statsPath path for the player statistics text file.
     * @return list of text lines, or an empty list if the file does not exist.
     * @throws IOException if the file exists but cannot be read.
     */
    List<String> readStats(Path statsPath) throws IOException;
}

