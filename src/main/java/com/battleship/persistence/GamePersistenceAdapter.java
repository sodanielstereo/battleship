package com.battleship.persistence;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.battleship.model.Game;

/**
 * Adapter class that implements the Adapter pattern (GoF).
 *
 * This adapter wraps two different persistence services
 * ({@link GameStatePersistenceService} and {@link PlayerStatsFileService}) and
 * exposes a unified interface through {@link GamePersistenceTarget}.
 *
 * The controller layer depends only on this adapter instead of calling two
 * separate services. This simplifies the client code and allows replacing the
 * underlying storage formats without modifying the controller.
 */
public class GamePersistenceAdapter implements GamePersistenceTarget {

    private final GameStatePersistenceService binaryService;
    private final PlayerStatsFileService textService;

    /**
     * Creates an adapter that uses the default binary and text services.
     */
    public GamePersistenceAdapter() {
        this.binaryService = new GameStatePersistenceService();
        this.textService = new PlayerStatsFileService();
    }

    /**
     * Creates an adapter with explicit service instances (useful for testing).
     *
     * @param binaryService service used for binary serialization.
     * @param textService   service used for CSV text persistence.
     */
    public GamePersistenceAdapter(
            GameStatePersistenceService binaryService,
            PlayerStatsFileService textService) {
        this.binaryService = binaryService;
        this.textService = textService;
    }

    /**
     * Saves a game state using binary serialization and also writes player
     * statistics to a text file.
     *
     * @param game          game state to persist.
     * @param binaryPath    path for the binary serialized file.
     * @param statsPath     path for the player statistics text file.
     * @throws IOException if any of the two files cannot be written.
     */
    @Override
    public void saveGame(Game game, Path binaryPath, Path statsPath) throws IOException {
        binaryService.saveGame(game, binaryPath);
        textService.savePlayerStats(game, statsPath);
    }

    /**
     * Loads a game state from a binary serialized file.
     *
     * @param binaryPath path for the binary serialized file.
     * @return loaded game state.
     * @throws IOException            if the file does not exist or cannot be read.
     * @throws ClassNotFoundException if the serialized class definition cannot be
     *                                resolved.
     */
    @Override
    public Game loadGame(Path binaryPath) throws IOException, ClassNotFoundException {
        return binaryService.loadGame(binaryPath);
    }

    /**
     * Saves player statistics to a text file without saving the full game state.
     *
     * @param game     game used to extract statistics.
     * @param statsPath path for the player statistics text file.
     * @throws IOException if the statistics file cannot be written.
     */
    @Override
    public void saveStats(Game game, Path statsPath) throws IOException {
        textService.savePlayerStats(game, statsPath);
    }

    /**
     * Reads the persisted player statistics from a text file.
     *
     * @param statsPath path for the player statistics text file.
     * @return list of text lines, or an empty list if the file does not exist.
     * @throws IOException if the file exists but cannot be read.
     */
    @Override
    public List<String> readStats(Path statsPath) throws IOException {
        return textService.readStats(statsPath);
    }

    /**
     * Deletes the binary game state file.
     *
     * @param binaryPath path for the binary serialized file.
     * @throws IOException if the file cannot be deleted.
     */
    @Override
    public void deleteGameState(Path binaryPath) throws IOException {
        java.nio.file.Files.deleteIfExists(binaryPath);
    }
}

