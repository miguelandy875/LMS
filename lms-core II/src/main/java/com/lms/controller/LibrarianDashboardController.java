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
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LibrarianDashboardController implements Initializable {

    @FXML private Label welcomeLabel;
    @FXML private Label librarianNameLabel;
    @FXML private BorderPane mainContentPane;
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
