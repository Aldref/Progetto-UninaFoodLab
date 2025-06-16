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

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

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
    
    private final LineChart<String, Number> earningsChart;

    // Dati fittizi - facilmente sostituibili con dati dal DB
    private static class ReportData {
        int totalCourses = 12;
        int onlineSessions = 48;
        int practicalSessions = 24;
        double monthlyEarnings = 4580.0;
        double avgRecipes = 3.2;
        int maxRecipes = 5;
        int minRecipes = 2;
        int totalRecipes = 76;
    }

    public MonthlyReportController(
            Label monthYearLabel, ComboBox<String> monthComboBox, ComboBox<Integer> yearComboBox,
            Label totalCoursesLabel, Label onlineSessionsLabel, Label practicalSessionsLabel, Label monthlyEarningsLabel,
            PieChart sessionsChart, BarChart<String, Number> recipesChart,
            Label avgRecipesLabel, Label maxRecipesLabel, Label minRecipesLabel, Label totalRecipesLabel,
            LineChart<String, Number> earningsChart) {
        
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
        this.earningsChart = earningsChart;
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
        // TODO query del database
        ReportData data = generateSampleData();
        
        updateStatistics(data);
        updateChartsData(data);
    }

    private ReportData generateSampleData() {
        
        ReportData data = new ReportData();
        
        String selectedMonth = monthComboBox.getValue();
        if (selectedMonth != null) {
            
            if (selectedMonth.equals("Dicembre") || selectedMonth.equals("Gennaio")) {
                data.totalCourses += 3; 
                data.monthlyEarnings += 1200;
            } else if (selectedMonth.equals("Luglio") || selectedMonth.equals("Agosto")) {
                data.totalCourses -= 2; 
                data.monthlyEarnings -= 800;
            }
        }
        
        return data;
    }

    private void updateStatistics(ReportData data) {
        totalCoursesLabel.setText(String.valueOf(data.totalCourses));
        onlineSessionsLabel.setText(String.valueOf(data.onlineSessions));
        practicalSessionsLabel.setText(String.valueOf(data.practicalSessions));
        monthlyEarningsLabel.setText(String.format("€ %.2f", data.monthlyEarnings));
        
        avgRecipesLabel.setText(String.format("%.1f", data.avgRecipes));
        maxRecipesLabel.setText(String.valueOf(data.maxRecipes));
        minRecipesLabel.setText(String.valueOf(data.minRecipes));
        totalRecipesLabel.setText(String.valueOf(data.totalRecipes));
    }

    private void updateChartsData(ReportData data) {
        
        ObservableList<PieChart.Data> sessionsData = FXCollections.observableArrayList(
            new PieChart.Data("Sessioni Online", data.onlineSessions),
            new PieChart.Data("Sessioni Pratiche", data.practicalSessions)
        );
        sessionsChart.setData(sessionsData);
        sessionsChart.setTitle("");

        
        XYChart.Series<String, Number> recipesSeries = new XYChart.Series<>();
        recipesSeries.setName("Ricette per Sessione");
        recipesSeries.getData().add(new XYChart.Data<>("Minimo", data.minRecipes));
        recipesSeries.getData().add(new XYChart.Data<>("Media", data.avgRecipes));
        recipesSeries.getData().add(new XYChart.Data<>("Massimo", data.maxRecipes));
        
        recipesChart.getData().clear();
        recipesChart.getData().add(recipesSeries);
        recipesChart.setTitle("");
        recipesChart.setLegendVisible(false);

        
        XYChart.Series<String, Number> earningsSeries = new XYChart.Series<>();
        earningsSeries.setName("Guadagni Mensili");
        
        String[] lastSixMonths = {"Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"};
        double[] earnings = {3200, 2800, 4100, 4500, 4300, data.monthlyEarnings};
        
        for (int i = 0; i < lastSixMonths.length; i++) {
            earningsSeries.getData().add(new XYChart.Data<>(lastSixMonths[i], earnings[i]));
        }
        
        earningsChart.getData().clear();
        earningsChart.getData().add(earningsSeries);
        earningsChart.setTitle("");
        earningsChart.setLegendVisible(false);
    }

    public void updateReport() {
        updateMonthYearLabel();
        loadReportData();
    }

}