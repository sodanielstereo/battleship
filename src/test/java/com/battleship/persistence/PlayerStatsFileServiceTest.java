package com.battleship.persistence;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.battleship.model.Game;

class PlayerStatsFileServiceTest {

    @TempDir
    private Path tempDirectory;

    @Test
    void playerStatsShouldBeSavedInFlatFile() throws IOException {
        Game game = new Game("Daniel");
        PlayerStatsFileService service = new PlayerStatsFileService();
        Path filePath = tempDirectory.resolve("player-stats.txt");

        service.savePlayerStats(game, filePath);
        List<String> lines = service.readStats(filePath);

        assertEquals(2, lines.size());
        assertTrue(lines.get(0).contains("nickname"));
        assertTrue(lines.get(0).contains("enemy_sunk_ships"));
        assertTrue(lines.get(1).contains("Daniel"));
        assertTrue(lines.get(1).contains("PLACEMENT"));
    }

    @Test
    void readStatsShouldReturnEmptyListWhenFileDoesNotExist() throws IOException {
        PlayerStatsFileService service = new PlayerStatsFileService();
        Path filePath = tempDirectory.resolve("missing-stats.txt");

        List<String> lines = service.readStats(filePath);

        assertTrue(lines.isEmpty());
    }
}