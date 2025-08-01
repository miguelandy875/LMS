package com.lms.controller;

import com.lms.model.User;
import com.lms.util.DialogHelper;
import com.lms.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button exitButton;
    @FXML private Label statusLabel;
    @FXML private ProgressIndicator loadingIndicator;
    
    private SessionManager sessionManager;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sessionManager = SessionManager.getInstance();
        loadingIndicator.setVisible(false);
        
        // Set up enter key handling
        passwordField.setOnAction(this::handleLogin);
        
        // Add some validation styling
        emailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validateEmail();
        });
    }
    
    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        if (email.isEmpty() || password.isEmpty()) {
            DialogHelper.showError("Validation Error", "Please enter both email and password.");
            return;
        }
        
        if (!isValidEmail(email)) {
            DialogHelper.showError("Validation Error", "Please enter a valid email address.");
            return;
        }
        
        performLogin(email, password);
    }
    
    private void performLogin(String email, String password) {
        loadingIndicator.setVisible(true);
        loginButton.setDisable(true);
       
        
        // Simulate async operation (in real app, you might want to use Task)
        new Thread(() -> {
            try {
                Thread.sleep(500); // Simulate network delay
                
                User user = sessionManager.login(email, password);
                
                javafx.application.Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    loginButton.setDisable(false);
                    
                    if (user != null) {
                        statusLabel.setText("Login successful!");
                        navigateToDashboard(user);
                    } else {
                        statusLabel.setText("Invalid credentials or inactive account.");
                        DialogHelper.showError("Login Failed", 
                            "Invalid email/password combination or your account is inactive. Please contact administrator.");
                        clearFields();
                    }
                });
            } catch (InterruptedException e) {
                javafx.application.Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    loginButton.setDisable(false);
                    statusLabel.setText("Login failed.");
                });
            }
        }).start();
    }
    
private void navigateToDashboard(User user) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dash_v1.fxml"));
        Parent root = loader.load();

        // Get the current stage
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setTitle("LMS - " + user.getFullName() + " (" + user.getRole() + ")");
        stage.setScene(new Scene(root));
        stage.setMaximized(true);

    } catch (Exception e) {
        // Show error dialog with stack trace
        StringBuilder errorDetails = new StringBuilder();
        errorDetails.append(e.toString()).append("\n");
        for (StackTraceElement ste : e.getStackTrace()) {
            errorDetails.append(ste.toString()).append("\n");
        }
        DialogHelper.showError("Navigation Error", "Could not load dashboard:\n" + errorDetails.toString());
        e.printStackTrace(); // Also print to console
    }
}
    
    @FXML
    private void handleExit(ActionEvent event) {
        if (DialogHelper.showConfirmation("Exit Application", "Are you sure you want to exit?")) {
            System.exit(0);
        }
    }
    
    private void validateEmail() {
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !isValidEmail(email)) {
            emailField.setStyle("-fx-border-color: red;");
        } else {
            emailField.setStyle("");
        }
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }
    
    private void clearFields() {
        passwordField.clear();
        statusLabel.setText("");
    }
}
