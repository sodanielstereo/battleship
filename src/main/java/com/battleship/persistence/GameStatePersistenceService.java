package com.battleship.persistence;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.battleship.model.Game;

/**
 * Service responsible for saving and loading the complete game state.
 *
 * This class uses Java object serialization to persist a {@link Game} instance in
 * a binary file. It is used to resume an unfinished match from the main screen.
 */
public class GameStatePersistenceService {

    /**
     * Serializes a game state into the provided file path.
     *
     * @param game game state to persist.
     * @param filePath destination file path.
     * @throws IOException if the file cannot be created or written.
     * @throws IllegalArgumentException if the game or file path is {@code null}.
     */
    public void saveGame(Game game, Path filePath) throws IOException {
        if (game == null) {
            throw new IllegalArgumentException("La partida no puede ser nula.");
        }

        if (filePath == null) {
            throw new IllegalArgumentException("La ruta del archivo no puede ser nula.");
        }

        createParentDirectory(filePath);

        try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            outputStream.writeObject(game);
        }
    }

    /**
     * Loads a serialized game state from disk.
     *
     * @param filePath source file path.
     * @return loaded game state.
     * @throws IOException if the file does not exist, cannot be read, or does not contain a valid game.
     * @throws ClassNotFoundException if the serialized class definition cannot be resolved.
     * @throws IllegalArgumentException if the file path is {@code null}.
     */
    public Game loadGame(Path filePath) throws IOException, ClassNotFoundException {
        if (filePath == null) {
            throw new IllegalArgumentException("La ruta del archivo no puede ser nula.");
        }

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("No existe una partida guardada en la ruta indicada.");
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(filePath))) {
            Object loadedObject = inputStream.readObject();

            if (loadedObject instanceof Game game) {
                return game;
            }

            throw new IOException("El archivo no contiene una partida válida.");
        }
    }

    /**
     * Creates the parent directory for a persistence file when it does not exist.
     *
     * @param filePath file path whose parent directory must be created.
     * @throws IOException if the directory cannot be created.
     */
    private void createParentDirectory(Path filePath) throws IOException {
        Path parentDirectory = filePath.getParent();

        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
    }
}
