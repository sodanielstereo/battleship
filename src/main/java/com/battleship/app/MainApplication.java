package com.battleship.app;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicación JavaFX.
 *
 * Se encarga de cargar la primera vista del proyecto.
 */
public class MainApplication extends Application {

    private static final String MAIN_VIEW_PATH = "/com/battleship/view/main-view.fxml";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(MAIN_VIEW_PATH));

        Scene scene = new Scene(fxmlLoader.load(), 900, 600);

        stage.setTitle("Batalla Naval");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}