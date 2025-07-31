package com.lms.controller;

import com.lms.model.User;
import com.lms.dao.UserDAO;
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
    
    @FXML private Label welcomeLabel;
    @FXML private Label totalMembersLabel;
    @FXML private Label totalBooksLabel;
    @FXML private Label activeLoansLabel;
    @FXML private Label todayVisitsLabel;
    @FXML private Label lastLoginLabel;
    @FXML private PieChart statisticsChart;
    
    private User currentUser;
    private UserDAO userDAO;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userDAO = new UserDAO();
        setupStatisticsChart();
        loadDashboardStats();
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            updateWelcomeMessage();
            updateLastLoginInfo();
        }
    }
    
    private void updateWelcomeMessage() {
        LocalDateTime now = LocalDateTime.now();
        String timeOfDay;
        int hour = now.getHour();
        
        if (hour < 12) {
            timeOfDay = "Good Morning";
        } else if (hour < 17) {
            timeOfDay = "Good Afternoon";
        } else {
            timeOfDay = "Good Evening";
        }
        
        welcomeLabel.setText(timeOfDay + ", " + currentUser.getFirstName() + "!");
    }
    
    private void updateLastLoginInfo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
        lastLoginLabel.setText("Last login: " + LocalDateTime.now().format(formatter));
    }
    
    private void loadDashboardStats() {
        try {
            // Load user statistics
            List<User> allUsers = userDAO.findAll();
            List<User> members = userDAO.findByRole("MEMBER");
            List<User> activeUsers = userDAO.findActiveUsers();
            
            totalMembersLabel.setText(String.valueOf(members.size()));
            
            // Placeholder values for now (will be implemented in later phases)
            totalBooksLabel.setText("0");
            activeLoansLabel.setText("0");
            todayVisitsLabel.setText("0");
            
            // Update statistics chart
            updateStatisticsChart(allUsers);
            
        } catch (Exception e) {
            System.err.println("Error loading dashboard stats: " + e.getMessage());
            // Set default values
            totalMembersLabel.setText("0");
            totalBooksLabel.setText("0");
            activeLoansLabel.setText("0");
            todayVisitsLabel.setText("0");
        }
    }
    
    private void setupStatisticsChart() {
        statisticsChart.setTitle("Library Statistics");
        statisticsChart.setLegendVisible(true);
    }
    
    private void updateStatisticsChart(List<User> users) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        // Count users by role
        long adminCount = users.stream().filter(u -> "ADMIN".equals(u.getRole())).count();
        long librarianCount = users.stream().filter(u -> "LIBRARIAN".equals(u.getRole())).count();
        long memberCount = users.stream().filter(u -> "MEMBER".equals(u.getRole())).count();
        
        if (adminCount > 0) pieChartData.add(new PieChart.Data("Admins", adminCount));
        if (librarianCount > 0) pieChartData.add(new PieChart.Data("Librarians", librarianCount));
        if (memberCount > 0) pieChartData.add(new PieChart.Data("Members", memberCount));
        
        // Add placeholder data if no users
        if (pieChartData.isEmpty()) {
            pieChartData.add(new PieChart.Data("No Data", 1));
        }
        
        statisticsChart.setData(pieChartData);
    }
}