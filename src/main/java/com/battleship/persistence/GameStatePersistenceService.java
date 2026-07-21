package com.battleship.persistence;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.battleship.model.Game;

/**
 * Service in charge of persisting the game state to a file and loading it back
 * using serialization.
 * 
 * GameStatePersistenceService
 */

public class GameStatePersistenceService {

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

    private void createParentDirectory(Path filePath) throws IOException {
        Path parentDirectory = filePath.getParent();

        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
    }
}