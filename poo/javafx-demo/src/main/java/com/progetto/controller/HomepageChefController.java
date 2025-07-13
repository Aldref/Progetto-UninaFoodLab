package com.progetto.controller;


import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import com.progetto.boundary.CardCorsoBoundary;
import com.progetto.boundary.LogoutDialogBoundary;
import com.progetto.utils.SceneSwitcher;
import com.progetto.Entity.EntityDto.Chef;
import com.progetto.Entity.EntityDto.Corso;
import com.progetto.Entity.entityDao.BarraDiRicercaDao;

public class HomepageChefController {
    private FlowPane mainContentArea;
    private Label pageLabel;
    private ComboBox<String> categoryComboBox;
    private ComboBox<String> frequencyComboBox;
    private Label chefNameLabel;
    private ImageView profileImageLarge;

    private List<Node> allCourseCards = new ArrayList<>();
    private int currentPage = 0;
    private final int CARDS_PER_PAGE = 12;

    public HomepageChefController(FlowPane mainContentArea, Label pageLabel, 
                                ComboBox<String> categoryComboBox, ComboBox<String> frequencyComboBox,
                                Label chefNameLabel,
                                javafx.scene.image.ImageView profileImageLarge) {
        this.mainContentArea = mainContentArea;
        this.pageLabel = pageLabel;
        this.categoryComboBox = categoryComboBox;
        this.frequencyComboBox = frequencyComboBox;
        this.chefNameLabel = chefNameLabel;
        this.profileImageLarge = profileImageLarge;
    }


    public void initializeSearchFilters() {
        categoryComboBox.getItems().clear();
        frequencyComboBox.getItems().clear();

        BarraDiRicercaDao barraDao = new BarraDiRicercaDao();
        List<String> categorie = barraDao.Categorie();
        List<String> frequenze = barraDao.CeraEnumFrequenza();
        categoryComboBox.getItems().add("Tutte le categorie");
        if (categorie != null) categoryComboBox.getItems().addAll(categorie);
        frequencyComboBox.getItems().add("Tutte le frequenze");
        if (frequenze != null) frequencyComboBox.getItems().addAll(frequenze);

        Chef chef = Chef.loggedUser;
        if (chef != null) {
            if (chef.getNome() == null || chef.getCognome() == null) {
                chef.getChefDao().recuperaDatiUtente(chef);
            }
            chefNameLabel.setText(chef.getNome() + " " + chef.getCognome());
        }
    }

    public void searchCourses() {
        String categoria = categoryComboBox.getValue();
        String frequenza = frequencyComboBox.getValue();
        loadChefCourses();
    }

    public void loadChefCourses() {
        allCourseCards.clear();
        Chef chef = Chef.loggedUser;
        if (chef == null) {
            updateCourseCards();
            return;
        }
        if (chef.getId_Chef() == 0) {
            chef.getChefDao().recuperaDatiUtente(chef);
        }
        chef.getChefDao().RecuperaCorsi(chef);
        List<Corso> corsi = chef.getCorsi();
        String categoriaFiltro = categoryComboBox.getValue();
        String frequenzaFiltro = frequencyComboBox.getValue();
        if (corsi != null) {
            for (Corso corso : corsi) {
                boolean matchCategoria = true;
                if (categoriaFiltro != null && !categoriaFiltro.equals("Tutte le categorie")) {
                    List<String> tipiCucina = corso.getTipiDiCucina();
                    matchCategoria = false;
                    String categoriaFiltroNorm = categoriaFiltro.trim().toLowerCase();
                    if (tipiCucina != null) {
                        for (String tipo : tipiCucina) {
                            if (tipo != null && tipo.trim().toLowerCase().equals(categoriaFiltroNorm)) {
                                matchCategoria = true;
                                break;
                            }
                        }
                    }
                }
                boolean matchFrequenza = true;
                if (frequenzaFiltro != null && !frequenzaFiltro.equals("Tutte le frequenze")) {
                    matchFrequenza = corso.getFrequenzaDelleSessioni() != null && corso.getFrequenzaDelleSessioni().equals(frequenzaFiltro);
                }
                if (!matchCategoria || !matchFrequenza) continue;
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cardcorso.fxml"));
                    Node card = loader.load();
                    CardCorsoBoundary boundary = loader.getController();
                    boundary.setChefMode(true);
                    boundary.setCorso(corso); 
                    String title = corso.getNome();
                    String description = corso.getDescrizione();
                    String startDate = corso.getDataInizio() != null ? corso.getDataInizio().toString() : "";
                    String endDate = corso.getDataFine() != null ? corso.getDataFine().toString() : "";
                    String frequency = corso.getFrequenzaDelleSessioni();
                    String price = null;
                    String chefName = chef.getNome() + " " + chef.getCognome();
                    String experience = String.valueOf(chef.getAnniDiEsperienza());
                    String maxPeople = corso.getMaxPersone() != 0 ? String.valueOf(corso.getMaxPersone()) : "";
                    boundary.setCourseData(title, description, startDate, endDate, frequency, price, chefName, experience, maxPeople);
                    List<String> tipiCucina = corso.getTipiDiCucina();
                    String cucina1 = tipiCucina != null && tipiCucina.size() > 0 ? tipiCucina.get(0) : "";
                    String cucina2 = tipiCucina != null && tipiCucina.size() > 1 ? tipiCucina.get(1) : "";
                    boundary.setCuisineTypes(cucina1, cucina2);
                    String imagePath = corso.getUrl_Propic() != null ? "/" + corso.getUrl_Propic() : "/immagini/corsi/esempio.png";
                    boundary.setCourseImage(imagePath);
                    allCourseCards.add(card);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        updateCourseCards();
    }

    private void updateCourseCards() {
        mainContentArea.getChildren().clear();
        if (allCourseCards.isEmpty()) {
            Label noCoursesLabel = new Label("Comincia creando il tuo primo corso!");
            noCoursesLabel.getStyleClass().add("no-courses-label");
            mainContentArea.getChildren().add(noCoursesLabel);
            pageLabel.setText("");
            return;
        }
        int start = currentPage * CARDS_PER_PAGE;
        int end = Math.min(start + CARDS_PER_PAGE, allCourseCards.size());
        for (int i = start; i < end; i++) {
            mainContentArea.getChildren().add(allCourseCards.get(i));
        }
        int totalPages = (int) Math.ceil((double) allCourseCards.size() / CARDS_PER_PAGE);
        pageLabel.setText("Pagina " + (currentPage + 1) + " di " + Math.max(1, totalPages));
    }

    public void goToCreateCourse() {
        try {
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/createcourse.fxml", "UninaFoodLab - Crea Nuovo Corso");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToMonthlyReport() {
        try {
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/monthlyreport.fxml", "UninaFoodLab - Resoconto Mensile");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToAccountManagement() {
        try {
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/accountmanagementchef.fxml", "UninaFoodLab - Gestione Account Chef");
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
        if ((currentPage + 1) * CARDS_PER_PAGE < allCourseCards.size()) {
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