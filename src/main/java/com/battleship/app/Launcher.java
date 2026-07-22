package com.battleship.app;

/**
 * Launches the JavaFX Battleship application.
 *
 * This class delegates execution to {@link MainApplication}. It is useful when the
 * application is started from IDEs or packaging mechanisms that expect a regular
 * Java main class.
 */
public class Launcher {

    /**
     * Application bootstrap method.
     *
     * @param args command-line arguments passed by the runtime.
     */
    public static void main(String[] args) {
        MainApplication.main(args);
    }
}
