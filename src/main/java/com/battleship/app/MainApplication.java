package com.battleship.app;

import java.io.IOException;

import com.battleship.util.WindowHelper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Main JavaFX entry point for the Battleship application.
 *
 * This class loads the main FXML view, registers the custom fonts used by the CSS
 * theme, configures the primary stage, and starts the graphical application.
 */
public class MainApplication extends Application {

    private static final String MAIN_VIEW_PATH = "/com/battleship/view/main-view.fxml";

    /**
     * Starts the JavaFX application.
     *
     * The method loads custom fonts, creates the main scene from the FXML file, applies
     * the shared window configuration, and shows the primary stage.
     *
     * @param stage primary JavaFX stage provided by the runtime.
     * @throws IOException if the main FXML file cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        loadApplicationFonts();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(MAIN_VIEW_PATH));

        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Batalla Naval");
        stage.setScene(scene);
        WindowHelper.configureStage(stage);
        stage.show();
    }

    /**
     * Launches the JavaFX runtime.
     *
     * @param args command-line arguments passed by the runtime.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Loads all custom font resources used by the CSS theme.
     */
    private void loadApplicationFonts() {
        loadFont("/com/battleship/fonts/Orbitron-Bold.ttf");
        loadFont("/com/battleship/fonts/Orbitron-SemiBold.ttf");

        loadFont("/com/battleship/fonts/TitilliumWeb-Regular.ttf");
        loadFont("/com/battleship/fonts/TitilliumWeb-SemiBold.ttf");
        loadFont("/com/battleship/fonts/TitilliumWeb-Bold.ttf");

        loadFont("/com/battleship/fonts/ChakraPetch-Regular.ttf");
        loadFont("/com/battleship/fonts/ChakraPetch-SemiBold.ttf");
        loadFont("/com/battleship/fonts/ChakraPetch-Bold.ttf");
    }

    /**
     * Loads a single font resource from the application classpath.
     *
     * @param fontPath absolute resource path of the font file inside {@code src/main/resources}.
     */
    private void loadFont(String fontPath) {
        Font.loadFont(MainApplication.class.getResourceAsStream(fontPath), 12);
    }
}
