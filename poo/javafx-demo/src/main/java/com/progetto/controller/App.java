package com.progetto.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static final double WINDOW_WIDTH = 600;
    private static final double WINDOW_HEIGHT = 400;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Usa SceneSwitcher per caricare la scena iniziale
        SceneSwitcher.switchScene(stage, "/fxml/loginpage.fxml", "UninaFoodLab Login", false);

        

        stage.centerOnScreen();
    }

}
