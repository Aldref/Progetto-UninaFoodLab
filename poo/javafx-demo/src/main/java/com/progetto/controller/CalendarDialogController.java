package com.progetto.controller;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

public class CalendarDialogController {

    private Label monthYearLabel;
    private GridPane calendarGrid;
    private VBox lessonDetailsArea;
    private Label selectedDateLabel;
    private VBox lessonsContainer;
    private Button closeBtn;
    private boolean isChef;

    private LocalDate currentMonth;
    private LocalDate selectedDate;
    private Map<LocalDate, List<Lezione>> lessonsMap;
    private VBox selectedDayCell;

    public CalendarDialogController(Label monthYearLabel, GridPane calendarGrid, VBox lessonDetailsArea,
                                   Label selectedDateLabel, VBox lessonsContainer, Button closeBtn, boolean isChef) {
        this.monthYearLabel = monthYearLabel;
        this.calendarGrid = calendarGrid;
        this.lessonDetailsArea = lessonDetailsArea;
        this.selectedDateLabel = selectedDateLabel;
        this.lessonsContainer = lessonsContainer;
        this.closeBtn = closeBtn;
        this.isChef = isChef;
    }

    public void initialize() {
        currentMonth = LocalDate.now().withDayOfMonth(1);
        lessonsMap = new HashMap<>();
        loadSampleLessons();
        updateCalendar();
        selectedDateLabel.setText("Clicca su un giorno per vedere le lezioni");
        lessonDetailsArea.setVisible(true);
    }

    // Dati fittizi, facilmente sostituibili con dati da DB
    private void loadSampleLessons() {
        LocalDate today = LocalDate.now();
        lessonsMap.put(today.withDayOfMonth(15), Arrays.asList(
            new Lezione(
                "09:00", "3 ore", "Preparazione di tagliatelle e ravioli", "Napoli", "80100", "Via Roma 123",
                true, // presenza
                12,   // partecipanti
                Arrays.asList(
                    new Ingrediente("Farina", 100, "g"),
                    new Ingrediente("Uova", 1, "pz"),
                    new Ingrediente("Sale", 2, "g")
                )
            )
        ));
        lessonsMap.put(today.withDayOfMonth(22), Arrays.asList(
            new Lezione(
                "10:00", "3 ore", "Tiramisù e panna cotta", "Napoli", "80138", "Corso Umberto I, 45",
                false, // online
                0,
                Collections.emptyList()
            )
        ));
        lessonsMap.put(today.withDayOfMonth(29), Arrays.asList(
            new Lezione(
                "09:00", "3 ore", "Risotto ai funghi porcini", "Napoli", "80121", "Piazza del Plebiscito 1",
                true,
                8,
                Arrays.asList(
                    new Ingrediente("Riso", 80, "g"),
                    new Ingrediente("Funghi porcini", 50, "g"),
                    new Ingrediente("Brodo vegetale", 200, "ml")
                )
            )
        ));
    }

    private void updateCalendar() {
        String monthYear = currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN) + " " + currentMonth.getYear();
        monthYearLabel.setText(monthYear.substring(0, 1).toUpperCase() + monthYear.substring(1));
        calendarGrid.getChildren().clear();

        LocalDate firstDay = currentMonth;
        int daysInMonth = currentMonth.lengthOfMonth();
        int startDayOfWeek = firstDay.getDayOfWeek().getValue() - 1;

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
        Label dayLabel = new Label(String.valueOf(dayNumber));
        dayLabel.getStyleClass().add("day-number");
        cell.getChildren().add(dayLabel);

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

        cell.setOnMouseClicked(e -> selectDate(cell, date));
    }

    private void selectDate(VBox cell, LocalDate date) {
        if (selectedDayCell != null) {
            selectedDayCell.getStyleClass().remove("selected");
        }
        selectedDayCell = cell;
        cell.getStyleClass().add("selected");
        selectedDate = date;
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

        Label descLabel = new Label(lesson.getDescription());
        descLabel.getStyleClass().add("lesson-description");
        descLabel.setWrapText(true);

        Separator separator = new Separator();
        separator.getStyleClass().add("lesson-separator");

        VBox detailsContainer = new VBox(6);
        detailsContainer.getStyleClass().add("lesson-details-container");

        HBox startTimeBox = new HBox(8);
        Label startTimeIcon = new Label("🕐");
        startTimeIcon.getStyleClass().add("detail-icon");
        Label startTimeLabel = new Label("Orario: " + lesson.getStartTime());
        startTimeLabel.getStyleClass().add("detail-label");
        startTimeBox.getChildren().addAll(startTimeIcon, startTimeLabel);

        HBox durationBox = new HBox(8);
        Label durationIcon = new Label("⏱");
        durationIcon.getStyleClass().add("detail-icon");
        Label durationLabel = new Label("Durata: " + lesson.getDuration());
        durationLabel.getStyleClass().add("detail-label");
        durationBox.getChildren().addAll(durationIcon, durationLabel);

        HBox addressBox = new HBox(8);
        Label addressIcon = new Label("📍");
        addressIcon.getStyleClass().add("detail-icon");
        Label addressLabel = new Label("Indirizzo: " + lesson.getFullAddress());
        addressLabel.getStyleClass().add("detail-label");
        addressLabel.setWrapText(true);
        addressBox.getChildren().addAll(addressIcon, addressLabel);

        detailsContainer.getChildren().addAll(startTimeBox, durationBox, addressBox);

        // SOLO PER CHEF E SOLO PER CORSI IN PRESENZA
        if (isChef && lesson.isPresenza()) {
            Label partecipantiLabel = new Label("Partecipanti: " + lesson.getPartecipanti());
            partecipantiLabel.getStyleClass().add("detail-label");
            detailsContainer.getChildren().add(partecipantiLabel);

            if (lesson.getIngredienti() != null && !lesson.getIngredienti().isEmpty()) {
                Label ingredientiTitle = new Label("Ingredienti necessari:");
                ingredientiTitle.getStyleClass().add("detail-label");
                detailsContainer.getChildren().add(ingredientiTitle);

                for (Ingrediente ingr : lesson.getIngredienti()) {
                    double totale = ingr.getQuantitaPerPersona() * lesson.getPartecipanti();
                    Label ingrLabel = new Label("- " + ingr.getNome() + ": " + totale + " " + ingr.getUnita());
                    ingrLabel.getStyleClass().add("detail-label");
                    detailsContainer.getChildren().add(ingrLabel);
                }
            }
        }

        item.getChildren().addAll(descLabel, separator, detailsContainer);
        return item;
    }

    public void prevMonth() {
        currentMonth = currentMonth.minusMonths(1);
        updateCalendar();
        lessonDetailsArea.setVisible(false);
    }

    public void nextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        updateCalendar();
        lessonDetailsArea.setVisible(false);
    }

    public void closeDialog() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }

    // === CLASSI DATI ===
    public static class Lezione {
        private String startTime;
        private String duration;
        private String description;
        private String city;
        private String postalCode;
        private String address;
        private boolean presenza;
        private int partecipanti;
        private List<Ingrediente> ingredienti;

        public Lezione(String startTime, String duration, String description,
                       String city, String postalCode, String address,
                       boolean presenza, int partecipanti, List<Ingrediente> ingredienti) {
            this.startTime = startTime;
            this.duration = duration;
            this.description = description;
            this.city = city;
            this.postalCode = postalCode;
            this.address = address;
            this.presenza = presenza;
            this.partecipanti = partecipanti;
            this.ingredienti = ingredienti;
        }

        public String getStartTime() { return startTime; }
        public String getDuration() { return duration; }
        public String getDescription() { return description; }
        public String getCity() { return city; }
        public String getPostalCode() { return postalCode; }
        public String getAddress() { return address; }
        public String getFullAddress() {
            return address + ", " + postalCode + " " + city;
        }
        public boolean isPresenza() { return presenza; }
        public int getPartecipanti() { return partecipanti; }
        public List<Ingrediente> getIngredienti() { return ingredienti; }
    }

    public static class Ingrediente {
        private String nome;
        private double quantitaPerPersona;
        private String unita;

        public Ingrediente(String nome, double quantitaPerPersona, String unita) {
            this.nome = nome;
            this.quantitaPerPersona = quantitaPerPersona;
            this.unita = unita;
        }

        public String getNome() { return nome; }
        public double getQuantitaPerPersona() { return quantitaPerPersona; }
        public String getUnita() { return unita; }
    }
}