
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
import com.progetto.Entity.EntityDto.Ricetta;
import com.progetto.Entity.EntityDto.Ingredienti;
import com.progetto.Entity.entityDao.UtenteVisitatoreDao;
import com.progetto.Entity.entityDao.IngredientiDao;
import com.progetto.utils.SuccessDialogUtils;

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
    public void setSessioni(List<Sessione> sessioni) {
        this.userSessions = sessioni != null ? sessioni : new ArrayList<>();
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
        }
        updateCalendar();
        selectedDateLabel.setText("Clicca su un giorno per vedere le lezioni");
        lessonDetailsArea.setVisible(true);
    }

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
                }
            }
            if (data != null) {
                lessonsMap.computeIfAbsent(data, k -> new ArrayList<>()).add(sessione);
            } else {
            }
        }
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
                    tipo = "[P]"; 
                } else if (sessione instanceof SessioneOnline) {
                    tipo = "[T]";
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


        Button confermaBtn = null;
        Label confermaMsg = null;

        if (sessione instanceof SessioniInPresenza) {
            SessioniInPresenza s = (SessioniInPresenza) sessione;
            String indirizzo = s.getVia() != null ? s.getVia() : "";
            String cap = s.getCap() != null ? s.getCap() : "";
            String citta = s.getCitta() != null ? s.getCitta() : "";

            // Mostra tutti i nomi delle ricette
            if (s.getRicette() != null && !s.getRicette().isEmpty()) {
                for (Ricetta ricetta : s.getRicette()) {
                    if (ricetta != null) {
                        Label ricettaLabel = new Label("Ricetta: " + ricetta.getNome());
                        ricettaLabel.getStyleClass().add("detail-label");
                        ricettaLabel.setWrapText(true);
                        detailsContainer.getChildren().add(ricettaLabel);
                    }
                }
            }

            HBox addressBox = new HBox(8);
            Label addressIcon = new Label("📍");
            addressIcon.getStyleClass().add("detail-icon");
            Label addressLabel = new Label("Indirizzo: " + indirizzo + (cap.isEmpty() ? "" : (", " + cap)) + (citta.isEmpty() ? "" : (" " + citta)));
            addressLabel.getStyleClass().add("detail-label");
            addressLabel.setWrapText(true);
            addressBox.getChildren().addAll(addressIcon, addressLabel);

            detailsContainer.getChildren().addAll(startTimeBox, durationBox, addressBox);

        if (isChef && s.getRicette() != null && !s.getRicette().isEmpty()) {
            for (Ricetta ricetta : s.getRicette()) {
                if (ricetta != null && ricetta.getIngredientiRicetta() != null && !ricetta.getIngredientiRicetta().isEmpty()) {
                    Label ingrTitle = new Label("Ingredienti totali per la sessione:");
                    ingrTitle.getStyleClass().add("detail-label");
                    detailsContainer.getChildren().add(ingrTitle);
                    for (Ingredienti ingr : ricetta.getIngredientiRicetta()) {
                        IngredientiDao ingrDao = new IngredientiDao();
                        ingrDao.recuperaQuantitaTotale(ingr, ricetta);
                        String ingrText = "- " + ingr.getNome() + ": " + ingr.getQuantitaTotale() + " " + ingr.getUnitaMisura();
                        Label ingrLabel = new Label(ingrText);
                        ingrLabel.getStyleClass().add("detail-label");
                        detailsContainer.getChildren().add(ingrLabel);
                    }
                }
            }
        }

            UtenteVisitatore user = UtenteVisitatore.loggedUser;
            if (!isChef && user != null) {
                boolean isIscritto = false;
                boolean giaConfermato = false;
                try {
                    isIscritto = user.getUtenteVisitatoreDao().isUtenteIscrittoAlCorso(user.getId_UtenteVisitatore(), s.getId_Corso());
                } catch (Exception e) {
                    isIscritto = false;
                }
                if (!isIscritto) {
                    confermaMsg = new Label("Iscriviti al corso per confermare la presenza");
                    confermaMsg.getStyleClass().add("conferma-presenza-msg");
                    confermaMsg.setWrapText(true);
                } else {
                    try {
                        giaConfermato = user.getUtenteVisitatoreDao().haGiaConfermatoPresenza(s.getId_Sessione(), user.getId_UtenteVisitatore());
                    } catch (Exception e) {
                        giaConfermato = false;
                    }
                    if (!giaConfermato) {
                        final Button confermaBtnFinal = new Button("Conferma presenza");
                        confermaBtnFinal.getStyleClass().add("conferma-presenza-btn");
                        final UtenteVisitatore userFinal = user;
                        final SessioniInPresenza sessioneFinal = s;
                        confermaBtnFinal.setOnAction(evt -> {
                            userFinal.getUtenteVisitatoreDao().partecipaAllaSessioneDalVivo(sessioneFinal, userFinal);
                            confermaBtnFinal.setDisable(true);
                            confermaBtnFinal.setText("Presenza confermata");
                            javafx.stage.Stage parentStage = null;
                            try {
                                parentStage = (javafx.stage.Stage) confermaBtnFinal.getScene().getWindow();
                            } catch (Exception ignored) {}
                            SuccessDialogUtils.showGenericSuccessDialog(parentStage, "Presenza confermata", "La tua presenza è stata confermata con successo.");
                        });
                        confermaBtn = confermaBtnFinal;
                    } else {
                        confermaMsg = new Label("Hai già confermato la presenza a questa sessione");
                        confermaMsg.getStyleClass().add("conferma-presenza-msg");
                        confermaMsg.setWrapText(true);
                    }
                }
            }

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

        String descrizione;
        if (sessione instanceof SessioniInPresenza) {
            SessioniInPresenza s = (SessioniInPresenza) sessione;
            String nomiRicette = "";
            if (s.getRicette() != null && !s.getRicette().isEmpty()) {
                List<String> nomi = new ArrayList<>();
                for (Ricetta ricetta : s.getRicette()) {
                    if (ricetta != null) {
                        nomi.add(ricetta.getNome());
                    }
                }
                nomiRicette = String.join(", ", nomi);
            }
            descrizione = "[PRESENZA] " + nomiRicette;
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
        if (confermaBtn != null) {
            item.getChildren().add(confermaBtn);
        } else if (confermaMsg != null) {
            item.getChildren().add(confermaMsg);
        }
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