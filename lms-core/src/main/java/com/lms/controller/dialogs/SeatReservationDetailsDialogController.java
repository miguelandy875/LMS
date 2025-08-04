package com.lms.controller.dialogs;

import com.lms.model.SeatReservation;
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

public class SeatReservationDetailsDialogController implements Initializable {
    
    @FXML private Label reservationIdLabel;
    @FXML private Label statusBadge;
    @FXML private Label memberNameLabel;
    @FXML private Label memberEmailLabel;
    @FXML private Label seatLocationLabel;
    @FXML private Label seatTypeLabel;
    @FXML private Label startTimeLabel;
    @FXML private Label endTimeLabel;
    @FXML private Label durationLabel;
    @FXML private Label statusInfoLabel;
    @FXML private VBox timeInfoSection;
    @FXML private Button closeButton;
    
    private Stage dialogStage;
    private SeatReservation reservation;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // All sections visible by default
    }
    
    public void setReservation(SeatReservation reservation) {
        this.reservation = reservation;
        populateFields();
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    private void populateFields() {
        if (reservation == null) return;
        
        // Basic reservation information
        reservationIdLabel.setText("Seat Reservation Details");
        
        // Member information
        memberNameLabel.setText(reservation.getMemberName());
        memberEmailLabel.setText(reservation.getMemberEmail());
        
        // Seat information
        seatLocationLabel.setText(reservation.getSeatLocation());
        seatTypeLabel.setText(reservation.getSeatType());
        
        // Time information
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
        startTimeLabel.setText(reservation.getReserveStart().format(formatter));
        endTimeLabel.setText(reservation.getReserveEnd().format(formatter));
        durationLabel.setText(reservation.getDurationMinutes() + " minutes");
        
        // Status and status-specific information
        String status = reservation.getStatus();
        statusBadge.setText(status);
        updateStatusBadgeStyle(status);
        
        // Status information
        if (reservation.isActive()) {
            if (reservation.isOngoing()) {
                long minutesLeft = reservation.getMinutesRemaining();
                statusInfoLabel.setText(String.format("Reservation is currently ongoing. %d minutes remaining.", minutesLeft));
                statusInfoLabel.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
            } else if (reservation.isUpcoming()) {
                long minutesUntil = reservation.getMinutesUntilStart();
                statusInfoLabel.setText(String.format("Reservation starts in %d minutes.", minutesUntil));
                statusInfoLabel.setStyle("-fx-text-fill: #17a2b8; -fx-font-weight: bold;");
            } else if (reservation.isPast()) {
                statusInfoLabel.setText("This reservation time has passed.");
                statusInfoLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-weight: bold;");
            }
        } else {
            switch (status) {
                case "COMPLETED":
                    statusInfoLabel.setText("This reservation was completed successfully.");
                    statusInfoLabel.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                    break;
                case "CANCELLED":
                    statusInfoLabel.setText("This reservation was cancelled.");
                    statusInfoLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                    break;
                case "NO_SHOW":
                    statusInfoLabel.setText("Member did not show up for this reservation.");
                    statusInfoLabel.setStyle("-fx-text-fill: #ffc107; -fx-font-weight: bold;");
                    break;
                default:
                    statusInfoLabel.setText("Status: " + status);
                    statusInfoLabel.setStyle("-fx-text-fill: #6c757d;");
                    break;
            }
        }
    }
    
    private void updateStatusBadgeStyle(String status) {
        statusBadge.getStyleClass().removeAll("status-badge-active", "status-badge-ongoing", 
            "status-badge-upcoming", "status-badge-completed", "status-badge-cancelled", "status-badge-no-show");
        
        switch (status) {
            case "ACTIVE":
                if (reservation != null && reservation.isOngoing()) {
                    statusBadge.getStyleClass().add("status-badge-ongoing");
                    statusBadge.setStyle("-fx-background-color: #28a745;");
                } else if (reservation != null && reservation.isUpcoming()) {
                    statusBadge.getStyleClass().add("status-badge-upcoming");
                    statusBadge.setStyle("-fx-background-color: #17a2b8;");
                } else {
                    statusBadge.getStyleClass().add("status-badge-active");
                    statusBadge.setStyle("-fx-background-color: #6c757d;");
                }
                break;
            case "COMPLETED":
                statusBadge.getStyleClass().add("status-badge-completed");
                statusBadge.setStyle("-fx-background-color: #28a745;");
                break;
            case "CANCELLED":
                statusBadge.getStyleClass().add("status-badge-cancelled");
                statusBadge.setStyle("-fx-background-color: #dc3545;");
                break;
            case "NO_SHOW":
                statusBadge.getStyleClass().add("status-badge-no-show");
                statusBadge.setStyle("-fx-background-color: #ffc107; -fx-text-fill: #212529;");
                break;
            default:
                statusBadge.setStyle("-fx-background-color: #6c757d;");
                break;
        }
    }
    
    @FXML
    private void handleClose(ActionEvent event) {
        dialogStage.close();
    }
}