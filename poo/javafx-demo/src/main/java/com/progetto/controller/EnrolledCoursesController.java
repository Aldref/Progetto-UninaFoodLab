package com.progetto.controller;

import com.progetto.boundary.CardCorsoBoundary;
import com.progetto.boundary.LogoutDialogBoundary;
import com.progetto.utils.SceneSwitcher;
import com.progetto.Entity.EntityDto.UtenteVisitatore;
import com.progetto.Entity.EntityDto.Corso;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.util.ArrayList;
import com.progetto.Entity.entityDao.BarraDiRicercaDao;
import java.util.List;
import java.io.IOException;

import com.progetto.boundary.EnrolledCoursesBoundary;

import javafx.stage.WindowEvent;

public class EnrolledCoursesController {
    private Label userNameLabel;
    private ComboBox<String> categoryComboBox;
    private ComboBox<String> frequencyComboBox;
    // Rimossa lessonTypeComboBox
    private Button searchButton;
    private FlowPane mainContentArea;
    private ScrollPane scrollPane;
    private Label pageLabel;
    private Button prevButton;
    private Button nextButton;
    private Label totalCoursesLabel;
    private EnrolledCoursesBoundary boundary;

    // Variabili per la paginazione
    // Caching delle card: crea tutte le card solo quando cambia la lista corsi, non ad ogni pagina
    private List<Node> filteredCourseCards = new ArrayList<>();
    private List<Corso> cachedCorsi = new ArrayList<>();
    private int currentPage = 0;
    private final int CARDS_PER_PAGE = 12;

    // Thread di caricamento corsi
    private Thread loadCoursesThread;

    public EnrolledCoursesController(Label userNameLabel, ComboBox<String> categoryComboBox,
                                    ComboBox<String> frequencyComboBox,
                                    Button searchButton, FlowPane mainContentArea, ScrollPane scrollPane,
                                    Label pageLabel, Button prevButton, Button nextButton, Label totalCoursesLabel,
                                    EnrolledCoursesBoundary boundary) {
        this.userNameLabel = userNameLabel;
        this.categoryComboBox = categoryComboBox;
        this.frequencyComboBox = frequencyComboBox;
        this.searchButton = searchButton;
        this.mainContentArea = mainContentArea;
        this.scrollPane = scrollPane;
        this.pageLabel = pageLabel;
        this.prevButton = prevButton;
        this.nextButton = nextButton;
        this.totalCoursesLabel = totalCoursesLabel;
        this.boundary = boundary;
    }

    public void initialize() {
        initializeSearchFilters();
        setupWindowCloseListener();
        loadEnrolledCourses();
    }

    /**
     * Aggiunge un listener alla finestra principale per interrompere il thread di caricamento corsi alla chiusura.
     */
    private void setupWindowCloseListener() {
        if (mainContentArea != null && mainContentArea.getScene() != null && mainContentArea.getScene().getWindow() != null) {
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
                if (loadCoursesThread != null && loadCoursesThread.isAlive()) {
                    loadCoursesThread.interrupt();
                }
            });
        } else {
            // Se la scena non è ancora pronta, aggiungi un listener per quando viene impostata
            mainContentArea.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.windowProperty().addListener((obsWin, oldWin, newWin) -> {
                        if (newWin != null) {
                            ((Stage) newWin).addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
                                if (loadCoursesThread != null && loadCoursesThread.isAlive()) {
                                    loadCoursesThread.interrupt();
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    public void initializeSearchFilters() {
        // Inizializza i filtri di ricerca prendendo i dati dal DB
        categoryComboBox.getItems().clear();
        frequencyComboBox.getItems().clear();
        // Prendi le categorie e le frequenze dal DB
        BarraDiRicercaDao barraDao = new BarraDiRicercaDao();
        ArrayList<String> categorie = barraDao.Categorie();
        ArrayList<String> frequenze = barraDao.CeraEnumFrequenza();
        categoryComboBox.getItems().add("Tutte le categorie");
        if (categorie != null) categoryComboBox.getItems().addAll(categorie);
        frequencyComboBox.getItems().add("Tutte le frequenze");
        if (frequenze != null) frequencyComboBox.getItems().addAll(frequenze);
        // Imposta i valori di default
        categoryComboBox.setValue("Tutte le categorie");
        frequencyComboBox.setValue("Tutte le frequenze");
        // Il nome utente viene impostato dalla boundary
    }

    public void handleSearch() {
        // Quando si effettua una ricerca, si parte sempre dalla prima pagina
        resetPageAndSearch();
    }

    public void resetPageAndSearch() {
        currentPage = 0;
        loadEnrolledCourses();
    }

    public void viewCoursesClick() {
        // Metodo per visualizzare i corsi (se necessario)
        loadEnrolledCourses();
    }

    public void loadEnrolledCourses() {
        if (boundary != null) boundary.showLoadingIndicator();
        Thread t = new Thread(() -> {
            try {
                UtenteVisitatore utente = UtenteVisitatore.loggedUser;
                if (utente == null) {
                    filteredCourseCards.clear();
                    cachedCorsi.clear();
                    javafx.application.Platform.runLater(() -> {
                        updateCourseCards();
                        updateTotalCoursesLabel();
                        if (boundary != null) boundary.hideLoadingIndicator();
                    });
                    return;
                }
                // Recupera i corsi dal database
                utente.getUtenteVisitatoreDao().RecuperaCorsi(utente);
                List<Corso> corsi = utente.getCorsi();
                // Se la lista corsi non è cambiata, non ricreare le card
                if (corsi != null && corsi.equals(cachedCorsi)) {
                    javafx.application.Platform.runLater(() -> {
                        updateCourseCards();
                        updateTotalCoursesLabel();
                        if (boundary != null) boundary.hideLoadingIndicator();
                    });
                    return;
                }
                filteredCourseCards.clear();
                cachedCorsi = corsi != null ? new ArrayList<>(corsi) : new ArrayList<>();
                List<Node> newCards = new ArrayList<>();
                if (corsi != null) {
                    // Applica i filtri
                    String categoriaFiltro = categoryComboBox.getValue();
                    String frequenzaFiltro = frequencyComboBox.getValue();
                    for (Corso corso : corsi) {
                        boolean matchCategoria = true;
                        if (categoriaFiltro != null && !categoriaFiltro.equals("Tutte le categorie")) {
                            List<String> tipiCucina = corso.getTipiDiCucina();
                            matchCategoria = false;
                            String categoriaFiltroNorm = categoriaFiltro.trim().toLowerCase();
                            for (String tipo : tipiCucina) {
                                if (tipo != null && tipo.trim().toLowerCase().equals(categoriaFiltroNorm)) {
                                    matchCategoria = true;
                                    break;
                                }
                            }
                        }
                        boolean matchFrequenza = frequenzaFiltro == null || frequenzaFiltro.equals("Tutte le frequenze") || (corso.getFrequenzaDelleSessioni() != null && corso.getFrequenzaDelleSessioni().equals(frequenzaFiltro));
                        if (!matchCategoria || !matchFrequenza) continue;
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cardcorso.fxml"));
                        Node card = loader.load();
                        CardCorsoBoundary boundary = loader.getController();
                        boundary.setupFromCorso(corso, true);
                        newCards.add(card);
                    }
                }
                javafx.application.Platform.runLater(() -> {
                    filteredCourseCards.clear();
                    filteredCourseCards.addAll(newCards);
                    updateCourseCards();
                    updateTotalCoursesLabel();
                    if (boundary != null) boundary.hideLoadingIndicator();
                });
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    if (boundary != null) boundary.hideLoadingIndicator();
                });
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void updateCourseCards() {
        mainContentArea.getChildren().clear();
        int start = currentPage * CARDS_PER_PAGE;
        int end = Math.min(start + CARDS_PER_PAGE, filteredCourseCards.size());
        if (start < 0) start = 0;
        if (end < 0) end = 0;
        for (int i = start; i < end; i++) {
            mainContentArea.getChildren().add(filteredCourseCards.get(i));
        }
        int totalPages = (int) Math.ceil((double) filteredCourseCards.size() / CARDS_PER_PAGE);
        // RIMOSSO IL LABEL DELLA PAGINA COME RICHIESTO
        // pageLabel.setText("Pagina " + (currentPage + 1) + " di " + Math.max(1, totalPages));
        // Aggiorna i pulsanti di navigazione
        if (prevButton != null) {
            prevButton.setDisable(currentPage == 0);
        }
        if (nextButton != null) {
            nextButton.setDisable((currentPage + 1) * CARDS_PER_PAGE >= filteredCourseCards.size());
        }
    }


    private void updateTotalCoursesLabel() {
        if (totalCoursesLabel != null) {
            totalCoursesLabel.setText("Totale corsi iscritti: " + filteredCourseCards.size());
        }
    }


    public void nextPage() {
        if ((currentPage + 1) * CARDS_PER_PAGE < filteredCourseCards.size()) {
            currentPage++;
            updateCourseCards();
        }
    }

    public void prevPage() {
        if (currentPage > 0) {
            currentPage--;
            updateCourseCards();
        }
    }

    public void goToHomepage() {
        try {
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/homepageutente.fxml", "UninaFoodLab - Homepage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToUserCards() {
        try {
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/usercards.fxml", "UninaFoodLab - Carte Utente");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToAccountManagement() {
        try {
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/accountmanagement.fxml", "UninaFoodLab - Gestione Account");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void LogoutClick() {
        try {
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            LogoutDialogBoundary dialogBoundary = SceneSwitcher.showLogoutDialog(stage);

            if (dialogBoundary.isConfirmed()) {
                SceneSwitcher.switchToLogin(stage, "/fxml/loginpage.fxml", "UninaFoodLab - Login");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}