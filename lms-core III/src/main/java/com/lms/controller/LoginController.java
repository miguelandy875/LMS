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
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;
    @FXML private Button loginButton;
    @FXML private ProgressIndicator loadingIndicator;
    
    // Add references to the main containers for glass effects
    @FXML private VBox leftPanel;   // Add fx:id="leftPanel" to your left VBox in FXML
    @FXML private VBox loginFormContainer; // Add fx:id="loginFormContainer" to your login form VBox

    private final UserService userService = new UserServiceImpl();

    @FXML
    private void initialize() {
        // Hide loading indicator initially
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(false);
        }
        
        // Clear status label
        statusLabel.setText("");
        
        // Apply glass effects
        setupGlassEffects();
    }

    private void setupGlassEffects() {
        // Apply glass effect to left panel
        setupLeftPanelGlass();
        
        // Apply glass effect to login form
        setupLoginFormGlass();
        
        // Apply glass effect to buttons
        setupButtonGlassEffects();
        
        // Apply glass effect to input fields
        setupInputFieldGlassEffects();
    }

    private void setupLeftPanelGlass() {
        if (leftPanel != null) {
            // Create glass background - semi-transparent white
            BackgroundFill glassFill = new BackgroundFill(
                Color.rgb(255, 255, 255, 0.1), // 10% opacity white
                CornerRadii.EMPTY, 
                Insets.EMPTY
            );
            leftPanel.setBackground(new Background(glassFill));
            
            // Apply subtle blur
            GaussianBlur glassBlur = new GaussianBlur(2.5);
            leftPanel.setEffect(glassBlur);
            
            // Add glass border
            leftPanel.setStyle(leftPanel.getStyle() + 
                "; -fx-border-color: rgba(255, 255, 255, 0.2);" +
                "; -fx-border-width: 0 1px 0 0;"); // Right border only
        }
    }

    private void setupLoginFormGlass() {
        if (loginFormContainer != null) {
            // Enhanced glass effect for login form
            BackgroundFill formGlass = new BackgroundFill(
                Color.rgb(255, 255, 255, 0.95), // Almost opaque white with slight transparency
                new CornerRadii(15), 
                Insets.EMPTY
            );
            loginFormContainer.setBackground(new Background(formGlass));
            
            // Enhanced drop shadow for depth
            DropShadow formShadow = new DropShadow();
            formShadow.setColor(Color.rgb(0, 0, 0, 0.15));
            formShadow.setRadius(25);
            formShadow.setSpread(0.3);
            formShadow.setOffsetY(10);
            
            loginFormContainer.setEffect(formShadow);
            
            // Add subtle glass border
            loginFormContainer.setStyle(loginFormContainer.getStyle() + 
                "; -fx-border-color: rgba(255, 255, 255, 0.8);" +
                "; -fx-border-width: 1px;" +
                "; -fx-border-radius: 15px;");
        }
    }

    private void setupButtonGlassEffects() {
        // Glass effect for login button
        setupLoginButtonGlass();
        
        // Glass effect for other buttons (forgot password, help)
        setupLinkButtonsGlass();
    }

    private void setupLoginButtonGlass() {
        if (loginButton != null) {
            // Create subtle blur for glass effect
            GaussianBlur buttonBlur = new GaussianBlur(1.0);
            
            // Mouse enter effect - enhanced glass with dark theme
            loginButton.setOnMouseEntered(e -> {
                BackgroundFill hoverGlass = new BackgroundFill(
                    Color.rgb(118, 75, 162, 0.3), // Purple from gradient
                    new CornerRadii(8), 
                    Insets.EMPTY
                );
                
                // Apply both background and effect
                loginButton.setEffect(buttonBlur);
                
                // Add glow effect with theme colors
                DropShadow glow = new DropShadow();
                glow.setColor(Color.rgb(102, 126, 234, 0.6)); // Blue from gradient
                glow.setRadius(15);
                glow.setSpread(0.3);
                
                loginButton.setEffect(glow);
                
                // Scale effect
                loginButton.setScaleX(1.05);
                loginButton.setScaleY(1.05);
            });
            
            // Mouse exit effect - reset
            loginButton.setOnMouseExited(e -> {
                loginButton.setEffect(null);
                loginButton.setScaleX(1.0);
                loginButton.setScaleY(1.0);
            });
        }
    }

    private void setupLinkButtonsGlass() {
        // Find and apply glass effects to link buttons
        // This would apply to "Forgot Password?" and "Need Help?" buttons
        // You can get references to these buttons and apply similar effects
        
        // Example for any button with transparent background:
        /*
        if (forgotPasswordButton != null) {
            forgotPasswordButton.setOnMouseEntered(e -> {
                BackgroundFill linkGlass = new BackgroundFill(
                    Color.rgb(102, 126, 234, 0.1),
                    new CornerRadii(5), 
                    Insets.EMPTY
                );
                forgotPasswordButton.setBackground(new Background(linkGlass));
            });
            
            forgotPasswordButton.setOnMouseExited(e -> {
                forgotPasswordButton.setBackground(Background.EMPTY);
            });
        }
        */
    }

    private void setupInputFieldGlassEffects() {
        // Glass effect for email field
        setupFieldGlass(emailField);
        
        // Glass effect for password field
        setupFieldGlass(passwordField);
    }

    private void setupFieldGlass(Control field) {
        if (field != null) {
            // Focus effects for glass
            field.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    // Focused - glass effect with dark theme
                    BackgroundFill focusGlass = new BackgroundFill(
                        Color.rgb(45, 55, 72, 0.9), // Darker background on focus
                        new CornerRadii(8), 
                        Insets.EMPTY
                    );
                    field.setBackground(new Background(focusGlass));
                    
                    // Add glow border with theme colors
                    field.setStyle(field.getStyle() + 
                        "; -fx-border-color: rgba(102, 126, 234, 0.8);" +
                        "; -fx-border-width: 2px;");
                        
                    // Subtle blur
                    GaussianBlur fieldBlur = new GaussianBlur(0.5);
                    field.setEffect(fieldBlur);
                    
                } else {
                    // Not focused - reset
                    field.setBackground(Background.EMPTY);
                    field.setEffect(null);
                    
                    // Reset border
                    String style = field.getStyle();
                    style = style.replaceAll("; -fx-border-color: rgba\\(102, 126, 234, 0\\.6\\);", "");
                    style = style.replaceAll("; -fx-border-width: 2px;", "");
                    field.setStyle(style);
                }
            });
        }
    }

    // Your existing methods remain the same...
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot Password");
        alert.setHeaderText(null);
        alert.setContentText("Please contact your system administrator to reset your password.");
        alert.showAndWait();
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration");
        alert.setHeaderText(null);
        alert.setContentText("Please contact your librarian to create a new account.");
        alert.showAndWait();
    }
}