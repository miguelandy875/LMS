package com.lms.controller.modules;

import com.lms.model.User;
import com.lms.dao.UserDAO;
import com.lms.util.SessionManager;
import javafx.scene.control.Button;
import com.lms.util.DialogHelper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.chart.PieChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardHomeController implements Initializable {
    private com.lms.controller.DashboardController dashboardController;
    
    @FXML private Label welcomeLabel;
    @FXML private Label dateTimeLabel;
    @FXML private Label totalBooksLabel;
    @FXML private Label availableBooksLabel;
    @FXML private Label issuedBooksLabel;
    @FXML private Label totalMembersLabel;
    @FXML private Label activeLoansLabel;
    @FXML private Label overdueLoansLabel;
    @FXML private Label reservationsLabel;
    @FXML private Label occupiedSeatsLabel;
    @FXML private PieChart statisticsChart;
   @FXML private Button addMemberBtn;
   @FXML private Button addBookBtn;
   @FXML private Button processLoanBtn;
   @FXML private Button makeReservationBtn;
   @FXML private Button viewReportsBtn;
    
    private User currentUser;
    private UserDAO userDAO;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentUser = SessionManager.getInstance().getCurrentUser();
        userDAO = new UserDAO();
        
        if (currentUser != null) {
            setupWelcomeMessage();
            updateDateTime();
            loadStatistics();
            setupChart();
        }
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            setupWelcomeMessage();
        }
    }

    public void setDashboardController(com.lms.controller.DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }
    @FXML
    private void handleAddMember() {
        if (dashboardController != null) {
            dashboardController.loadModule("members");
        } else {
            System.out.println("Add Member clicked");
        }
    }

    @FXML
    private void handleAddBook() {
        if (dashboardController != null) {
            dashboardController.loadModule("books");
        } else {
            DialogHelper.showInfo("Module Under Development", "Book Management module is under development.");
        }
    }

    @FXML
    private void handleProcessLoan() {
        if (dashboardController != null) {
            dashboardController.loadModule("loans");
        } else {
            DialogHelper.showInfo("Module Under Development", "Loan Management module is under development.");
        }
    }

    @FXML
    private void handleMakeReservation() {
        if (dashboardController != null) {
            dashboardController.loadModule("reservations");
        } else {
            DialogHelper.showInfo("Module Under Development", "Reservation module is under development.");
        }
    }

    @FXML
    private void handleViewReports() {
        DialogHelper.showInfo("Module Under Development", "Reports module is under development.");
    }
    
    private void setupWelcomeMessage() {
        String greeting = getGreeting();
        if (welcomeLabel != null) {
            welcomeLabel.setText(greeting + ", " + currentUser.getFirstName() + "!");
        }
    }
    
    private String getGreeting() {
        int hour = LocalDateTime.now().getHour();
        if (hour < 12) {
            return "Good Morning";
        } else if (hour < 17) {
            return "Good Afternoon";
        } else {
            return "Good Evening";
        }
    }
    
    private void updateDateTime() {
        if (dateTimeLabel != null) {
            LocalDateTime now = LocalDateTime.now();
            String formattedDateTime = now.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"));
            dateTimeLabel.setText(formattedDateTime);
        }
    }
    
    private void loadStatistics() {
        try {
            // Get real member statistics
            List<User> allUsers = userDAO.findAll();
            long totalMembers = allUsers.size();
            long activeMembers = allUsers.stream().filter(u -> "ACTIVE".equals(u.getStatus())).count();
            
            // TODO: These will be replaced with actual database queries in later phases
            // For now, using sample data for books and other entities
            
            if (totalBooksLabel != null) totalBooksLabel.setText("1,245");
            if (availableBooksLabel != null) availableBooksLabel.setText("1,089");
            if (issuedBooksLabel != null) issuedBooksLabel.setText("156");
            if (totalMembersLabel != null) totalMembersLabel.setText(String.valueOf(totalMembers));
            if (activeLoansLabel != null) activeLoansLabel.setText("156");
            if (overdueLoansLabel != null) overdueLoansLabel.setText("12");
            if (reservationsLabel != null) reservationsLabel.setText("23");
            if (occupiedSeatsLabel != null) occupiedSeatsLabel.setText("45/80");
            
        } catch (Exception e) {
            System.err.println("Error loading statistics: " + e.getMessage());
            // Set default values on error
            if (totalMembersLabel != null) totalMembersLabel.setText("0");
        }
    }
    
    private void setupChart() {
        if (statisticsChart != null) {
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Available Books", 1089),
                new PieChart.Data("Issued Books", 156),
                new PieChart.Data("Reserved Books", 23)
            );
            
            statisticsChart.setData(pieChartData);
            statisticsChart.setTitle("Book Status Distribution");
            
            // Apply custom colors
        
            statisticsChart.setLegendVisible(true);

            statisticsChart.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #c2d3ea;"
            );
            
            // Add listener to apply styles after chart is rendered
            statisticsChart.dataProperty().addListener((obs, oldData, newData) -> {
                if (newData != null && newData.size() >= 3) {
                    newData.get(0).getNode().setStyle("-fx-pie-color: #4CAF50;");
                    newData.get(1).getNode().setStyle("-fx-pie-color: #FF9800;");
                    newData.get(2).getNode().setStyle("-fx-pie-color: #2196F3;");
                }
            });
        }
    }
}
