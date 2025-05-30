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
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/loginpage.fxml"));
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        stage.setScene(scene);
        stage.setTitle("UninaFoodLab Login");

        // Imposta dimensioni fisse
        stage.setResizable(false);
        stage.setMinWidth(WINDOW_WIDTH);
        stage.setMaxWidth(WINDOW_WIDTH);
        stage.setMinHeight(WINDOW_HEIGHT);
        stage.setMaxHeight(WINDOW_HEIGHT);

        stage.show();              
        stage.centerOnScreen();   
    }
}
