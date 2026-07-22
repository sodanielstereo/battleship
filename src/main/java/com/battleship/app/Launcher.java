package com.battleship.app;

/**
 * Launcher class to start the JavaFX application.
 * This class is necessary to launch the application from a JAR file or from
 * certain IDEs that do not support JavaFX directly.
 * It delegates the launch to the MainApplication class.
 */
public class Launcher {

    public static void main(String[] args) {
        MainApplication.main(args);
    }
}