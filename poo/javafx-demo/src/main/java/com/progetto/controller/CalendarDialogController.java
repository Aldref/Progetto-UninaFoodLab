
package com.progetto.controller;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.progetto.Entity.EntityDto.Corso;
import com.progetto.Entity.EntityDto.Sessione;
import com.progetto.Entity.EntityDto.SessioneOnline;
import com.progetto.Entity.EntityDto.SessioniInPresenza;
import com.progetto.Entity.EntityDto.UtenteVisitatore;
import com.progetto.Entity.entityDao.UtenteVisitatoreDao;

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
    private Map<LocalDate, List<Sessione>> lessonsMap;
    private VBox selectedDayCell;

    private List<Sessione> userSessions = new ArrayList<>();
    // Permette di impostare le sessioni reali dal boundary
    public void setSessioni(List<Sessione> sessioni) {
        this.userSessions = sessioni != null ? sessioni : new ArrayList<>();
        // Aggiorna la mappa delle lezioni se già inizializzato
        if (lessonsMap != null) {
            lessonsMap.clear();
            if (userSessions != null && !userSessions.isEmpty()) {
                loadUserSessions();
                updateCalendar();
            }
        }
    }

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

    // Nuovo costruttore per passare le sessioni dell'utente
    public CalendarDialogController(Label monthYearLabel, GridPane calendarGrid, VBox lessonDetailsArea,
                                   Label selectedDateLabel, VBox lessonsContainer, Button closeBtn, boolean isChef, List<Sessione> userSessions) {
        this(monthYearLabel, calendarGrid, lessonDetailsArea, selectedDateLabel, lessonsContainer, closeBtn, isChef);
        if (userSessions != null) this.userSessions = userSessions;
    }

    public void initialize() {
        currentMonth = LocalDate.now().withDayOfMonth(1);
        lessonsMap = new HashMap<>();
        if (userSessions != null && !userSessions.isEmpty()) {
            loadUserSessions();
        } else {
            loadSampleLessons();
        }
        updateCalendar();
        selectedDateLabel.setText("Clicca su un giorno per vedere le lezioni");
        lessonDetailsArea.setVisible(true);
    }

    // Popola il calendario con le sessioni reali dell'utente
    private void loadUserSessions() {
        for (Sessione sessione : userSessions) {
            LocalDate data = null;
            Object rawData = sessione.getData();
            if (rawData instanceof LocalDate) {
                data = (LocalDate) rawData;
            } else if (rawData instanceof java.sql.Date) {
                data = ((java.sql.Date) rawData).toLocalDate();
            } else if (rawData instanceof String) {
                try {
                    data = LocalDate.parse((String) rawData);
                } catch (Exception e) {
                    System.out.println("Data sessione non parsabile: " + rawData);
                }
            }
            if (data != null) {
                lessonsMap.computeIfAbsent(data, k -> new ArrayList<>()).add(sessione);
            } else {
                System.out.println("Sessione ignorata (data non valida): " + sessione);
            }
        }
    }

    // Dati fittizi, facilmente sostituibili con dati da DB
    private void loadSampleLessons() {
        // Metodo placeholder: non aggiunge più lezioni fittizie, solo per compatibilità
        // lessonsMap.clear();
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
            List<Sessione> sessions = lessonsMap.get(date);
            int added = 0;
            for (Sessione sessione : sessions) {
                String tipo = "";
                String orario = sessione.getOrario() != null ? sessione.getOrario().toString() : "";
                if (sessione instanceof SessioniInPresenza) {
                    tipo = "[P]"; // Presenza
                } else if (sessione instanceof SessioneOnline) {
                    tipo = "[T]"; // Telematica
                }
                Label lessonLabel = new Label(tipo + " " + orario);
                lessonLabel.getStyleClass().add("lesson-indicator");
                cell.getChildren().add(lessonLabel);
                added++;
                if (added >= 3) break;
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
            List<Sessione> sessions = lessonsMap.get(date);
            for (Sessione sessione : sessions) {
                VBox lessonItem = createSessionItem(sessione);
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

    private VBox createSessionItem(Sessione sessione) {
        VBox item = new VBox(8);
        item.getStyleClass().add("lesson-item");

        Separator separator = new Separator();
        separator.getStyleClass().add("lesson-separator");

        VBox detailsContainer = new VBox(6);
        detailsContainer.getStyleClass().add("lesson-details-container");

        HBox startTimeBox = new HBox(8);
        Label startTimeIcon = new Label("🕐");
        startTimeIcon.getStyleClass().add("detail-icon");
        Label startTimeLabel = new Label("Orario: " + (sessione.getOrario() != null ? sessione.getOrario().toString() : ""));
        startTimeLabel.getStyleClass().add("detail-label");
        startTimeBox.getChildren().addAll(startTimeIcon, startTimeLabel);

        HBox durationBox = new HBox(8);
        Label durationIcon = new Label("⏱");
        durationIcon.getStyleClass().add("detail-icon");
        Label durationLabel = new Label("Durata: " + (sessione.getDurata() != null ? sessione.getDurata().toString() : ""));
        durationLabel.getStyleClass().add("detail-label");
        durationBox.getChildren().addAll(durationIcon, durationLabel);

        if (sessione instanceof SessioniInPresenza) {
            SessioniInPresenza s = (SessioniInPresenza) sessione;
            String nomeRicetta = "";
            if (s.getRicette() != null && !s.getRicette().isEmpty() && s.getRicette().get(0) != null) {
                nomeRicetta = s.getRicette().get(0).getNome();
            }
            String indirizzo = s.getVia() != null ? s.getVia() : "";
            String cap = s.getCap() != null ? s.getCap() : "";
            String citta = s.getCitta() != null ? s.getCitta() : "";

            Label ricettaLabel = new Label("Ricetta: " + nomeRicetta);
            ricettaLabel.getStyleClass().add("detail-label");
            ricettaLabel.setWrapText(true);

            HBox addressBox = new HBox(8);
            Label addressIcon = new Label("📍");
            addressIcon.getStyleClass().add("detail-icon");
            Label addressLabel = new Label("Indirizzo: " + indirizzo + (cap.isEmpty() ? "" : (", " + cap)) + (citta.isEmpty() ? "" : (" " + citta)));
            addressLabel.getStyleClass().add("detail-label");
            addressLabel.setWrapText(true);
            addressBox.getChildren().addAll(addressIcon, addressLabel);

            detailsContainer.getChildren().addAll(startTimeBox, durationBox, ricettaLabel, addressBox);
        } else if (sessione instanceof SessioneOnline) {
            SessioneOnline s = (SessioneOnline) sessione;
            String app = s.getApplicazione() != null ? s.getApplicazione() : "";
            String codice = s.getCodicechiamata() != null ? s.getCodicechiamata() : "";

            HBox appBox = new HBox(8);
            Label appIcon = new Label("💻");
            appIcon.getStyleClass().add("detail-icon");
            Label appLabel = new Label("App: " + app);
            appLabel.getStyleClass().add("detail-label");
            appBox.getChildren().addAll(appIcon, appLabel);

            HBox codeBox = new HBox(8);
            Label codeIcon = new Label("🔑");
            codeIcon.getStyleClass().add("detail-icon");
            Label codeLabel = new Label("Codice chiamata: " + codice);
            codeLabel.getStyleClass().add("detail-label");
            codeBox.getChildren().addAll(codeIcon, codeLabel);

            detailsContainer.getChildren().addAll(startTimeBox, durationBox, appBox, codeBox);
        }

        // Titolo/descrizione in alto: tipo e nome ricetta o app
        String descrizione;
        if (sessione instanceof SessioniInPresenza) {
            SessioniInPresenza s = (SessioniInPresenza) sessione;
            String nomeRicetta = "";
            if (s.getRicette() != null && !s.getRicette().isEmpty() && s.getRicette().get(0) != null) {
                nomeRicetta = s.getRicette().get(0).getNome();
            }
            descrizione = "[PRESENZA] " + nomeRicetta;
        } else if (sessione instanceof SessioneOnline) {
            SessioneOnline s = (SessioneOnline) sessione;
            descrizione = "[TELEMATICA] " + (s.getApplicazione() != null ? s.getApplicazione() : "");
        } else {
            descrizione = "Sessione";
        }
        Label descLabel = new Label(descrizione);
        descLabel.getStyleClass().add("lesson-description");
        descLabel.setWrapText(true);

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

}