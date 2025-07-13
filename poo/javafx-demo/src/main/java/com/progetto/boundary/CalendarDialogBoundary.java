package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import com.progetto.controller.CalendarDialogController;
import com.progetto.Entity.EntityDto.Sessione;
import java.util.List;

public class CalendarDialogBoundary {

    @FXML private Label monthYearLabel;
    @FXML private GridPane calendarGrid;
    @FXML private VBox lessonDetailsArea;
    @FXML private Label selectedDateLabel;
    @FXML private VBox lessonsContainer;
    @FXML private Button closeBtn;

    private CalendarDialogController controller;
    private boolean isChef;
    private List<Sessione> sessioni;
    
    @FXML
    public void initialize() {
        controller = new CalendarDialogController(
            monthYearLabel, calendarGrid, lessonDetailsArea, selectedDateLabel, lessonsContainer, closeBtn, isChef
        );
        if (sessioni != null) {
            controller.setSessioni(sessioni);
        }
        controller.initialize();
    }

    @FXML
    private void prevMonth(ActionEvent event) {
        controller.prevMonth();
    }

    @FXML
    private void nextMonth(ActionEvent event) {
        controller.nextMonth();
    }

    @FXML
    private void closeDialog(ActionEvent event) {
        controller.closeDialog();
    }

    public void setChefMode(boolean isChef) {
        this.isChef = isChef;
    }

    public void setSessioni(List<Sessione> sessioni) {
        this.sessioni = sessioni;
        if (controller != null) {
            controller.setSessioni(sessioni);
        }
    }
}
