package com.progetto.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/loginpage.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("UninaFoodLab Login");
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }
}
