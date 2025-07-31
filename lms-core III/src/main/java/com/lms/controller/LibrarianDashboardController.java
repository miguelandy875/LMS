package com.lms.controller;

import com.lms.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LibrarianDashboardController implements Initializable {

    @FXML private Label welcomeLabel;
    @FXML private Label librarianNameLabel;
    @FXML private BorderPane mainContentPane;
    @FXML private Button dashboardHomeButton;
    @FXML private Button booksButton;
    @FXML private Button usersButton;
    @FXML private Button loansButton;
    @FXML private Button seatsButton;
    @FXML private Button reportsButton;
    @FXML private Button settingsButton;
    @FXML private Button logoutButton;

    private User loggedInLibrarian;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize any default settings here
        loadDashboardHomeView();
        
        // Apply glass effects to the dashboard
        setupGlassEffects();
    }

    /**
     * Set up glass effects for the dashboard
     */
    private void setupGlassEffects() {
        // Apply glass effects to navigation buttons
        setupNavigationGlassEffects();
        
        // Apply glass effect to logout button
        setupLogoutButtonGlass();
    }

    /**
     * Setup glass effects for navigation buttons
     */
    private void setupNavigationGlassEffects() {
        // Apply glass effect to main navigation buttons (no blur)
        applyGlassToButton(dashboardHomeButton);
        applyGlassToButton(booksButton);
        applyGlassToButton(usersButton);
        applyGlassToButton(loansButton);
        applyGlassToButton(seatsButton);
        applyGlassToButton(reportsButton);
        applyGlassToButton(settingsButton);
    }

    /**
     * Apply glass effect to individual navigation buttons (without blur)
     */
    private void applyGlassToButton(Button button) {
        if (button != null) {
            // Create hover effects
            button.setOnMouseEntered(e -> {
                // Glass hover effect with complementary colors
                BackgroundFill glassHover = new BackgroundFill(
                    Color.rgb(102, 126, 234, 0.2), // Using the gradient blue with transparency
                    new CornerRadii(8), 
                    Insets.EMPTY
                );
                button.setBackground(new Background(glassHover));
                
                // Update text color and add glass border
                button.setStyle(button.getStyle() + 
                    "; -fx-text-fill: white;" +
                    "; -fx-font-weight: bold;" +
                    "; -fx-border-color: rgba(102, 126, 234, 0.5);" +
                    "; -fx-border-width: 1px;" +
                    "; -fx-border-radius: 8px;");
                
                // Subtle scale effect
                button.setScaleX(1.02);
                button.setScaleY(1.02);
            });
            
            button.setOnMouseExited(e -> {
                // Reset to original style
                button.setBackground(Background.EMPTY);
                
                // Remove added styles
                String style = button.getStyle();
                style = style.replaceAll("; -fx-text-fill: white;", "");
                style = style.replaceAll("; -fx-font-weight: bold;", "");
                style = style.replaceAll("; -fx-border-color: rgba\\(102, 126, 234, 0\\.5\\);", "");
                style = style.replaceAll("; -fx-border-width: 1px;", "");
                style = style.replaceAll("; -fx-border-radius: 8px;", "");
                button.setStyle(style);
                
                // Reset scale
                button.setScaleX(1.0);
                button.setScaleY(1.0);
            });
        }
    }

    /**
     * Setup enhanced glass effect for logout button
     */
    private void setupLogoutButtonGlass() {
        if (logoutButton != null) {
            // Enhanced glass effect for logout button
            GaussianBlur logoutBlur = new GaussianBlur(2.0);
            
            logoutButton.setOnMouseEntered(e -> {
                BackgroundFill glassEffect = new BackgroundFill(
                    Color.rgb(118, 75, 162, 0.3), // Using the gradient purple with transparency
                    new CornerRadii(20), 
                    Insets.EMPTY
                );
                logoutButton.setBackground(new Background(glassEffect));
                logoutButton.setEffect(logoutBlur);
                
                // Add glow effect with matching colors
                DropShadow glow = new DropShadow();
                glow.setColor(Color.rgb(118, 75, 162, 0.6)); // Purple glow
                glow.setRadius(10);
                glow.setSpread(0.3);
                logoutButton.setEffect(glow);
                
                // Scale effect
                logoutButton.setScaleX(1.05);
                logoutButton.setScaleY(1.05);
            });
            
            logoutButton.setOnMouseExited(e -> {
                // Reset to original semi-transparent style
                logoutButton.setBackground(Background.EMPTY);
                logoutButton.setEffect(null);
                
                // Reset scale
                logoutButton.setScaleX(1.0);
                logoutButton.setScaleY(1.0);
            });
        }
    }

    /**
     * Set the logged-in librarian and update UI accordingly
     */
    public void setLibrarian(User librarian) {
        this.loggedInLibrarian = librarian;
        updateWelcomeMessage();
    }

    private void updateWelcomeMessage() {
        if (loggedInLibrarian != null) {
            librarianNameLabel.setText(loggedInLibrarian.getUserFname() + " " + loggedInLibrarian.getUserLname());
            welcomeLabel.setText("Welcome back!");
        }
    }

    /**
     * Internal method to load the default dashboard home view (no params)
     */
    private void loadDashboardHomeView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lms/view/dashboard-home.fxml"));
            Parent homeView = loader.load();
            mainContentPane.setCenter(homeView);
        } catch (IOException e) {
            showError("Failed to load dashboard home view");
            e.printStackTrace();
        }
    }

    /**
     * Event handler for Dashboard Home button (with @FXML and ActionEvent)
     */
    @FXML
    private void loadDashboardHome(ActionEvent event) {
        loadDashboardHomeView();
    }

    /**
     * Handle Books Management
     */
    @FXML
    private void handleBooksManagement(ActionEvent event) {
        loadView("/com/lms/view/books-management.fxml", "Books Management");
    }

    /**
     * Handle Users Management
     */
    @FXML
    private void handleUsersManagement(ActionEvent event) {
        loadView("/com/lms/view/users-management.fxml", "Users Management");
    }

    /**
     * Handle Loans Management
     */
    @FXML
    private void handleLoansManagement(ActionEvent event) {
        loadView("/com/lms/view/loans-management.fxml", "Loans Management");
    }

    /**
     * Handle Seats Management
     */
    @FXML
    private void handleSeatsManagement(ActionEvent event) {
        loadView("/com/lms/view/seats-management.fxml", "Seats Management");
    }

    /**
     * Handle Reports
     */
    @FXML
    private void handleReports(ActionEvent event) {
        loadView("/com/lms/view/reports.fxml", "Reports");
    }

    /**
     * Handle Settings
     */
    @FXML
    private void handleSettings(ActionEvent event) {
        loadView("/com/lms/view/settings.fxml", "Settings");
    }

    /**
     * Handle Logout
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Load login screen
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lms/view/login.fxml"));
                    Parent loginRoot = loader.load();

                    // Get current stage and switch scene
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(loginRoot));
                    stage.setTitle("Library Management System - Login");
                    stage.show();

                } catch (IOException e) {
                    showError("Failed to load login screen");
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Generic method to load views in the main content pane
     */
    private void loadView(String fxmlPath, String viewName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
            // If the loaded controller needs the librarian info, you can pass it here
            Object controller = loader.getController();
            if (controller instanceof LibrarianAwareController) {
                ((LibrarianAwareController) controller).setLibrarian(loggedInLibrarian);
            }
            
            mainContentPane.setCenter(view);
            
        } catch (IOException e) {
            showError("Failed to load " + viewName + " view");
            e.printStackTrace();
        }
    }

    /**
     * Show error message to user
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show information message to user
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Get the logged-in librarian
     */
    public User getLoggedInLibrarian() {
        return loggedInLibrarian;
    }
}

/**
 * Interface for controllers that need access to librarian information
 */
interface LibrarianAwareController {
    void setLibrarian(User librarian);
}