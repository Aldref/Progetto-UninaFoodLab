package com.progetto.utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

import com.progetto.boundary.LogoutDialogBoundary;

public class SceneSwitcher {

    /**
     * Metodo principale per cambiare scena - MANTIENE LO STATO ATTUALE DELLA FINESTRA
     */
    public static <T> T switchScene(Stage stage, String fxmlPath, String title) throws IOException {
        boolean wasMaximized = stage.isMaximized();
    
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
        Parent root = loader.load();
        T controller = loader.getController();
    
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
    
        stage.show();
    
        // Solo se era già massimizzato, forza di nuovo maximized dopo il cambio scena
        if (wasMaximized) {
            Platform.runLater(() -> {
                stage.setMaximized(true);
                // Forza il ridisegno della scena
                stage.setWidth(stage.getWidth() + 1);
                stage.setWidth(stage.getWidth() - 1);
                stage.setHeight(stage.getHeight() + 1);
                stage.setHeight(stage.getHeight() - 1);
            });
        }
    
        return controller;
    }
    /**
     * Metodo per passare da login a homepage - VA A SCHERMO INTERO
     */
    public static <T> T switchToMainApp(Stage stage, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
        Parent root = loader.load();
        T controller = loader.getController();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setResizable(true);

        stage.show();
        Platform.runLater(() -> stage.setMaximized(true));
        return controller;
    }

    /**
     * Metodo per logout - VA IN FINESTRA PICCOLA (per login)
     */
    public static <T> T switchToLogin(Stage stage, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
        Parent root = loader.load();
        T controller = loader.getController();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        
        stage.show();

        // Forza finestra piccola
        Platform.runLater(() -> {
            stage.setMaximized(false);
            stage.setResizable(false);
            stage.setWidth(800);
            stage.setHeight(600);
            stage.centerOnScreen();
        });

        

        return controller;
    }

    /**
     * Metodo per registrazione - FINESTRA MEDIA
     */
    public static <T> T switchToRegister(Stage stage, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
        Parent root = loader.load();
        T controller = loader.getController();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        
        // Finestra più grande per la registrazione
        stage.setMaximized(false);
        stage.setResizable(true);
        stage.setWidth(1000);  // Più largo
        stage.setHeight(700);  // Più alto
        stage.centerOnScreen();

        stage.show();

        return controller;
    }
    public static void showDialogCentered(Stage owner, Stage dialogStage) {
        dialogStage.show();
        if (owner != null) {
            double centerX = owner.getX() + (owner.getWidth() - dialogStage.getWidth()) / 2;
            double centerY = owner.getY() + (owner.getHeight() - dialogStage.getHeight()) / 2;
            dialogStage.setX(centerX);
            dialogStage.setY(centerY);
        } else {
            dialogStage.centerOnScreen();
        }
    }

    public static LogoutDialogBoundary showLogoutDialog(Stage owner) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource("/fxml/logoutdialog.fxml"));
        VBox dialogContent = loader.load();
        LogoutDialogBoundary dialogBoundary = loader.getController();

        Stage dialogStage = new Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
        dialogStage.setTitle("Conferma Logout");

        javafx.scene.Scene dialogScene = new javafx.scene.Scene(dialogContent);
        dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        dialogStage.setScene(dialogScene);

        // CENTRA PRIMA DI MOSTRARE LA FINESTRA
        dialogStage.setOnShown(e -> {
            if (owner != null) {
                double centerX = owner.getX() + (owner.getWidth() - dialogStage.getWidth()) / 2;
                double centerY = owner.getY() + (owner.getHeight() - dialogStage.getHeight()) / 2;
                dialogStage.setX(centerX);
                dialogStage.setY(centerY);
            } else {
                dialogStage.centerOnScreen();
            }
        });

        dialogStage.showAndWait();

        return dialogBoundary;
    }
}