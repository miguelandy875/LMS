package com.lms.controller;

import com.lms.utils.NavigationHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

public class LibrarianDashboardController {
    @FXML private VBox sidebar;
    @FXML private Button toggleSidebarButton;
    @FXML private Label welcomeLabel;
    @FXML private StackPane contentPane;
    private boolean isSidebarVisible = true;

    @FXML
    private void initialize() {
        // Assume logged-in user's name is passed (e.g., from LoginController)
        welcomeLabel.setText("Welcome, " + System.getProperty("user.name", "Librarian"));
        loadBooksView(); // Load Books view by default
    }

    @FXML
    private void toggleSidebar() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebar);
        if (isSidebarVisible) {
            transition.setToX(-sidebar.getWidth());
            toggleSidebarButton.setText("▶");
        } else {
            transition.setToX(0);
            toggleSidebarButton.setText("☰");
        }
        transition.play();
        isSidebarVisible = !isSidebarVisible;
    }

    @FXML
    private void loadBooksView() {
        NavigationHelper.loadView(contentPane, "/com/lms/view/books.fxml");
    }

    @FXML
    private void loadMembersView() {
        NavigationHelper.loadView(contentPane, "/com/lms/view/members.fxml");
    }

    @FXML
    private void loadLoansView() {
        NavigationHelper.loadView(contentPane, "/com/lms/view/loans.fxml");
    }

    @FXML
    private void loadReservationsView() {
        NavigationHelper.loadView(contentPane, "/com/lms/view/reservations.fxml");
    }

    @FXML
    private void loadSeatsView() {
        NavigationHelper.loadView(contentPane, "/com/lms/view/seats.fxml");
    }

    @FXML
    private void loadLogsView() {
        NavigationHelper.loadView(contentPane, "/com/lms/view/logs.fxml");
    }

    @FXML
    private void handleLogout() {
        NavigationHelper.loadScene("/com/lms/view/login.fxml", sidebar.getScene());
    }
}