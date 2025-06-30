package com.progetto.controller;

import com.progetto.boundary.CardCorsoBoundary;
import com.progetto.boundary.LogoutDialogBoundary;
import com.progetto.utils.SceneSwitcher;
import com.progetto.Entity.entityDao.CorsoDao;
import com.progetto.Entity.entityDao.UtenteVisitatoreDao;
import com.progetto.Entity.EntityDto.UtenteVisitatore;
import com.progetto.Entity.EntityDto.Corso;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.io.IOException;


import com.progetto.boundary.HomepageUtenteBoundary;
import javafx.concurrent.Task;

public class HomepageUtenteController {
    private FlowPane mainContentArea;
    private Label pageLabel;
    private ComboBox<String> categoryComboBox;
    private ComboBox<String> frequencyComboBox;
    private Label userNameLabel;
    private HomepageUtenteBoundary boundary;

    private List<Node> filteredCourseCards = new ArrayList<>();
    private int currentPage = 0;
    private final int CARDS_PER_PAGE = 12;

    // Thread di caricamento corsi
    private Thread loadCoursesThread;

    // Costruttore corretto: FlowPane, Label, ComboBox, ComboBox, Label, HomepageUtenteBoundary
    public HomepageUtenteController(FlowPane mainContentArea, Label pageLabel, 
                                    ComboBox<String> categoryComboBox, ComboBox<String> frequencyComboBox,
                                    Label userNameLabel, HomepageUtenteBoundary boundary) {
        this.mainContentArea = mainContentArea;
        this.pageLabel = pageLabel;
        this.categoryComboBox = categoryComboBox;
        this.frequencyComboBox = frequencyComboBox;
        this.userNameLabel = userNameLabel;
        this.boundary = boundary;
    }


    public void initialize() {
        initializeSearchFilters();
        setupWindowCloseListener();
        loadCourses();
    }

    public void initializeSearchFilters() {
        categoryComboBox.getItems().clear();
        frequencyComboBox.getItems().clear();
        // lessonTypeComboBox rimosso
        // Il nome utente viene impostato dalla boundary
    }


    private void setupWindowCloseListener() {
        if (mainContentArea != null && mainContentArea.getScene() != null && mainContentArea.getScene().getWindow() != null) {
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
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
                            ((Stage) newWin).setOnCloseRequest(event -> {
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


    public void searchCourses() {
        // Quando si effettua una ricerca, si parte sempre dalla prima pagina
        resetPageAndSearch();
    }


    public void resetPageAndSearch() {
        currentPage = 0;
        loadCourses();
    }


    public void loadCourses() {
        if (boundary != null) boundary.showLoadingIndicator();
        filteredCourseCards.clear();
        Task<Void> loadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    CorsoDao corsoDao = new CorsoDao();
                    UtenteVisitatoreDao utenteDao = new UtenteVisitatoreDao();
                    UtenteVisitatore utente = UtenteVisitatore.loggedUser;
                    // Recupera tutti i corsi
                    ArrayList<Corso> corsi = corsoDao.recuperaCorsi();
                    // Recupera i corsi a cui l'utente è iscritto
                    utenteDao.RecuperaCorsi(utente);
                    List<Corso> corsiIscritti = utente.getCorsi();
                    HashSet<Integer> idCorsiIscritti = new HashSet<>();
                    if (corsiIscritti != null) {
                        for (Corso c : corsiIscritti) {
                            idCorsiIscritti.add(c.getId_Corso());
                        }
                    }
                    // Ottieni i filtri selezionati
                    String categoriaFiltro = categoryComboBox.getValue();
                    String frequenzaFiltro = frequencyComboBox.getValue();
                    for (Corso corso : corsi) {
                        if (idCorsiIscritti.contains(corso.getId_Corso())) continue;
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
                        boundary.setupFromCorso(corso, false);
                        // Aggiorna la lista in modo thread-safe
                        javafx.application.Platform.runLater(() -> filteredCourseCards.add(card));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void succeeded() {
                updateCourseCards();
                if (boundary != null) boundary.hideLoadingIndicator();
            }

            @Override
            protected void failed() {
                if (boundary != null) boundary.hideLoadingIndicator();
            }
        };
        Thread t = new Thread(loadTask);
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
        pageLabel.setText("Pagina " + (currentPage + 1) + " di " + Math.max(1, totalPages));
    }

    public void goToEnrolledCourses() {
        try {
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/enrolledcourses.fxml", "UninaFoodLab - Corsi Iscritti");
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
}