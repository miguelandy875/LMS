package com.lms.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

public class NavigationHelper {
    public static void loadView(StackPane contentPane, String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(NavigationHelper.class.getResource(fxmlPath));
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            DialogHelper.showError("Error loading view: " + e.getMessage());
        }
    }

    public static void loadScene(String fxmlPath, Scene currentScene) {
        try {
            Parent root = FXMLLoader.load(NavigationHelper.class.getResource(fxmlPath));
            currentScene.setRoot(root);
        } catch (Exception e) {
            DialogHelper.showError("Error loading scene: " + e.getMessage());
        }
    }
} 