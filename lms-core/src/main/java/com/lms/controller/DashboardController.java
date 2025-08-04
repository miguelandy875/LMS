package com.lms.controller;

import com.lms.model.User;
import com.lms.util.DialogHelper;
import com.lms.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.lms.controller.modules.DashboardHomeController;
import com.lms.controller.modules.ModulePlaceholderController;

public class DashboardController implements Initializable {
    
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Label currentTimeLabel;
    @FXML private StackPane contentArea;
    @FXML private NavigationController sidebarController;
    
    private SessionManager sessionManager;
    private User currentUser;
    private String currentModule = "dashboard";
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sessionManager = SessionManager.getInstance();
        currentUser = sessionManager.getCurrentUser();
        
        if (currentUser != null) {
            initializeDashboard();
        } else {
            redirectToLogin();
        }
    }
    
    private void initializeDashboard() {
        // Update user info
        userNameLabel.setText(currentUser.getFullName());
        userRoleLabel.setText(currentUser.getRole());
        
        // Update current time
        updateCurrentTime();
        
        // Set up navigation controller
        if (sidebarController != null) {
            sidebarController.setDashboardController(this);
            sidebarController.updateMenuForRole(currentUser.getRole());
        }
        
        // Load default module (dashboard home)
        loadModule("dashboard");
        
        // Start time updater
        startTimeUpdater();
    }
    
    public void loadModule(String moduleName) {
        try {
            Parent moduleContent = null;
            currentModule = moduleName;
            
            switch (moduleName.toLowerCase()) {
                case "dashboard":
                    moduleContent = loadDashboardHome();
                    break;
                case "books":
                    moduleContent = FXMLLoader.load(getClass().getResource("/fxml/modules/books.fxml"));
                    break;
                case "members":
                    moduleContent = FXMLLoader.load(getClass().getResource("/fxml/modules/members.fxml"));
                    break;
                case "loans":
                    moduleContent = FXMLLoader.load(getClass().getResource("/fxml/modules/loans.fxml"));
                    break;
                case "reservations":
                    moduleContent = FXMLLoader.load(getClass().getResource("/fxml/modules/reservations.fxml"));
                    break;
                case "seats":
                    moduleContent = loadPlaceholder("Seat Management", "💺", 
                        "This module will allow you to:\n• Reserve library seats\n• Manage seat availability\n• Track seat usage\n• Configure seating areas\n• Generate occupancy reports");
                    break;
                case "actionlogs":
                    moduleContent = loadPlaceholder("Action Logs", "📊", 
                        "This module will allow you to:\n• View system activity\n• Track user actions\n• Generate audit reports\n• Filter by date/user\n• Export logs");
                    break;
                case "settings":
                    moduleContent = loadPlaceholder("Settings", "⚙", 
                        "This module will allow you to:\n• View settings\n• chqnge pqssword\n• view profile");
                    break;
                default:
                    moduleContent = loadDashboardHome();
                    break;
            }
            
            if (moduleContent != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(moduleContent);
            }
            
        } catch (IOException e) {
            DialogHelper.showError("Navigation Error", "Could not load module: " + e.getMessage());
        }
    }
    
    private Parent loadDashboardHome() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modules/dashboard_home.fxml"));
        Parent content = loader.load();
        
        // Initialize dashboard home controller if needed
        DashboardHomeController controller = loader.getController();
        if (controller != null) {
            controller.setCurrentUser(currentUser);
            controller.setDashboardController(this);
        }
        
        return content;
    }
    
    private Parent loadPlaceholder(String title, String icon, String description) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/modules/module_placeholder.fxml"));
        Parent content = loader.load();
        
        // Initialize placeholder controller
        ModulePlaceholderController controller = loader.getController();
        if (controller != null) {
            controller.setModuleInfo(title, icon, description);
        }
        
        return content;
    }
    
    private void updateCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm:ss");
        currentTimeLabel.setText(now.format(formatter));
    }
    
    private void startTimeUpdater() {
        // Update time every second
        Thread updater = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(this::updateCurrentTime);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        updater.setDaemon(true);
        updater.start();
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        if (DialogHelper.showConfirmation("Logout", "Are you sure you want to logout?")) {
            sessionManager.logout();
            redirectToLogin();
        }
    }
    
    @FXML
    private void handleExit(ActionEvent event) {
        if (DialogHelper.showConfirmation("Exit Application", "Are you sure you want to exit?")) {
            sessionManager.logout();
            System.exit(0);
        }
    }
    
    @FXML
    private void handleChangePassword(ActionEvent event) {
        showChangePasswordDialog();
    }
    
    @FXML
    private void handleProfile(ActionEvent event) {
        showUserProfile();
    }
    
    @FXML
    private void handleAbout(ActionEvent event) {
        showAboutDialog();
    }
    
    @FXML
    private void handleSettings(ActionEvent event) {
        DialogHelper.showSuccess("Settings", "Settings module will be implemented in future phases.");
    }
    
    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setTitle("Library Management System - Login");
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.centerOnScreen();
            
        } catch (IOException e) {
            DialogHelper.showError("Navigation Error", "Could not load login screen: " + e.getMessage());
        }
    }
    
    private void showChangePasswordDialog() {
        try {
            String oldPassword = DialogHelper.showInputDialog("Change Password", "Enter your current password:", "");
            if (oldPassword == null || oldPassword.trim().isEmpty()) {
                return;
            }
            
            String newPassword = DialogHelper.showInputDialog("Change Password", "Enter your new password:", "");
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return;
            }
            
            String confirmPassword = DialogHelper.showInputDialog("Change Password", "Confirm your new password:", "");
            if (confirmPassword == null || !confirmPassword.equals(newPassword)) {
                DialogHelper.showError("Password Mismatch", "New passwords do not match.");
                return;
            }
            
            if (newPassword.length() < 6) {
                DialogHelper.showError("Invalid Password", "Password must be at least 6 characters long.");
                return;
            }
            
            boolean success = sessionManager.changePassword(oldPassword, newPassword);
            if (success) {
                DialogHelper.showSuccess("Success", "Password changed successfully.");
            } else {
                DialogHelper.showError("Error", "Could not change password. Please check your current password.");
            }
            
        } catch (Exception e) {
            DialogHelper.showError("Error", "An error occurred while changing password: " + e.getMessage());
        }
    }
    
    private void showUserProfile() {
        if (currentUser != null) {
            String profile = String.format(
                "User Profile\n\n" +
                "Name: %s\n" +
                "Email: %s\n" +
                "Phone: %s\n" +
                "Gender: %s\n" +
                "Role: %s\n" +
                "Status: %s\n" +
                "Created: %s",
                currentUser.getFullName(),
                currentUser.getEmail(),
                currentUser.getPhone(),
                currentUser.getSex(),
                currentUser.getRole(),
                currentUser.getStatus(),
                currentUser.getCreatedAt() != null ? currentUser.getCreatedAt().toString() : "N/A"
            );
            
            DialogHelper.showSuccess("User Profile", profile);
        }
    }
    
    private void showAboutDialog() {
        String aboutText = 
            "Library Management System\n" +
            "Version 1.0.0\n\n" +
            "A comprehensive library management solution built with JavaFX.\n\n" +
            "Current Module: " + currentModule.toUpperCase() + "\n\n" +
            "Phase 1: Authentication & Foundation - ✅ Complete\n" +
            "Phase 2: Dashboard & Navigation - ✅ Complete\n" +
            "Next: Phase 3 - User Management";
        
        DialogHelper.showSuccess("About LMS", aboutText);
    }
}