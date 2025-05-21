package com.progetto;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        Label label = new Label("Ciao da JavaFX!");
        Scene scene = new Scene(label, 400, 200);
        stage.setScene(scene);
        stage.setTitle("JavaFX con Maven");
        stage.show();
    }
}
