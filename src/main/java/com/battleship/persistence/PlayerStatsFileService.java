package com.battleship.persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.battleship.model.Game;

/**
 * Service in charge of persisting the player statistics (using serialization)
 * through a CSV file.
 * 
 */

public class PlayerStatsFileService {

    private static final String SEPARATOR = ";";
    private static final String HEADER = "date;nickname;enemy_sunk_ships;own_sunk_ships;phase";

    public void savePlayerStats(Game game, Path filePath) throws IOException {
        if (game == null) {
            throw new IllegalArgumentException("La partida no puede ser nula.");
        }

        if (filePath == null) {
            throw new IllegalArgumentException("La ruta del archivo no puede ser nula.");
        }

        createParentDirectory(filePath);

        boolean fileAlreadyExists = Files.exists(filePath);

        try (BufferedWriter writer = Files.newBufferedWriter(
                filePath,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            if (!fileAlreadyExists) {
                writer.write(HEADER);
                writer.newLine();
            }

            writer.write(buildStatsLine(game));
            writer.newLine();
        }
    }

    public List<String> readStats(Path filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("La ruta del archivo no puede ser nula.");
        }

        if (!Files.exists(filePath)) {
            return List.of();
        }

        return Files.readAllLines(filePath);
    }

    private String buildStatsLine(Game game) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String nickname = sanitize(game.getHumanPlayer().getNickname());
        String enemySunkShips = String.valueOf(game.getMachinePlayer().getSunkShipsCount());
        String ownSunkShips = String.valueOf(game.getHumanPlayer().getSunkShipsCount());
        String phase = game.getPhase().name();

        return String.join(
                SEPARATOR,
                date,
                nickname,
                enemySunkShips,
                ownSunkShips,
                phase);
    }

    private String sanitize(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace(SEPARATOR, ",")
                .replace("\n", " ")
                .replace("\r", " ")
                .trim();
    }

    private void createParentDirectory(Path filePath) throws IOException {
        Path parentDirectory = filePath.getParent();

        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
    }
}