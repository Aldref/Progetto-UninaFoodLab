package com.progetto.utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import com.progetto.boundary.LogoutDialogBoundary;

public class SceneSwitcher {

    private static final double GLOBAL_MAX_WIDTH = 2560;  
    private static final double GLOBAL_MAX_HEIGHT = 1440;

    private static final double AUTH_MIN_WIDTH = 600;
    private static final double AUTH_MIN_HEIGHT = 500;
    private static final double AUTH_MAX_WIDTH = 900;
    private static final double AUTH_MAX_HEIGHT = 700;
    
    private static final double LOGIN_DEFAULT_WIDTH = 800;
    private static final double LOGIN_DEFAULT_HEIGHT = 600;
    
    private static final double REGISTER_MIN_WIDTH = 900;
    private static final double REGISTER_MIN_HEIGHT = 750;  
    private static final double REGISTER_DEFAULT_WIDTH = 1000;
    private static final double REGISTER_DEFAULT_HEIGHT = 800; 
    private static final double REGISTER_MAX_WIDTH = 1200;
    private static final double REGISTER_MAX_HEIGHT = 900;

    private static final double MAIN_APP_MIN_WIDTH = 1024;
    private static final double MAIN_APP_MIN_HEIGHT = 768;
    
    private static final double TRANSACTION_MIN_WIDTH = 1100;
    private static final double TRANSACTION_MIN_HEIGHT = 800;
    private static final double TRANSACTION_DEFAULT_WIDTH = 1200;
    private static final double TRANSACTION_DEFAULT_HEIGHT = 800;

    private static final double DIALOG_CALENDAR_WIDTH = 1200;
    private static final double DIALOG_CALENDAR_HEIGHT = 800;
    private static final double DIALOG_LOGOUT_WIDTH = 400;
    private static final double DIALOG_LOGOUT_HEIGHT = 250;

    private static void applyAuthLimits(Stage stage) {
        stage.setMinWidth(AUTH_MIN_WIDTH);
        stage.setMinHeight(AUTH_MIN_HEIGHT);
        stage.setMaxWidth(AUTH_MAX_WIDTH);
        stage.setMaxHeight(AUTH_MAX_HEIGHT);
    }
    
    private static void applyMainAppLimits(Stage stage) {
        stage.setMinWidth(MAIN_APP_MIN_WIDTH);
        stage.setMinHeight(MAIN_APP_MIN_HEIGHT);
        stage.setMaxWidth(GLOBAL_MAX_WIDTH);
        stage.setMaxHeight(GLOBAL_MAX_HEIGHT);
    }
    
    private static void applyTransactionLimits(Stage stage) {
        stage.setMinWidth(TRANSACTION_MIN_WIDTH);
        stage.setMinHeight(TRANSACTION_MIN_HEIGHT);
        stage.setMaxWidth(GLOBAL_MAX_WIDTH);
        stage.setMaxHeight(GLOBAL_MAX_HEIGHT);
    }
    
    private static void applyDialogLimits(Stage stage, double width, double height) {
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setResizable(false); 
        stage.setMinWidth(width);
        stage.setMinHeight(height);
        stage.setMaxWidth(width);
        stage.setMaxHeight(height);
    }

    private static void applyRegisterLimits(Stage stage) {
        stage.setMinWidth(REGISTER_MIN_WIDTH);
        stage.setMinHeight(REGISTER_MIN_HEIGHT);
        stage.setMaxWidth(REGISTER_MAX_WIDTH);
        stage.setMaxHeight(REGISTER_MAX_HEIGHT);
    }

    private static void validateAndCorrectSize(Stage stage, double minWidth, double minHeight, 
                                              double maxWidth, double maxHeight) {
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();
        
        if (currentWidth < minWidth) stage.setWidth(minWidth);
        if (currentWidth > maxWidth) stage.setWidth(maxWidth);
        if (currentHeight < minHeight) stage.setHeight(minHeight);
        if (currentHeight > maxHeight) stage.setHeight(maxHeight);
    }

    private static void applyPlatformWindowFix(Stage stage, boolean forceMaximized) {
        Platform.runLater(() -> {
            String os = System.getProperty("os.name").toLowerCase();

            if (forceMaximized) {
                if (os.contains("linux")) {
                    stage.setMaximized(true);
                    stage.setWidth(stage.getWidth() + 1);
                    stage.setWidth(stage.getWidth() - 1);
                    stage.setHeight(stage.getHeight() + 1);
                    stage.setHeight(stage.getHeight() - 1);
                }
                else if (os.contains("mac")) {
                    javafx.geometry.Rectangle2D bounds = javafx.stage.Screen.getPrimary().getVisualBounds();
                    stage.setX(bounds.getMinX());
                    stage.setY(bounds.getMinY());
                    stage.setWidth(bounds.getWidth());
                    stage.setHeight(bounds.getHeight());
                }
                else if (os.contains("win")) {
                    stage.setMaximized(true);
                }
            }
        });
    }

    public static <T> T switchToLogin(Stage stage, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
        Parent root = loader.load();
        T controller = loader.getController();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.setWidth(LOGIN_DEFAULT_WIDTH);
        stage.setHeight(LOGIN_DEFAULT_HEIGHT);
        applyAuthLimits(stage);
        stage.show();
        Platform.runLater(() -> stage.centerOnScreen());

        return controller;
    }

    public static <T> T switchToRegister(Stage stage, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
        Parent root = loader.load();
        T controller = loader.getController();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.setWidth(REGISTER_DEFAULT_WIDTH);
        stage.setHeight(REGISTER_DEFAULT_HEIGHT);
        applyRegisterLimits(stage);
        stage.show();
        Platform.runLater(() -> stage.centerOnScreen());

        return controller;
    }

    public static <T> T switchToMainApp(Stage stage, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
        Parent root = loader.load();
        T controller = loader.getController();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setResizable(true);
        applyMainAppLimits(stage);
        stage.show();
        applyPlatformWindowFix(stage, true);

        return controller;
    }

    public static <T> T switchMainAppScene(Stage stage, String fxmlPath, String title) throws IOException {
        boolean wasMaximized = stage.isMaximized();
        double savedWidth = stage.getWidth();
        double savedHeight = stage.getHeight();
        double savedX = stage.getX();
        double savedY = stage.getY();
        boolean wasResizable = stage.isResizable();
    
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
        Parent root = loader.load();
        T controller = loader.getController();
    
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setResizable(wasResizable);
        applyMainAppLimits(stage);
        
        if (!wasMaximized) {
            validateAndCorrectSize(stage, MAIN_APP_MIN_WIDTH, MAIN_APP_MIN_HEIGHT, 
                                 GLOBAL_MAX_WIDTH, GLOBAL_MAX_HEIGHT);
            stage.setWidth(Math.max(MAIN_APP_MIN_WIDTH, savedWidth));
            stage.setHeight(Math.max(MAIN_APP_MIN_HEIGHT, savedHeight));
            stage.setX(savedX);
            stage.setY(savedY);
        }
        
        stage.show();
    
        if (wasMaximized) {
            Platform.runLater(() -> {
                stage.setMaximized(true);
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("linux")) {
                    stage.setWidth(stage.getWidth() + 1);
                    stage.setWidth(stage.getWidth() - 1);
                    stage.setHeight(stage.getHeight() + 1);
                    stage.setHeight(stage.getHeight() - 1);
                }
            });
        }
    
        return controller;
    }

    public static <T> T switchToTransaction(Stage stage, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
        Parent root = loader.load();
        T controller = loader.getController();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setResizable(true);

        if (!stage.isMaximized()) {
            stage.setWidth(TRANSACTION_DEFAULT_WIDTH);
            stage.setHeight(TRANSACTION_DEFAULT_HEIGHT);
            Platform.runLater(() -> stage.centerOnScreen());
        }

        applyTransactionLimits(stage);

        stage.show();

        return controller;
    }

    public static <T> T switchScene(Stage stage, String fxmlPath, String title) throws IOException {
        if (fxmlPath.contains("login")) {
            return switchToLogin(stage, fxmlPath, title);
        } else if (fxmlPath.contains("register")) {
            return switchToRegister(stage, fxmlPath, title);
        } else if (fxmlPath.contains("payment") || fxmlPath.contains("card")) {
            return switchToTransaction(stage, fxmlPath, title);
        } else {
            return switchMainAppScene(stage, fxmlPath, title);
        }
    }

    public static void switchToScene(Stage stage, String fxmlPath) {
        try {
            String title = "UninaFoodLab"; 
            switchScene(stage, fxmlPath, title);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore nel caricamento della scena: " + fxmlPath);
        }
    }

    public static void showDialogCentered(Stage owner, Stage dialogStage) {
        dialogStage.setResizable(false); 
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

    public static void showCalendarDialog(Stage owner, boolean isChef) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource("/fxml/calendardialog.fxml"));
        Parent dialogContent = loader.load();

        com.progetto.boundary.CalendarDialogBoundary dialogBoundary = loader.getController();
        dialogBoundary.setChefMode(isChef);
        dialogBoundary.initialize();

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(owner);

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            dialogStage.initStyle(javafx.stage.StageStyle.UTILITY);
        } else {
            dialogStage.initStyle(javafx.stage.StageStyle.DECORATED);
        }

        dialogStage.setTitle("Calendario lezioni - UninaFoodLab");

        applyDialogLimits(dialogStage, DIALOG_CALENDAR_WIDTH, DIALOG_CALENDAR_HEIGHT);

        Scene dialogScene = new Scene(dialogContent);
        dialogStage.setScene(dialogScene);

        showDialogCentered(owner, dialogStage);
    }

    public static LogoutDialogBoundary showLogoutDialog(Stage owner) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource("/fxml/logoutdialog.fxml"));
        VBox dialogContent = loader.load();
        LogoutDialogBoundary dialogBoundary = loader.getController();

        Stage dialogStage = new Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initOwner(owner);

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            dialogStage.initStyle(javafx.stage.StageStyle.UTILITY);
        } else {
            dialogStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
        }

        dialogStage.setTitle("Conferma Logout");
        dialogStage.setResizable(false);

        javafx.scene.Scene dialogScene = new javafx.scene.Scene(dialogContent);
        dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialogStage.setScene(dialogScene);

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
