package com.progetto.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

public class CalendarDialogController {

    @FXML private Label monthYearLabel;
    @FXML private GridPane calendarGrid;
    @FXML private VBox lessonDetailsArea;
    @FXML private Label selectedDateLabel;
    @FXML private VBox lessonsContainer;
    @FXML private Button closeBtn;

    private LocalDate currentMonth;
    private LocalDate selectedDate;
    private Map<LocalDate, List<Lezione>> lessonsMap;
    private VBox selectedDayCell;

    @FXML
    public void initialize() {
        currentMonth = LocalDate.now().withDayOfMonth(1);
        lessonsMap = new HashMap<>();
        
        // Dati di esempio (sostituisci con dati reali dal DB)
        loadSampleLessons();
        
        updateCalendar();
        
        
        selectedDateLabel.setText("Clicca su un giorno per vedere le lezioni");
        lessonDetailsArea.setVisible(true);
    }

    // Esempio di caricamento di lezioni
    private void loadSampleLessons() {
        LocalDate today = LocalDate.now();
        
        lessonsMap.put(today.withDayOfMonth(15), Arrays.asList(
            new Lezione("09:00", "3 ore", "Preparazione di tagliatelle e ravioli", "Napoli", "80100", "Via Roma 123")
        ));
        
        lessonsMap.put(today.withDayOfMonth(22), Arrays.asList(
            new Lezione("10:00", "3 ore", "Tiramisù e panna cotta", "Napoli", "80138", "Corso Umberto I, 45")
        ));
        
        lessonsMap.put(today.withDayOfMonth(29), Arrays.asList(
            new Lezione("09:00", "3 ore", "Risotto ai funghi porcini", "Napoli", "80121", "Piazza del Plebiscito 1")
        ));
    }

    private void updateCalendar() {
        
        String monthYear = currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN) + " " + currentMonth.getYear();
        monthYearLabel.setText(monthYear.substring(0, 1).toUpperCase() + monthYear.substring(1));
        
        
        calendarGrid.getChildren().clear();
        
        
        LocalDate firstDay = currentMonth;
        int daysInMonth = currentMonth.lengthOfMonth();
        int startDayOfWeek = firstDay.getDayOfWeek().getValue() - 1; 
        
        // Crea le celle del calendario
        int dayCounter = 1;
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                VBox dayCell = createDayCell();
                
                int cellIndex = row * 7 + col;
                if (cellIndex >= startDayOfWeek && dayCounter <= daysInMonth) {
                    LocalDate cellDate = currentMonth.withDayOfMonth(dayCounter);
                    setupDayCell(dayCell, cellDate, dayCounter);
                    dayCounter++;
                }
                
                calendarGrid.add(dayCell, col, row);
            }
        }
    }

    private VBox createDayCell() {
        VBox cell = new VBox();
        cell.getStyleClass().add("day-cell");
        cell.setSpacing(3);
        return cell;
    }

    private void setupDayCell(VBox cell, LocalDate date, int dayNumber) {
        // Numero del giorno
        Label dayLabel = new Label(String.valueOf(dayNumber));
        dayLabel.getStyleClass().add("day-number");
        cell.getChildren().add(dayLabel);
        
        // Controlla se ci sono lezioni in questo giorno
        if (lessonsMap.containsKey(date)) {
            cell.getStyleClass().add("has-lesson");
            
            List<Lezione> lessons = lessonsMap.get(date);
            for (Lezione lesson : lessons) {
                Label lessonLabel = new Label(lesson.getStartTime());
                lessonLabel.getStyleClass().add("lesson-indicator");
                cell.getChildren().add(lessonLabel);
                
                if (cell.getChildren().size() > 3) break; 
            }
        }
        
        // Click handler
        cell.setOnMouseClicked(e -> selectDate(cell, date));
    }

    private void selectDate(VBox cell, LocalDate date) {
        // Rimuovi selezione precedente
        if (selectedDayCell != null) {
            selectedDayCell.getStyleClass().remove("selected");
        }
        
        // Seleziona nuova cella
        selectedDayCell = cell;
        cell.getStyleClass().add("selected");
        selectedDate = date;
        
        // Mostra dettagli lezioni
        showLessonDetails(date);
    }

    private void showLessonDetails(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ITALIAN);
        selectedDateLabel.setText("Lezioni del " + date.format(formatter));
        
        lessonsContainer.getChildren().clear();
        
        if (lessonsMap.containsKey(date)) {
            List<Lezione> lessons = lessonsMap.get(date);
            for (Lezione lesson : lessons) {
                VBox lessonItem = createLessonItem(lesson);
                lessonsContainer.getChildren().add(lessonItem);
            }
            lessonDetailsArea.setVisible(true);
        } else {
            Label noLessons = new Label("Nessuna lezione programmata per questo giorno");
            noLessons.getStyleClass().add("lesson-description");
            noLessons.setWrapText(true);
            lessonsContainer.getChildren().add(noLessons);
            lessonDetailsArea.setVisible(true);
        }
    }

    private VBox createLessonItem(Lezione lesson) {
        VBox item = new VBox(8);
        item.getStyleClass().add("lesson-item");
        
        // Descrizione della lezione
        Label descLabel = new Label(lesson.getDescription());
        descLabel.getStyleClass().add("lesson-description");
        descLabel.setWrapText(true);
        
        // Separator line
        Separator separator = new Separator();
        separator.getStyleClass().add("lesson-separator");
        
        // Container per i dettagli
        VBox detailsContainer = new VBox(6);
        detailsContainer.getStyleClass().add("lesson-details-container");
        
        // Orario di inizio
        HBox startTimeBox = new HBox(8);
        Label startTimeIcon = new Label("🕐");
        startTimeIcon.getStyleClass().add("detail-icon");
        Label startTimeLabel = new Label("Orario: " + lesson.getStartTime());
        startTimeLabel.getStyleClass().add("detail-label");
        startTimeBox.getChildren().addAll(startTimeIcon, startTimeLabel);
        
        // Durata
        HBox durationBox = new HBox(8);
        Label durationIcon = new Label("⏱");
        durationIcon.getStyleClass().add("detail-icon");
        Label durationLabel = new Label("Durata: " + lesson.getDuration());
        durationLabel.getStyleClass().add("detail-label");
        durationBox.getChildren().addAll(durationIcon, durationLabel);
        
        // Indirizzo
        HBox addressBox = new HBox(8);
        Label addressIcon = new Label("📍");
        addressIcon.getStyleClass().add("detail-icon");
        Label addressLabel = new Label("Indirizzo: " + lesson.getFullAddress());
        addressLabel.getStyleClass().add("detail-label");
        addressLabel.setWrapText(true);
        addressBox.getChildren().addAll(addressIcon, addressLabel);
        
        detailsContainer.getChildren().addAll(startTimeBox, durationBox, addressBox);
        
        item.getChildren().addAll(descLabel, separator, detailsContainer);
        return item;
    }

    @FXML
    private void prevMonth() {
        currentMonth = currentMonth.minusMonths(1);
        updateCalendar();
        lessonDetailsArea.setVisible(false);
    }

    @FXML
    private void nextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        updateCalendar();
        lessonDetailsArea.setVisible(false);
    }

    @FXML
    private void closeDialog() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }

    // Classe TEMPORANEA per rappresentare una lezione di esempio
    public static class Lezione {
        private String startTime;
        private String duration;
        private String description;
        private String city;
        private String postalCode;
        private String address;

        public Lezione(String startTime, String duration, String description, 
                       String city, String postalCode, String address) {
            this.startTime = startTime;
            this.duration = duration;
            this.description = description;
            this.city = city;
            this.postalCode = postalCode;
            this.address = address;
        }

        // Getter methods
        public String getStartTime() { return startTime; }
        public String getDuration() { return duration; }
        public String getDescription() { return description; }
        public String getCity() { return city; }
        public String getPostalCode() { return postalCode; }
        public String getAddress() { return address; }
        
        public String getFullAddress() {
            return address + ", " + postalCode + " " + city;
        }
    }
}