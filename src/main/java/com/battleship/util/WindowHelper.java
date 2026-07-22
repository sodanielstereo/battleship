package com.battleship.util;

import java.io.InputStream;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Utility class for common JavaFX window configuration.
 *
 * It centralizes application icon loading and fixed maximized sizing so controllers
 * and the main application do not duplicate stage configuration logic.
 */
public final class WindowHelper {

    private static final String ICON_PATH = "/com/battleship/icons/icon.png";

    /**
     * Prevents instantiation of this utility class.
     */
    private WindowHelper() {
    }

    /**
     * Applies all shared window settings to a stage.
     *
     * @param stage application stage to configure.
     */
    public static void configureStage(Stage stage) {
        applyAppIcon(stage);
        applyFixedMaximizedSize(stage);
    }

    /**
     * Loads the application icon from resources and assigns it to the stage.
     *
     * @param stage application stage to configure.
     */
    public static void applyAppIcon(Stage stage) {
        InputStream iconStream = WindowHelper.class.getResourceAsStream(ICON_PATH);

        if (iconStream != null) {
            stage.getIcons().add(new Image(iconStream));
        }
    }

    /**
     * Sets the stage to the maximum visible screen size and disables resizing.
     *
     * The visual bounds are used so the window respects the operating system taskbar
     * instead of entering exclusive fullscreen mode.
     *
     * @param stage application stage to configure.
     */
    public static void applyFixedMaximizedSize(Stage stage) {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();

        stage.setX(visualBounds.getMinX());
        stage.setY(visualBounds.getMinY());
        stage.setWidth(visualBounds.getWidth());
        stage.setHeight(visualBounds.getHeight());

        stage.setMinWidth(visualBounds.getWidth());
        stage.setMinHeight(visualBounds.getHeight());
        stage.setMaxWidth(visualBounds.getWidth());
        stage.setMaxHeight(visualBounds.getHeight());

        stage.setResizable(false);
    }
}
