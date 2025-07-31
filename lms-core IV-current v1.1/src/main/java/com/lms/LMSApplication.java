package com.lms;

import com.lms.util.DatabaseUtil;
import com.lms.util.DialogHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LMSApplication extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Test database connection first
            if (!DatabaseUtil.testConnection()) {
                DialogHelper.showError("Database Error", 
                    "Could not connect to database. Please check your database configuration.");
                return;
            }
            
            // Load login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            primaryStage.setTitle("Library Management System - Login");
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();
            
        } catch (Exception e) {
            DialogHelper.showError("Application Error", "Could not start application: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void stop() {
        // Cleanup when application closes
        com.lms.util.SessionManager.getInstance().logout();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
