package com.lms.controller;

import com.lms.model.User;
import com.lms.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NavigationController implements Initializable {
    
    @FXML private Button menuBooks;
    @FXML private Button menuMembers;
    @FXML private Button menuLoans;
    @FXML private Button menuReservations;
    @FXML private Button menuSeats;
    @FXML private Button menuActionLogs;
    @FXML private Button menuDashboard;
    @FXML private Button settingsButton;
    
    private DashboardController dashboardController;
    private Button activeMenuItem;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set dashboard as default active menu
        setActiveMenuItem(menuDashboard);
    }
    
    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }
    
    @FXML
    private void handleDashboard(ActionEvent event) {
        setActiveMenuItem(menuDashboard);
        if (dashboardController != null) {
            dashboardController.loadModule("dashboard");
        }
    }
    
    @FXML
    private void handleBooks(ActionEvent event) {
        setActiveMenuItem(menuBooks);
        if (dashboardController != null) {
            dashboardController.loadModule("books");
        }
    }
    
    @FXML
    private void handleMembers(ActionEvent event) {
        setActiveMenuItem(menuMembers);
        if (dashboardController != null) {
            dashboardController.loadModule("members");
        }
    }
    
    @FXML
    private void handleLoans(ActionEvent event) {
        setActiveMenuItem(menuLoans);
        if (dashboardController != null) {
            dashboardController.loadModule("loans");
        }
    }
    
    @FXML
    private void handleReservations(ActionEvent event) {
        setActiveMenuItem(menuReservations);
        if (dashboardController != null) {
            dashboardController.loadModule("reservations");
        }
    }
    
    @FXML
    private void handleSeats(ActionEvent event) {
        setActiveMenuItem(menuSeats);
        if (dashboardController != null) {
            dashboardController.loadModule("seats");
        }
    }
    
    @FXML
    private void handleActionLogs(ActionEvent event) {
        setActiveMenuItem(menuActionLogs);
        if (dashboardController != null) {
            dashboardController.loadModule("actionlogs");
        }
    }
    @FXML
    private void handleSettings(ActionEvent event) {
          setActiveMenuItem(settingsButton);
         if (dashboardController != null) {
            dashboardController.loadModule("settings");
        }
    }
    
    private void setActiveMenuItem(Button menuItem) {
        // Remove active class from previous item
        if (activeMenuItem != null) {
            activeMenuItem.getStyleClass().remove("menu-item-active");
        }
        
        // Add active class to new item
        menuItem.getStyleClass().add("menu-item-active");
        activeMenuItem = menuItem;
    }
    
    public void updateMenuForRole(String role) {
        // Hide/show menu items based on user role
        boolean isAdmin = "ADMIN".equals(role);
        boolean isLibrarian = "LIBRARIAN".equals(role);
        
        // All roles can see dashboard
        menuDashboard.setVisible(true);
        
        // Books, Members, Loans - visible to Admin and Librarian
        menuBooks.setVisible(isAdmin || isLibrarian);
        menuMembers.setVisible(isAdmin || isLibrarian);
        menuLoans.setVisible(isAdmin || isLibrarian);
        menuReservations.setVisible(isAdmin || isLibrarian);
        menuSeats.setVisible(isAdmin || isLibrarian);
        
        // Action logs - visible to Admin and Librarian
        menuActionLogs.setVisible(isAdmin || isLibrarian);
    }
}
