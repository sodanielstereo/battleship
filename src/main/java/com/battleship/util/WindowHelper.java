package com.battleship.util;

import java.io.InputStream;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Utility class for applying common window configuration to the application.
 */
public final class WindowHelper {

    private static final String ICON_PATH = "/com/battleship/icons/icon.png";

    private WindowHelper() {
    }

    /**
     * Applies the application icon and fixed maximized size to the stage.
     *
     * @param stage application stage to configure.
     */
    public static void configureStage(Stage stage) {
        applyAppIcon(stage);
        applyFixedMaximizedSize(stage);
    }

    /**
     * Sets the application icon on the given stage.
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
     * <p>
     * This uses the visual bounds of the screen, so the window respects the
     * operating system taskbar instead of using real fullscreen mode.
     * </p>
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