package com.progetto.boundary;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.event.ActionEvent;
import com.progetto.controller.MonthlyReportController;
import com.progetto.Entity.EntityDto.Chef;
import com.progetto.boundary.LogoutDialogBoundary;
import com.progetto.utils.SceneSwitcher;
import javafx.stage.Stage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import com.progetto.utils.ImageClipUtils;

public class MonthlyReportBoundary {

    @FXML private Label chefNameLabel;
    @FXML private ImageView chefProfileImage;
    @FXML private Button viewMyCoursesBtn;
    @FXML private Button createCourseBtn;
    @FXML private Button monthlyReportBtn;
    @FXML private Button accountManagementBtn;
    @FXML private Button logoutBtn;

    @FXML private Label monthYearLabel;
    @FXML private ComboBox<String> monthComboBox;
    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private Button updateButton;
    
    @FXML private Label totalCoursesLabel;
    @FXML private Label onlineSessionsLabel;
    @FXML private Label practicalSessionsLabel;
    @FXML private Label monthlyEarningsLabel;
    
    @FXML private PieChart sessionsChart;
    @FXML private BarChart<String, Number> recipesChart;
    @FXML private CategoryAxis recipesXAxis;
    @FXML private NumberAxis recipesYAxis;
    
    @FXML private Label avgRecipesLabel;
    @FXML private Label maxRecipesLabel;
    @FXML private Label minRecipesLabel;
    @FXML private Label totalRecipesLabel;
    
    @FXML private LineChart<String, Number> earningsChart;
    @FXML private CategoryAxis earningsXAxis;
    @FXML private NumberAxis earningsYAxis;

    private MonthlyReportController controller;
    private Chef chef;

    @FXML
    public void initialize() {
        chef = Chef.loggedUser;
        setupChefProfile();
        controller = new MonthlyReportController(
            monthYearLabel, monthComboBox, yearComboBox,
            totalCoursesLabel, onlineSessionsLabel, practicalSessionsLabel, monthlyEarningsLabel,
            sessionsChart, recipesChart, avgRecipesLabel, maxRecipesLabel, minRecipesLabel, totalRecipesLabel,
            earningsChart,
            chef
        );
        controller.initialize();
    }

   
    @FXML
    private void goToHomepage(ActionEvent event) {
        try {
            Stage stage = (Stage) chefNameLabel.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/homepagechef.fxml", "UninaFoodLab - Dashboard Chef");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToCreateCourse(ActionEvent event) {
        try {
            Stage stage = (Stage) chefNameLabel.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/createcourse.fxml", "UninaFoodLab - Crea Corso");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToAccountManagement(ActionEvent event) {
        try {
            Stage stage = (Stage) chefNameLabel.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/fxml/accountmanagementchef.fxml", "UninaFoodLab - Gestione Account Chef");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void LogoutClick(ActionEvent event) {
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

    @FXML
    private void updateReport(ActionEvent event) {
        controller.updateReport();
    }

    public void setupChefProfile() {
        if (chef != null) {
            chefNameLabel.setText(chef.getNome() + " " + chef.getCognome());
            String propic = chef.getUrl_Propic();
            if (propic != null && !propic.isEmpty()) {
                File imgFile = new File("src/main/resources/" + propic);
                if (imgFile.exists()) {
                    Image img = new Image(imgFile.toURI().toString(), 80, 80, true, true);
                    chefProfileImage.setImage(img);
                    ImageClipUtils.setCircularClip(chefProfileImage, 40);
                }
            }
        }
    }
}