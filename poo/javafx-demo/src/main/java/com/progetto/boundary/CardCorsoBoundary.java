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
import com.progetto.utils.FrequenzaSessioniProvider;
import java.util.List;

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
    private Corso corso; 

    @FXML
    private void initialize() {
        controller = new CardCorsoController(buyButton, editButton, buttonsBox, priceSection, 
                                           acquistatoBadge, priceLabel, calendarButton, courseImage,
                                           courseTitle, courseDescription, startDate, endDate, frequency,
                                           chefName, chefExperience,
                                           cuisineTypeLabel1, cuisineTypeLabel2, maxPeople);
        controller.initialize();
        setCourseImage("/immagini/corso_default.png");
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

    public void setupFromCorso(Corso corso, boolean enrolledMode) {
        setCorso(corso);
        setEnrolledMode(enrolledMode);
        String title = corso.getNome();
        String description = corso.getDescrizione();
        String startDate = corso.getDataInizio() != null ? corso.getDataInizio().toString() : "";
        String endDate = corso.getDataFine() != null ? corso.getDataFine().toString() : "";
        String frequency = corso.getFrequenzaDelleSessioni();
        String price = "€" + String.format("%.2f", corso.getPrezzo());
        String chefName = corso.getChefNome() + (corso.getChefCognome() != null && !corso.getChefCognome().isEmpty() ? (" " + corso.getChefCognome()) : "");
        String experience = corso.getChefEsperienza() > 0 ? String.valueOf(corso.getChefEsperienza()) : "";
        String maxPeople = String.valueOf(corso.getMaxPersone());
        setCourseData(title, description, startDate, endDate, frequency, price, chefName, experience, maxPeople);
        List<String> tipiCucina = corso.getTipiDiCucina();
        String cucina1 = tipiCucina.size() > 0 ? tipiCucina.get(0) : "";
        String cucina2 = tipiCucina.size() > 1 ? tipiCucina.get(1) : "";
        setCuisineTypes(cucina1, cucina2);
        String imagePath = corso.getUrl_Propic() != null && !corso.getUrl_Propic().isEmpty() ? corso.getUrl_Propic() : "/immagini/corso_default.png";
        setCourseImage(imagePath);
    }

    public void setCourseImage(String imagePath) {
        try {
            Image image;
            if (imagePath != null && (imagePath.startsWith("/immagini/") || imagePath.startsWith("immagini/"))) {
                String path = imagePath.startsWith("/") ? imagePath : "/" + imagePath;
                image = new Image(getClass().getResourceAsStream(path));
            } else if (imagePath != null && (new java.io.File("src/main/resources/" + imagePath)).exists()) {
                image = new Image((new java.io.File("src/main/resources/" + imagePath)).toURI().toString());
            } else {
                image = new Image(getClass().getResourceAsStream("/immagini/corso_default.png"));
            }
            courseImage.setImage(image);
        } catch (Exception e) {
            try {
                Image image = new Image(getClass().getResourceAsStream("/immagini/corso_default.png"));
                courseImage.setImage(image);
            } catch (Exception ignored) {}
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