package com.progetto.controller;

import com.progetto.utils.SceneSwitcher;
import com.progetto.boundary.PaymentPageBoundary;
import com.progetto.boundary.EditCourseBoundary;
import com.progetto.Entity.EntityDto.Corso;
import com.progetto.Entity.EntityDto.Sessione;
import com.progetto.Entity.entityDao.CorsoDao;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.util.ArrayList;

public class CardCorsoController {

    private final Button buyButton;
    private final Button editButton;
    private final HBox buttonsBox;
    private final VBox priceSection;
    private final Label acquistatoBadge;
    private final Label priceLabel;
    private final Button calendarButton;
    private final ImageView courseImage;
    private final Label courseTitle;
    private final Label courseDescription;
    private final Label startDate;
    private final Label endDate;
    private final Label frequency;
    private final Label chefName;
    private final Label chefExperience;
    private final Label cuisineTypeLabel1;
    private final Label cuisineTypeLabel2;
    private final Label maxPeople;


    private boolean isEnrolledPage = false;
    private boolean isChefMode = false;

    private Corso corso;

    public CardCorsoController(Button buyButton, Button editButton, HBox buttonsBox, VBox priceSection,
                              Label acquistatoBadge, Label priceLabel, Button calendarButton, ImageView courseImage,
                              Label courseTitle, Label courseDescription, Label startDate, Label endDate, Label frequency,
                              Label chefName, Label chefExperience,
                              Label cuisineTypeLabel1, Label cuisineTypeLabel2, Label maxPeople) {
        this.buyButton = buyButton;
        this.editButton = editButton;
        this.buttonsBox = buttonsBox;
        this.priceSection = priceSection;
        this.acquistatoBadge = acquistatoBadge;
        this.priceLabel = priceLabel;
        this.calendarButton = calendarButton;
        this.courseImage = courseImage;
        this.courseTitle = courseTitle;
        this.courseDescription = courseDescription;
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequency = frequency;
        this.chefName = chefName;
        this.chefExperience = chefExperience;
        this.cuisineTypeLabel1 = cuisineTypeLabel1;
        this.cuisineTypeLabel2 = cuisineTypeLabel2;
        this.maxPeople = maxPeople;

    }

    public void setCorso(Corso corso) {
        this.corso = corso;
    }

    public void initialize() {
        updateCardState();
    }

    public void setChefMode(boolean isChef) {
        this.isChefMode = isChef;
        this.isEnrolledPage = false; 
        updateCardState();
    }

    public void setEnrolledMode(boolean isEnrolled) {
        this.isEnrolledPage = isEnrolled;
        this.isChefMode = false; 
        updateCardState();
    }

    private void updateCardState() {
        Platform.runLater(() -> {
            if (isChefMode) {
                
                if (priceSection != null) {
                    priceSection.setVisible(false);
                    priceSection.setManaged(false);
                }
                if (buyButton != null) {
                    buyButton.setVisible(false);
                    buyButton.setManaged(false);
                }
                if (editButton != null) {
                    editButton.setVisible(true);
                    editButton.setManaged(true);
                }
                if (acquistatoBadge != null) {
                    acquistatoBadge.setVisible(false);
                }
                if (calendarButton != null) {
                    calendarButton.setVisible(true);
                    calendarButton.setManaged(true);
                }
                
                if (buttonsBox != null) {
                    buttonsBox.getStyleClass().remove("centered-calendar");
                }
            } else if (isEnrolledPage) {
                
                if (buyButton != null) {
                    buyButton.setVisible(false);
                    buyButton.setManaged(false);
                }
                if (editButton != null) {
                    editButton.setVisible(false);
                    editButton.setManaged(false);
                }
                if (priceSection != null) {
                    priceSection.setVisible(false);
                    priceSection.setManaged(false);
                }
                if (acquistatoBadge != null) {
                    acquistatoBadge.setVisible(true);
                }
                if (buttonsBox != null && !buttonsBox.getStyleClass().contains("centered-calendar")) {
                    buttonsBox.getStyleClass().add("centered-calendar");
                }
                if (calendarButton != null) {
                    calendarButton.setVisible(true);
                    calendarButton.setManaged(true);
                }
            } else {
                
                if (buyButton != null) {
                    buyButton.setVisible(true);
                    buyButton.setManaged(true);
                }
                if (editButton != null) {
                    editButton.setVisible(false);
                    editButton.setManaged(false);
                }
                if (priceSection != null) {
                    priceSection.setVisible(true);
                    priceSection.setManaged(true);
                }
                if (acquistatoBadge != null) {
                    acquistatoBadge.setVisible(false);
                }
                if (calendarButton != null) {
                    calendarButton.setVisible(true);
                    calendarButton.setManaged(true);
                }
                
                if (buttonsBox != null) {
                    buttonsBox.getStyleClass().remove("centered-calendar");
                }
            }
        });
    }

    public void setCourseData(String title, String description, String start, String end, String freq, String price, String chef, String experience, String maxPeopleValue) {
        if (courseTitle != null) courseTitle.setText(title);
        if (courseDescription != null) courseDescription.setText(description);
        if (startDate != null) startDate.setText(start);
        if (endDate != null) endDate.setText(end);
        if (frequency != null) frequency.setText(freq);
        if (priceLabel != null && price != null) priceLabel.setText(price);
        if (chefName != null && chef != null) chefName.setText(chef);
        if (chefExperience != null && experience != null) chefExperience.setText(experience + " anni di esperienza");
        if (maxPeople != null && maxPeopleValue != null) maxPeople.setText(maxPeopleValue);
    }

    public void setCuisineTypes(String... types) {
        if (cuisineTypeLabel1 != null) {
            if (types != null && types.length > 0 && types[0] != null && !types[0].trim().isEmpty()) {
                cuisineTypeLabel1.setText(types[0]);
                cuisineTypeLabel1.setVisible(true);
                cuisineTypeLabel1.setManaged(true);
            } else {
                cuisineTypeLabel1.setVisible(false);
                cuisineTypeLabel1.setManaged(false);
            }
        }
        if (cuisineTypeLabel2 != null) {
            if (types != null && types.length > 1 && types[1] != null && !types[1].trim().isEmpty()) {
                cuisineTypeLabel2.setText(types[1]);
                cuisineTypeLabel2.setVisible(true);
                cuisineTypeLabel2.setManaged(true);
            } else {
                cuisineTypeLabel2.setVisible(false);
                cuisineTypeLabel2.setManaged(false);
            }
        }
    }

    public void handlePurchase(Corso corsoSelezionato) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/paymentpage.fxml"));
            Parent root = loader.load();
            PaymentPageBoundary paymentBoundary = loader.getController();
            paymentBoundary.setSelectedCorso(corsoSelezionato);
            Stage stage = (Stage) buyButton.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("UninaFoodLab - Pagamento");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Errore", "Impossibile aprire la pagina di pagamento.");
        }
    }

    public void handleEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editcourse.fxml"));
            Parent root = loader.load();
            EditCourseBoundary boundary = loader.getController();
            if (corso != null) {
                System.out.println("[DEBUG] CardCorsoController.handleEdit: corso.getId_Corso() = " + corso.getId_Corso());
                boundary.setCourseId(corso.getId_Corso());
            } else {
                System.out.println("[DEBUG] CardCorsoController.handleEdit: corso è null!");
            }
            Stage stage = (Stage) editButton.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("UninaFoodLab - Modifica Corso");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Errore", "Impossibile aprire la pagina di modifica del corso.");
        }
    }

    public void handleShowCalendar() {
        try {
            if (corso == null) {
                showAlert("Errore", "Corso non impostato per questa card.");
                return;
            }
            CorsoDao corsoDao = new CorsoDao();
            ArrayList<Sessione> sessioni = corsoDao.recuperoSessioniPerCorso(corso);
            Stage stage = (Stage) calendarButton.getScene().getWindow();
            SceneSwitcher.showCalendarDialog(stage, sessioni, isChefMode);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Errore", "Impossibile aprire il calendario del corso.");
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void animateButton(Button button) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), button);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.05);
        scaleTransition.setToY(1.05);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(2);
        scaleTransition.play();
    }

    public void setEnrolledPage(boolean isEnrolled) {
        setEnrolledMode(isEnrolled);
    }
}