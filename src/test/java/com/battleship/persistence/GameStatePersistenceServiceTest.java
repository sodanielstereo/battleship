package com.battleship.persistence;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.battleship.model.Game;
import com.battleship.model.enums.GamePhase;

class GameStatePersistenceServiceTest {

    @TempDir
    private Path tempDirectory;

    @Test
    void gameShouldBeSavedAndLoadedUsingSerialization() throws Exception {
        Game game = new Game("Daniel");
        GameStatePersistenceService service = new GameStatePersistenceService();
        Path filePath = tempDirectory.resolve("game-state.ser");

        service.saveGame(game, filePath);
        Game loadedGame = service.loadGame(filePath);

        assertTrue(Files.exists(filePath));
        assertEquals("Daniel", loadedGame.getHumanPlayer().getNickname());
        assertEquals("Máquina", loadedGame.getMachinePlayer().getNickname());
        assertEquals(GamePhase.PLACEMENT, loadedGame.getPhase());
        assertEquals(100, loadedGame.getHumanPlayer().getBoard().getCells().size());
        assertEquals(100, loadedGame.getMachinePlayer().getBoard().getCells().size());
    }

    @Test
    void loadGameShouldFailWhenFileDoesNotExist() {
        GameStatePersistenceService service = new GameStatePersistenceService();
        Path filePath = tempDirectory.resolve("missing-game.ser");

        assertThrows(FileNotFoundException.class, () -> service.loadGame(filePath));
    }
}