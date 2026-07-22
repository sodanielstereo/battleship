package com.battleship.model.player;

/**
 * Represents the human player controlled through the JavaFX interface.
 */
public class RealPlayer extends Player {
    // Additional properties and methods specific to the real player can be added here

    private static final long serialVersionUID = 1L;

    /**
     * Creates the human player.
     *
     * @param nickname nickname entered by the user.
     */
    public RealPlayer(String nickname) {
        super(nickname);
    }
    
}
