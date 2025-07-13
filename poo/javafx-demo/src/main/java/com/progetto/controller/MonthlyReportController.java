package com.progetto.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import com.progetto.utils.SceneSwitcher;
import com.progetto.boundary.LogoutDialogBoundary;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import com.progetto.Entity.EntityDto.Chef;

import com.progetto.Entity.entityDao.GraficoChefDao;
import com.progetto.Entity.EntityDto.GraficoChef;

public class MonthlyReportController {
    private final Label monthYearLabel;
    private final ComboBox<String> monthComboBox;
    private final ComboBox<Integer> yearComboBox;

    private final Label totalCoursesLabel;
    private final Label onlineSessionsLabel;
    private final Label practicalSessionsLabel;
    private final Label monthlyEarningsLabel;

    private final PieChart sessionsChart;
    private final BarChart<String, Number> recipesChart;

    private final Label avgRecipesLabel;
    private final Label maxRecipesLabel;
    private final Label minRecipesLabel;
    private final Label totalRecipesLabel;

    private final Chef chef;
    private final GraficoChefDao graficoChefDao;



    public MonthlyReportController(
            Label monthYearLabel, ComboBox<String> monthComboBox, ComboBox<Integer> yearComboBox,
            Label totalCoursesLabel, Label onlineSessionsLabel, Label practicalSessionsLabel, Label monthlyEarningsLabel,
            PieChart sessionsChart, BarChart<String, Number> recipesChart,
            Label avgRecipesLabel, Label maxRecipesLabel, Label minRecipesLabel, Label totalRecipesLabel,
            Chef chef) {
        this.monthYearLabel = monthYearLabel;
        this.monthComboBox = monthComboBox;
        this.yearComboBox = yearComboBox;
        this.totalCoursesLabel = totalCoursesLabel;
        this.onlineSessionsLabel = onlineSessionsLabel;
        this.practicalSessionsLabel = practicalSessionsLabel;
        this.monthlyEarningsLabel = monthlyEarningsLabel;
        this.sessionsChart = sessionsChart;
        this.recipesChart = recipesChart;
        this.avgRecipesLabel = avgRecipesLabel;
        this.maxRecipesLabel = maxRecipesLabel;
        this.minRecipesLabel = minRecipesLabel;
        this.totalRecipesLabel = totalRecipesLabel;
        this.chef = Chef.loggedUser;
        this.graficoChefDao = new GraficoChefDao();
    }

    public void initialize() {
        setupDateSelectors();
        loadReportData();
    }

    private void setupDateSelectors() {
        
        String[] months = {
            "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno",
            "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"
        };
        monthComboBox.getItems().addAll(months);
        
        int currentYear = LocalDate.now().getYear();
        for (int year = currentYear - 2; year <= currentYear + 1; year++) {
            yearComboBox.getItems().add(year);
        }
        
        LocalDate now = LocalDate.now();
        monthComboBox.setValue(now.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN));
        yearComboBox.setValue(now.getYear());
        
        updateMonthYearLabel();
    }

    private void updateMonthYearLabel() {
        String month = monthComboBox.getValue();
        Integer year = yearComboBox.getValue();
        if (month != null && year != null) {
            monthYearLabel.setText("Resoconto - " + month + " " + year);
        }
    }

    private void loadReportData() {
        Integer selectedYear = yearComboBox.getValue();
        int mese = monthComboBox.getSelectionModel().getSelectedIndex() + 1;
        int anno = (selectedYear != null) ? selectedYear : LocalDate.now().getYear();
        GraficoChef grafico = new GraficoChef();
        grafico.setNumeroMassimo(graficoChefDao.RicavaMassimo(chef, mese, anno));
        grafico.setNumeroMinimo(graficoChefDao.RicavaMinimo(chef, mese, anno));
        grafico.setMedia(graficoChefDao.RicavaMedia(chef, mese, anno));
        grafico.setNumeriCorsi(graficoChefDao.RicavaNumeroCorsi(chef, mese, anno));
        grafico.setNumeroSessioniInPresenza(graficoChefDao.RicavaNumeroSessioniInPresenza(chef, mese, anno));
        grafico.setNumerosessionitelematiche(graficoChefDao.RicavaNumeroSesssioniTelematiche(chef, mese, anno));
        double monthlyEarnings = graficoChefDao.ricavaGuadagno(chef, mese, anno);
        int totalRecipes = (int) Math.round((grafico.getNumeroSessioniInPresenza() + grafico.getNumerosessionitelematiche()) * grafico.getMedia());

        updateStatistics(grafico, monthlyEarnings, totalRecipes);
        updateChartsData(grafico, monthlyEarnings);
    }

    private void updateStatistics(GraficoChef grafico, double monthlyEarnings, int totalRecipes) {
        totalCoursesLabel.setText(String.valueOf(grafico.getNumeriCorsi()));
        onlineSessionsLabel.setText(String.valueOf(grafico.getNumerosessionitelematiche()));
        practicalSessionsLabel.setText(String.valueOf(grafico.getNumeroSessioniInPresenza()));
        monthlyEarningsLabel.setText(String.format("€ %.2f", monthlyEarnings));

        avgRecipesLabel.setText(String.format("%.1f", grafico.getMedia()));
        maxRecipesLabel.setText(String.valueOf(grafico.getNumeroMassimo()));
        minRecipesLabel.setText(String.valueOf(grafico.getNumeroMinimo()));
        totalRecipesLabel.setText(String.valueOf(totalRecipes));
    }

    private void updateChartsData(GraficoChef grafico, double monthlyEarnings) {
        ObservableList<PieChart.Data> sessionsData = sessionsChart.getData();
        if (sessionsData == null || sessionsData.size() != 2) {
            sessionsData = FXCollections.observableArrayList(
                new PieChart.Data("Sessioni Online", grafico.getNumerosessionitelematiche()),
                new PieChart.Data("Sessioni Pratiche", grafico.getNumeroSessioniInPresenza())
            );
            sessionsChart.setData(sessionsData);
        } else {
            sessionsData.get(0).setPieValue(grafico.getNumerosessionitelematiche());
            sessionsData.get(1).setPieValue(grafico.getNumeroSessioniInPresenza());
        }

        XYChart.Series<String, Number> recipesSeries;
        if (recipesChart.getData().isEmpty()) {
            recipesSeries = new XYChart.Series<>();
            recipesSeries.setName("Ricette per Sessione");
            recipesSeries.getData().add(new XYChart.Data<>("Minimo", grafico.getNumeroMinimo()));
            recipesSeries.getData().add(new XYChart.Data<>("Media", grafico.getMedia()));
            recipesSeries.getData().add(new XYChart.Data<>("Massimo", grafico.getNumeroMassimo()));
            recipesChart.getData().add(recipesSeries);
        } else {
            recipesSeries = recipesChart.getData().get(0);
            if (recipesSeries.getData().size() == 3) {
                recipesSeries.getData().get(0).setYValue(grafico.getNumeroMinimo());
                recipesSeries.getData().get(1).setYValue(grafico.getMedia());
                recipesSeries.getData().get(2).setYValue(grafico.getNumeroMassimo());
            }
        }

    }

    public void updateReport() {
        updateMonthYearLabel();
        loadReportData();
    }

    public void goToHomepage(Label chefNameLabel) {
        try {
            Stage stage = (Stage) chefNameLabel.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/homepagechef.fxml", "UninaFoodLab - Dashboard Chef");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToCreateCourse(Label chefNameLabel) {
        try {
            Stage stage = (Stage) chefNameLabel.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/createcourse.fxml", "UninaFoodLab - Crea Corso");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToAccountManagement(Label chefNameLabel) {
        try {
            Stage stage = (Stage) chefNameLabel.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/accountmanagementchef.fxml", "UninaFoodLab - Gestione Account Chef");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logout(Label chefNameLabel) {
        try {
            Stage stage = (Stage) chefNameLabel.getScene().getWindow();
            LogoutDialogBoundary dialogBoundary = SceneSwitcher.showLogoutDialog(stage);
            if (dialogBoundary.isConfirmed()) {
                SceneSwitcher.switchToLogin(stage, "/fxml/loginpage.fxml", "UninaFoodLab - Login");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}