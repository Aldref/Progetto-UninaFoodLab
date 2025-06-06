package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import com.progetto.controller.CardCorsoController;

public class CardCorsoBoundary {

    @FXML private Button buyButton;
    @FXML private HBox buttonsBox;
    @FXML private VBox priceSection;
    @FXML private Label acquistatoBadge;
    @FXML private Label priceLabel;
    @FXML private Button calendarButton;
    @FXML private ImageView courseImage;
    @FXML private Label courseTitle;
    @FXML private Label courseDescription;
    @FXML private Label startDate;
    @FXML private Label endDate;
    @FXML private Label frequency;
    @FXML private Label lessonType;

    private CardCorsoController controller;

    @FXML
    private void initialize() {
        controller = new CardCorsoController(buyButton, buttonsBox, priceSection, acquistatoBadge, priceLabel, calendarButton);
        controller.initialize();

        // Immagine di esempio, facilmente modificabile da Java
        setCourseImage("/immagini/corsi/esempio.png");
    }

    @FXML
    private void handlePurchase(ActionEvent event) {
        controller.handlePurchase();
    }

    @FXML
    private void handleShowCalendar(ActionEvent event) {
        controller.handleShowCalendar();
    }

    // Metodi di supporto per impostare stato e dati da altri controller
    public void setAcquistato(boolean acquistato) {
        controller.setAcquistato(acquistato);
    }

    public void setEnrolledPage(boolean enrolledPage) {
        controller.setEnrolledPage(enrolledPage);
    }

    public CardCorsoController getController() {
        return controller;
    }

    // Metodo per cambiare facilmente l'immagine
    public void setCourseImage(String resourcePath) {
        try {
            Image img = new Image(getClass().getResource(resourcePath).toExternalForm());
            courseImage.setImage(img);
            courseImage.setPreserveRatio(false); 
            courseImage.setSmooth(true); 
        } catch (Exception e) {
            System.err.println("Immagine non trovata: " + resourcePath);
            
            courseImage.setImage(null);
        }
    }

    // Metodi per cambiare facilmente i dati della card
    public void setCourseTitle(String title) { courseTitle.setText(title); }
    public void setCourseDescription(String desc) { courseDescription.setText(desc); }
    public void setStartDate(String date) { startDate.setText(date); }
    public void setEndDate(String date) { endDate.setText(date); }
    public void setFrequency(String freq) { frequency.setText(freq); }
    public void setLessonType(String type) { lessonType.setText(type); }
    public void setPrice(String price) { priceLabel.setText(price); }
}