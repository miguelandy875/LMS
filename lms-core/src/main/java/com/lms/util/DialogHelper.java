package com.lms.util;

import javafx.scene.control.*;
import javafx.scene.layout.Region;
import java.util.Optional;

public class DialogHelper {

    private static final String DARK_ALERT_CSS = "/styles/dark-alert.css";

    // Apply dark stylesheet to the dialog
    private static void applyDarkTheme(Dialog<?> dialog) {
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().clear(); // Optional: Clear others if needed
        dialogPane.getStylesheets().add(DialogHelper.class.getResource(DARK_ALERT_CSS).toExternalForm());
        dialogPane.setMinHeight(Region.USE_PREF_SIZE); // Ensure proper sizing
    }

    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        applyDarkTheme(alert);
        alert.showAndWait();
    }

    public static void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        applyDarkTheme(alert);
        alert.showAndWait();
    }

    public static void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        applyDarkTheme(alert);
        alert.showAndWait();
    }

    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        applyDarkTheme(alert);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static String showInputDialog(String title, String message, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        applyDarkTheme(dialog);
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        applyDarkTheme(alert);
        alert.showAndWait();
    }
}
