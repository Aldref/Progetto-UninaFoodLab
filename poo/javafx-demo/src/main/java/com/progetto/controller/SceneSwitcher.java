package com.progetto.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {

    public static void switchScene(Stage stage, String fxmlPath, String title, boolean resizable) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setTitle(title);
        stage.setScene(scene);
        stage.setResizable(resizable);

        if (!resizable) {
            stage.setMinWidth(scene.getWidth());
            stage.setMaxWidth(scene.getWidth());
            stage.setMinHeight(scene.getHeight());
            stage.setMaxHeight(scene.getHeight());
        }

        stage.centerOnScreen();
        stage.show();
    }
}
