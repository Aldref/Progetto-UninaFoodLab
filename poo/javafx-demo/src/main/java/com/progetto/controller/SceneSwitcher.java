package com.progetto.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {

    public static <T> T switchScene(Stage stage, String fxmlPath, String title, boolean resizable, 
                                    double minWidth, double minHeight, double maxWidth, double maxHeight) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
            Parent root = loader.load();
            T controller = loader.getController();

            // Configura la scena
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(resizable);

            // Imposta dimensioni minime e massime per la finestra
            stage.setMinWidth(minWidth);
            stage.setMinHeight(minHeight);
            if (maxWidth > 0) stage.setMaxWidth(maxWidth); // Imposta solo se specificato
            if (maxHeight > 0) stage.setMaxHeight(maxHeight); // Imposta solo se specificato

            stage.centerOnScreen();
            stage.show();

            return controller;
        } catch (IOException e) {
            // Mostra un messaggio di errore all'utente
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Errore durante il caricamento della scena");
            alert.setContentText("Impossibile caricare la scena: " + fxmlPath);
            alert.showAndWait();

            throw e;
        }
    }
}