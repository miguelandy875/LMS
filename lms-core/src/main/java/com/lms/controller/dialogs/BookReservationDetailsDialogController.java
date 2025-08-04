package com.lms.controller.dialogs;

import com.lms.model.Reservation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class BookReservationDetailsDialogController implements Initializable {
    
    @FXML private Label reservationIdLabel;
    @FXML private Label statusBadge;
    @FXML private Label memberNameLabel;
    @FXML private Label memberEmailLabel;
    @FXML private Label bookTitleLabel;
    @FXML private Label bookAuthorsLabel;
    @FXML private Label reservationDateLabel;
    @FXML private Label expiryDateLabel;
    @FXML private Label queuePositionLabel;
    @FXML private Label statusInfoLabel;
    @FXML private VBox queueSection;
    @FXML private Button closeButton;
    
    private Stage dialogStage;
    private Reservation reservation;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initially hide queue section
        queueSection.setVisible(false);
        queueSection.setManaged(false);
    }
    
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        populateFields();
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    private void populateFields() {
        if (reservation == null) return;
        
        // Basic reservation information
        reservationIdLabel.setText("Reservation ID: #" + reservation.getReservationId());
        
        // Member information
        memberNameLabel.setText(reservation.getMemberName());
        memberEmailLabel.setText(reservation.getMemberEmail());
        
        // Book information
        bookTitleLabel.setText(reservation.getBookTitle());
        bookAuthorsLabel.setText(reservation.getBookAuthors() != null ? reservation.getBookAuthors() : "Unknown Author");
        
        // Date information
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
        reservationDateLabel.setText(reservation.getReservationDate().format(formatter));
        expiryDateLabel.setText(reservation.getExpiryDate().format(formatter));
        
        // Status and status-specific information
        String status = reservation.getStatus();
        statusBadge.setText(status);
        updateStatusBadgeStyle(status);
        
        // Status information
        if (reservation.isActive()) {
            if (reservation.isExpired()) {
                statusInfoLabel.setText("This reservation has expired and will be automatically cancelled.");
                statusInfoLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
            } else {
                long hours = reservation.getHoursUntilExpiry();
                statusInfoLabel.setText(String.format("Reservation expires in %d hours", hours));
                
                if (hours <= 24) {
                    statusInfoLabel.setStyle("-fx-text-fill: #ffc107; -fx-font-weight: bold;");
                } else {
                    statusInfoLabel.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                }
            }
            
            // Show queue information for active reservations
            showQueueSection();
        } else {
            switch (status) {
                case "FULFILLED":
                    statusInfoLabel.setText("This reservation has been fulfilled. The book was made available to the member.");
                    statusInfoLabel.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                    break;
                case "CANCELLED":
                    statusInfoLabel.setText("This reservation has been cancelled.");
                    statusInfoLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                    break;
                case "EXPIRED":
                    statusInfoLabel.setText("This reservation expired and was automatically cancelled.");
                    statusInfoLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-weight: bold;");
                    break;
                default:
                    statusInfoLabel.setText("Status: " + status);
                    statusInfoLabel.setStyle("-fx-text-fill: #6c757d;");
                    break;
            }
        }
    }
    
    private void updateStatusBadgeStyle(String status) {
        statusBadge.getStyleClass().removeAll("status-badge-active", "status-badge-expired", 
            "status-badge-fulfilled", "status-badge-cancelled");
        
        switch (status) {
            case "ACTIVE":
                if (reservation != null && reservation.isExpired()) {
                    statusBadge.getStyleClass().add("status-badge-expired");
                    statusBadge.setStyle("-fx-background-color: #ffc107; -fx-text-fill: #212529;");
                } else {
                    statusBadge.getStyleClass().add("status-badge-active");
                    statusBadge.setStyle("-fx-background-color: #17a2b8;");
                }
                break;
            case "FULFILLED":
                statusBadge.getStyleClass().add("status-badge-fulfilled");
                statusBadge.setStyle("-fx-background-color: #28a745;");
                break;
            case "CANCELLED":
                statusBadge.getStyleClass().add("status-badge-cancelled");
                statusBadge.setStyle("-fx-background-color: #dc3545;");
                break;
            case "EXPIRED":
                statusBadge.getStyleClass().add("status-badge-expired");
                statusBadge.setStyle("-fx-background-color: #6c757d;");
                break;
            default:
                statusBadge.setStyle("-fx-background-color: #6c757d;");
                break;
        }
    }
    
    private void showQueueSection() {
        queueSection.setVisible(true);
        queueSection.setManaged(true);
        
        queuePositionLabel.setText("Position #" + reservation.getQueuePosition());
    }
    
    @FXML
    private void handleClose(ActionEvent event) {
        dialogStage.close();
    }
}