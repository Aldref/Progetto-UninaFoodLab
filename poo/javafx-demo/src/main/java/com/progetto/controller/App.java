package com.progetto.controller;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.image.Image;

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
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/immagini/logo.png")));
        
        // Usa switchToLogin per avviare con la finestra del login
        SceneSwitcher.switchToLogin(stage, "/fxml/loginpage.fxml", "UninaFoodLab Login");
    }
}