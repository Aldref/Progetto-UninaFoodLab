package com.progetto.utils;

import java.io.IOException;

import com.progetto.boundary.SuccessDialogBoundary;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SuccessDialogUtils {
    
    public static void showPaymentSuccessDialog(Stage parentStage, String courseName) {
        showSuccessDialog(parentStage, 
            "Pagamento Completato!", 
            "Il pagamento è stato elaborato con successo.",
            "Il corso è stato aggiunto ai tuoi corsi iscritti.",
            courseName);
    }
    
    public static void showSaveSuccessDialog(Stage parentStage) {
        showSuccessDialog(parentStage,
            "Dati Salvati!",
            "Le modifiche sono state salvate con successo.",
            "I tuoi dati sono stati aggiornati.",
            null);
    }

    public static void showCancelSuccessDialog(Stage parentStage) {
        showSuccessDialog(parentStage,
            "Modifiche Annullate!",
            "Le modifiche sono state annullate con successo.",
            "I dati sono stati ripristinati ai valori originali.",
            null);
    }
    
    public static void showGenericSuccessDialog(Stage parentStage, String title, String message) {
        showSuccessDialog(parentStage, title, message, null, null);
    }
    
    private static void showSuccessDialog(Stage parentStage, String title, String mainMessage, 
                                        String subMessage, String courseName) {
        try {
            FXMLLoader loader = new FXMLLoader(SuccessDialogUtils.class.getResource("/fxml/successdialog.fxml"));
            Parent root = loader.load();
            
            SuccessDialogBoundary dialogBoundary = loader.getController();
            
            dialogBoundary.setTitle(title);
            dialogBoundary.setMainMessage(mainMessage);
            
            if (subMessage != null && !subMessage.isEmpty()) {
                dialogBoundary.setSubMessage(subMessage);
            }
            
            if (courseName != null && !courseName.isEmpty()) {
                dialogBoundary.setCourseName(courseName);
            } else {
                dialogBoundary.hideCourseInfo();
            }
            
            Stage dialogStage = new Stage();
            dialogBoundary.setDialogStage(dialogStage);
            
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.UNDECORATED);
            dialogStage.setTitle(title);
            dialogStage.setResizable(false);
            
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            dialogStage.setScene(scene);
            
            if (parentStage != null) {
                dialogStage.initOwner(parentStage);
                dialogStage.setOnShown(e -> {
                    double centerX = parentStage.getX() + (parentStage.getWidth() - dialogStage.getWidth()) / 2;
                    double centerY = parentStage.getY() + (parentStage.getHeight() - dialogStage.getHeight()) / 2;
                    dialogStage.setX(centerX);
                    dialogStage.setY(centerY);
                });
            } else {
                dialogStage.centerOnScreen();
            }
            
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}