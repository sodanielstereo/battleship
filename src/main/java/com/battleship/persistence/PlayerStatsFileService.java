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
 * Service responsible for writing and reading player statistics in a flat file.
 *
 * Unlike the game state persistence service, this class does not use
 * serialization. It stores human-readable CSV-like rows separated by semicolons.
 */
public class PlayerStatsFileService {

    private static final String SEPARATOR = ";";
    private static final String HEADER = "date;nickname;enemy_sunk_ships;own_sunk_ships;phase";

    /**
     * Appends one player-statistics row to the flat file.
     *
     * If the file does not exist, the method writes the header before appending the
     * new game statistics.
     *
     * @param game finished or current game used to extract statistics.
     * @param filePath destination flat file path.
     * @throws IOException if the file cannot be created or written.
     * @throws IllegalArgumentException if the game or file path is {@code null}.
     */
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

    /**
     * Reads all statistics rows from the flat file.
     *
     * @param filePath source flat file path.
     * @return file lines, or an empty list when the file does not exist.
     * @throws IOException if the file exists but cannot be read.
     * @throws IllegalArgumentException if the file path is {@code null}.
     */
    public List<String> readStats(Path filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("La ruta del archivo no puede ser nula.");
        }

        if (!Files.exists(filePath)) {
            return List.of();
        }

        return Files.readAllLines(filePath);
    }

    /**
     * Builds a semicolon-separated statistics row for the provided game.
     *
     * @param game game used to extract the row values.
     * @return formatted statistics row.
     */
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

    /**
     * Sanitizes a text value before writing it to the flat file.
     *
     * @param value value to sanitize.
     * @return sanitized value without separators or line breaks.
     */
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
