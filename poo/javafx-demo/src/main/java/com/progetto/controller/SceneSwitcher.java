package com.progetto.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {
    
    private static boolean isFirstTransition = true; 

    public static <T> T switchScene(Stage stage, String fxmlPath, String title, boolean resizable, 
                                    double minWidth, double minHeight, double maxWidth, double maxHeight) throws IOException {
        return switchScene(stage, fxmlPath, title, resizable, minWidth, minHeight, maxWidth, maxHeight, true);
    }

    public static <T> T switchScene(Stage stage, String fxmlPath, String title, boolean resizable, 
                                    double minWidth, double minHeight, double maxWidth, double maxHeight, 
                                    boolean shouldMaximize) throws IOException {
        try {
            // Salva lo stato corrente
            boolean wasMaximized = stage.isMaximized();
            
            // Pre-configura le dimensioni corrette PRIMA di cambiare scena
            if (wasMaximized) {
                javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
                javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();
                
                // Imposta le dimensioni corrette PRIMA del cambio scena
                stage.setX(bounds.getMinX());
                stage.setY(bounds.getMinY());
                stage.setWidth(bounds.getWidth());
                stage.setHeight(bounds.getHeight());
            }

            FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
            Parent root = loader.load();
            T controller = loader.getController();

            // Configura la scena SENZA toccare lo stato maximized
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(resizable);

            // Imposta dimensioni minime e massime per la finestra
            stage.setMinWidth(minWidth);
            stage.setMinHeight(minHeight);
            
            if (maxWidth > 0) {
                stage.setMaxWidth(maxWidth);
            } else {
                stage.setMaxWidth(Double.MAX_VALUE);
            }
            
            if (maxHeight > 0) {
                stage.setMaxHeight(maxHeight);
            } else {
                stage.setMaxHeight(Double.MAX_VALUE);
            }

            // Gestione dello stato della finestra
            if (!shouldMaximize) {
                stage.setMaximized(false);
                // NON forzare width/height qui!
                if (fxmlPath.contains("loginpage")) {
                    stage.centerOnScreen();
                }
            } else if (!wasMaximized && isLoginToMainTransition(fxmlPath)) {
                            // Solo per login → main: vai a schermo intero (prima volta)
                            Platform.runLater(() -> stage.setMaximized(true));
                        }

            stage.show();
            return controller;
        } catch (IOException e) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Errore durante il caricamento della scena");
            alert.setContentText("Impossibile caricare la scena: " + fxmlPath);
            alert.showAndWait();
            throw e;
        }
    }

    private static boolean isLoginToMainTransition(String fxmlPath) {
        return fxmlPath.contains("homepageutente") || fxmlPath.contains("enrolledcourses");
    }

    // Metodo specifico per transizioni che devono mantenere lo stato corrente
    public static <T> T switchSceneKeepCurrentState(Stage stage, String fxmlPath, String title) throws IOException {
        // Mantieni sempre lo stato corrente, NON forzare maximized per login transitions
        boolean shouldMaintainCurrentState = stage.isMaximized();
        return switchScene(stage, fxmlPath, title, true, 800, 600, 2560, 1440, shouldMaintainCurrentState);
    }

    // Metodo specifico per transizioni che devono rimanere a schermo intero (deprecated)
    @Deprecated
    public static <T> T switchSceneKeepMaximized(Stage stage, String fxmlPath, String title) throws IOException {
        return switchSceneKeepCurrentState(stage, fxmlPath, title);
    }

    // Metodo specifico per transizioni che devono andare in finestra
    public static <T> T switchSceneToWindow(Stage stage, String fxmlPath, String title,
                                        double minWidth, double minHeight, double maxWidth, double maxHeight) throws IOException {
        T controller = switchScene(stage, fxmlPath, title, false, minWidth, minHeight, maxWidth, maxHeight, false);

        Platform.runLater(() -> {
            stage.setResizable(false);
            stage.setMinWidth(minWidth);
            stage.setMinHeight(minHeight);
            stage.setMaxWidth(maxWidth);
            stage.setMaxHeight(maxHeight);
            stage.centerOnScreen();
        });

        return controller;
    }
}