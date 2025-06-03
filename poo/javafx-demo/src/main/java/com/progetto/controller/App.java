package com.progetto.controller;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    private static final double MIN_WINDOW_WIDTH = 600;
    private static final double MIN_WINDOW_HEIGHT = 400;
    private static final double MAX_WINDOW_WIDTH = 800;
    private static final double MAX_WINDOW_HEIGHT = 600;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Configura la scena di login con dimensioni minime e massime
        SceneSwitcher.switchScene(
            stage,
            "/fxml/loginpage.fxml",
            "UninaFoodLab Login",
            false, // Non resizable
            MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT, // Dimensioni minime
            MAX_WINDOW_WIDTH, MAX_WINDOW_HEIGHT  // Dimensioni massime
        );

        stage.centerOnScreen();
    }
}