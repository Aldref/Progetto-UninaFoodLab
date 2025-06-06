package com.progetto.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.progetto.boundary.CardCorsoBoundary;
import com.progetto.boundary.LogoutDialogBoundary;
import com.progetto.utils.SceneSwitcher;

public class EnrolledCoursesController {

    private Label userNameLabel;
    private ComboBox<String> categoryComboBox;
    private ComboBox<String> frequencyComboBox;
    private ComboBox<String> lessonTypeComboBox;
    private Button searchBtn;
    private FlowPane enrolledCoursesArea;
    private ScrollPane enrolledCoursesScrollPane;
    private Label enrolledCountLabel;
    private Button prevPageBtn;
    private Button nextPageBtn;
    private Label pageLabel;

    private List<Node> allCards = new ArrayList<>();
    private int currentPage = 0;
    private final int CARDS_PER_PAGE = 12;

    public EnrolledCoursesController(
        Label userNameLabel, ComboBox<String> categoryComboBox, ComboBox<String> frequencyComboBox,
        ComboBox<String> lessonTypeComboBox, Button searchBtn, FlowPane enrolledCoursesArea,
        ScrollPane enrolledCoursesScrollPane, Label enrolledCountLabel, Button prevPageBtn,
        Button nextPageBtn, Label pageLabel
    ) {
        this.userNameLabel = userNameLabel;
        this.categoryComboBox = categoryComboBox;
        this.frequencyComboBox = frequencyComboBox;
        this.lessonTypeComboBox = lessonTypeComboBox;
        this.searchBtn = searchBtn;
        this.enrolledCoursesArea = enrolledCoursesArea;
        this.enrolledCoursesScrollPane = enrolledCoursesScrollPane;
        this.enrolledCountLabel = enrolledCountLabel;
        this.prevPageBtn = prevPageBtn;
        this.nextPageBtn = nextPageBtn;
        this.pageLabel = pageLabel;
    }

    public void initialize() {
        categoryComboBox.getItems().clear();
        frequencyComboBox.getItems().clear();
        lessonTypeComboBox.getItems().clear();

        searchBtn.setOnAction(e -> handleSearch());

        loadEnrolledCourses();
    }
    //temporanei
    private void loadEnrolledCourses() {
        allCards.clear();
        try {
            for (int i = 1; i <= 5; i++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cardcorso.fxml"));
                Node card = loader.load();
                CardCorsoBoundary boundary = loader.getController();

                // Dati di esempio personalizzabili
                String img = "/immagini/corsi/esempio.png";
                String titolo = "Corso Iscritto " + i;
                String descrizione = "Attualmente l’applicazione utilizza dati di esempio per simulare il comportamento dell’interfaccia utente in attesa dell’integrazione con il database. È stata implementata una prima separazione tra boundary e controller, dove il boundary gestisce gli elementi grafici tramite FXML e il controller elabora le azioni dell’utente. Anche se il controller utilizza ancora componenti JavaFX, questa struttura è pensata per facilitare la successiva sostituzione con logica basata su dati reali. In futuro, il controller sarà rifattorizzato per lavorare solo con oggetti dati (DTO), migliorando così l’architettura secondo i principi MVC o Clean Architecture.";
                String inizio = "01/09/2025";
                String fine = "30/11/2025";
                String frequenza = (i % 2 == 0) ? "1 volta a settimana" : "2 volte a settimana";
                String tipoLezione = (i % 2 == 0) ? "Online" : "Presenza";
                String prezzo = (i % 2 == 0) ? "200€" : "350€";

                boundary.setCourseImage(img);
                boundary.setCourseTitle(titolo);
                boundary.setCourseDescription(descrizione);
                boundary.setStartDate(inizio);
                boundary.setEndDate(fine);
                boundary.setFrequency(frequenza);
                boundary.setLessonType(tipoLezione);
                boundary.setPrice(prezzo);
                boundary.setAcquistato(true);

                CardCorsoController controller = boundary.getController();
                controller.setEnrolledPage(true);

                allCards.add(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateCards();
    }

    private void updateCards() {
        enrolledCoursesArea.getChildren().clear();
        int start = currentPage * CARDS_PER_PAGE;
        int end = Math.min(start + CARDS_PER_PAGE, allCards.size());
        for (int i = start; i < end; i++) {
            enrolledCoursesArea.getChildren().add(allCards.get(i));
        }
        pageLabel.setText("Pagina " + (currentPage + 1) + " di " + Math.max(1, (int)Math.ceil((double)allCards.size()/CARDS_PER_PAGE)));
        enrolledCountLabel.setText("Sei iscritto a " + allCards.size() + " corsi");
        prevPageBtn.setDisable(currentPage == 0);
        nextPageBtn.setDisable((currentPage + 1) * CARDS_PER_PAGE >= allCards.size());
    }

    public void nextPage() {
        if ((currentPage + 1) * CARDS_PER_PAGE < allCards.size()) {
            currentPage++;
            updateCards();
        }
    }

    public void prevPage() {
        if (currentPage > 0) {
            currentPage--;
            updateCards();
        }
    }

    public void handleSearch() {
        String categoria = categoryComboBox.getValue();
        String frequenza = frequencyComboBox.getValue();
        String tipoLezione = lessonTypeComboBox.getValue();

        System.out.println("Categoria: " + categoria);
        System.out.println("Frequenza: " + frequenza);
        System.out.println("Tipo Lezione: " + tipoLezione);

        // TODO: Qui inserisci la logica per filtrare i corsi iscritti dal database
        // Esempio: aggiorna allCards con i risultati filtrati e poi chiama updateCards()
        currentPage = 0;
        updateCards();
    }

    public void viewCoursesClick() {
        try {
            Stage stage = (Stage) enrolledCoursesArea.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/homepageutente.fxml", "UninaFoodLab - Homepage");
        } catch (IOException e) {
            System.err.println("Errore nel cambio pagina: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void goToAccountManagement() {
        try {
            Stage stage = (Stage) enrolledCoursesArea.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/accountmanagement.fxml", "UninaFoodLab - Gestione Account");
        } catch (IOException e) {
            System.err.println("Errore nel cambio pagina: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void LogoutClick() {
        try {
            Stage stage = (Stage) enrolledCoursesArea.getScene().getWindow();
            LogoutDialogBoundary dialogBoundary = SceneSwitcher.showLogoutDialog(stage);

            if (dialogBoundary.isConfirmed()) {
                SceneSwitcher.switchToLogin(stage, "/fxml/loginpage.fxml", "UninaFoodLab - Login");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}