package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.progetto.controller.CardCorsoController;
import com.progetto.Entity.EntityDto.Corso;

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
    @FXML private Label chefName;
    @FXML private Label chefExperience;
    @FXML private ImageView courseImage;
    @FXML private Label cuisineTypeLabel1;
    @FXML private Label cuisineTypeLabel2;
    @FXML private Label maxPeople;

    private CardCorsoController controller;
    private Corso corso; // Corso associato a questa card

    @FXML
    private void initialize() {
        controller = new CardCorsoController(buyButton, editButton, buttonsBox, priceSection, 
                                           acquistatoBadge, priceLabel, calendarButton, courseImage,
                                           courseTitle, courseDescription, startDate, endDate, frequency,
                                           chefName, chefExperience,
                                           cuisineTypeLabel1, cuisineTypeLabel2, maxPeople);
        controller.initialize();
        setCourseImage("/immagini/corsi/esempio.png");
    }

    public void setCuisineTypes(String... types) {
        controller.setCuisineTypes(types);
    }

    public void setCorso(Corso corso) {
        this.corso = corso;
        if (controller != null) {
            controller.setCorso(corso);
        }
    }

    @FXML
    private void handlePurchase() {
        controller.handlePurchase(this.corso);
    }

    @FXML
    private void handleEdit() {
        controller.handleEdit();
    }

    @FXML
    private void handleShowCalendar() {
        controller.handleShowCalendar();
    }

    public void setCourseImage(String imagePath) {
        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            courseImage.setImage(image);
        } catch (Exception e) {
            // System.out.println("Immagine non trovata: " + imagePath); // Disabilitato log immagini mancanti
        }
    }

    public void setCourseData(String title, String description, String start, String end, String freq, String price, String chef, String experience, String maxPeopleValue) {
        controller.setCourseData(title, description, start, end, freq, price, chef, experience, maxPeopleValue);
    }

    public void setChefMode(boolean isChef) {
        controller.setChefMode(isChef);
    }

    public void setEnrolledMode(boolean isEnrolled) {
        controller.setEnrolledMode(isEnrolled);
    }

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
    public Label getChefName() { return chefName; }
    public Label getChefExperience() { return chefExperience; }
    public ImageView getCourseImage() { return courseImage; }
    public CardCorsoController getController() {return controller;}
}