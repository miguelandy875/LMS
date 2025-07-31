package com.lms.controller;

import com.lms.model.User;
import com.lms.service.UserService;
import com.lms.service.impl.UserServiceImpl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;
    @FXML private Button loginButton;
    @FXML private ProgressIndicator loadingIndicator;

    private final UserService userService = new UserServiceImpl();

    @FXML
    private void initialize() {
        // Hide loading indicator initially
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(false);
        }
        
        // Clear status label
        statusLabel.setText("");
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Input validation
        if (email.isEmpty()) {
            statusLabel.setText("Please enter your email address.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (password.isEmpty()) {
            statusLabel.setText("Please enter your password.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Show loading indicator
        showLoading(true);
        statusLabel.setText("Authenticating...");
        statusLabel.setStyle("-fx-text-fill: blue;");

        try {
            // Attempt login
            User user = userService.login(email, password);

            if (user != null) {
                // Check user role and redirect accordingly
                handleSuccessfulLogin(user, event);
            } else {
                showLoading(false);
                statusLabel.setText("Invalid email or password. Please try again.");
                statusLabel.setStyle("-fx-text-fill: red;");
                clearPasswordField();
            }

        } catch (Exception e) {
            showLoading(false);
            statusLabel.setText("Login failed. Please try again later.");
            statusLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    private void handleSuccessfulLogin(User user, ActionEvent event) {
        try {
            String userRole = user.getUserRole().toLowerCase();
            
            switch (userRole) {
                case "librarian":
                case "admin":
                    loadLibrarianDashboard(user, event);
                    break;
                case "member":
                    loadMemberDashboard(user, event);
                    break;
                default:
                    showLoading(false);
                    statusLabel.setText("Access denied. Invalid user role.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    break;
            }
            
        } catch (IOException e) {
            showLoading(false);
            statusLabel.setText("Failed to load dashboard. Please try again.");
            statusLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    private void loadLibrarianDashboard(User user, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lms/view/librarian-dashboard.fxml"));
        Parent dashboardRoot = loader.load();

        // Get the controller and pass the logged-in user
        LibrarianDashboardController controller = loader.getController();
        controller.setLibrarian(user);

        // Switch scene
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(dashboardRoot));
        stage.setTitle("Library Management System - Dashboard");
        stage.setMaximized(true); // Maximize for better experience
        stage.show();
    }

    private void loadMemberDashboard(User user, ActionEvent event) throws IOException {
        // TODO: Implement member dashboard
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lms/view/member-dashboard.fxml"));
        Parent dashboardRoot = loader.load();

        // Get the controller and pass the logged-in user
        // MemberDashboardController controller = loader.getController();
        // controller.setMember(user);

        // Switch scene
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(dashboardRoot));
        stage.setTitle("Library Management System - Member Portal");
        stage.show();
    }

    private void showLoading(boolean show) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(show);
        }
        loginButton.setDisable(show);
    }

    private void clearPasswordField() {
        passwordField.clear();
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        // TODO: Implement forgot password functionality
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot Password");
        alert.setHeaderText(null);
        alert.setContentText("Please contact your system administrator to reset your password.");
        alert.showAndWait();
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        // TODO: Implement user registration (if applicable)
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration");
        alert.setHeaderText(null);
        alert.setContentText("Please contact your librarian to create a new account.");
        alert.showAndWait();
    }
}