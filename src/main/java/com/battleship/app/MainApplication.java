package com.battleship.app;

import java.io.IOException;

import com.battleship.util.WindowHelper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Main application class for the Battleship game.
 * This class is responsible for initializing and starting the JavaFX
 * application.
 */
public class MainApplication extends Application {

    private static final String MAIN_VIEW_PATH = "/com/battleship/view/main-view.fxml";

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

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Loads custom fonts used by the JavaFX stylesheets.
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
     * Loads one font resource if it exists.
     *
     * @param fontPath path of the font inside resources.
     */
    private void loadFont(String fontPath) {
        Font.loadFont(MainApplication.class.getResourceAsStream(fontPath), 12);
    }
}