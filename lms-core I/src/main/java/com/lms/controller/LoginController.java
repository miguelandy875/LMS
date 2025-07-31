package com.lms.controller;

import com.lms.model.Librarian;
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

    private final UserService userService = new UserServiceImpl();

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        User user = userService.login(email, password);

        if (user instanceof Librarian) {
		try{
            
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lms/view/librarian_dashboard.fxml"));
                Parent dashboardRoot = loader.load();

                // Get the controller and pass the logged-in user
                LibrarianDashboardController controller = loader.getController();
                controller.setLibrarian((Librarian) user); // Inject data

                // Switch scene
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(dashboardRoot));
                stage.setTitle("Librarian Dashboard");
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Invalid credentials or not a librarian.");
        }
    }
}
