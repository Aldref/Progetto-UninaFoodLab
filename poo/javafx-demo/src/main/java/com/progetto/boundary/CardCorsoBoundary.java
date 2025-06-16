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
    @FXML private Button editButton;
    @FXML private Button calendarButton;
    @FXML private HBox buttonsBox;
    @FXML private VBox priceSection;
    @FXML private Label acquistatoBadge;
    @FXML private Label priceLabel;
    @FXML private Label courseTitle;
    @FXML private Label courseDescription;
    @FXML private Label startDate;
    @FXML private Label endDate;
    @FXML private Label frequency;
    @FXML private ImageView courseImage;

    private CardCorsoController controller;

    @FXML
    private void initialize() {
        controller = new CardCorsoController(buyButton, editButton, buttonsBox, priceSection, 
                                           acquistatoBadge, priceLabel, calendarButton, courseImage,
                                           courseTitle, courseDescription, startDate, endDate, frequency);
        controller.initialize();
        setCourseImage("/immagini/corsi/esempio.png");
    }

    @FXML
    private void handlePurchase(ActionEvent event) {
        controller.handlePurchase();
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        controller.handleEdit();
    }

    @FXML
    private void handleShowCalendar(ActionEvent event) {
        controller.handleShowCalendar();
    }

    public void setCourseImage(String imagePath) {
        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            courseImage.setImage(image);
        } catch (Exception e) {
            System.out.println("Immagine non trovata: " + imagePath);
        }
    }

    public void setCourseData(String title, String description, String start, String end, String freq, String price) {
        controller.setCourseData(title, description, start, end, freq, price);
    }

    public void setChefMode(boolean isChef) {
        controller.setChefMode(isChef);
    }

    public void setEnrolledMode(boolean isEnrolled) {
        controller.setEnrolledMode(isEnrolled);
    }

    // Getter per i componenti
    public Button getBuyButton() { return buyButton; }
    public Button getEditButton() { return editButton; }
    public Button getCalendarButton() { return calendarButton; }
    public HBox getButtonsBox() { return buttonsBox; }
    public VBox getPriceSection() { return priceSection; }
    public Label getAcquistatoBadge() { return acquistatoBadge; }
    public Label getPriceLabel() { return priceLabel; }
    public Label getCourseTitle() { return courseTitle; }
    public Label getCourseDescription() { return courseDescription; }
    public Label getStartDate() { return startDate; }
    public Label getEndDate() { return endDate; }
    public Label getFrequency() { return frequency; }
    public ImageView getCourseImage() { return courseImage; }
    public CardCorsoController getController() {return controller;}

}