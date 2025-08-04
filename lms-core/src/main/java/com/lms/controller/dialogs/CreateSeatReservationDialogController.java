package com.lms.controller.dialogs;

import com.lms.dao.UserDAO;
import com.lms.model.Seat;
import com.lms.model.User;
import com.lms.service.ReservationService;
import com.lms.service.ServiceException;
import com.lms.util.DialogHelper;
import com.lms.util.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.event.ActionEvent;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CreateSeatReservationDialogController implements Initializable {
    
    @FXML private ComboBox<User> memberCombo;
    @FXML private ComboBox<Seat> seatCombo;
    @FXML private ComboBox<String> seatTypeFilter;
    @FXML private DatePicker reservationDatePicker;
    @FXML private ComboBox<String> startTimeCombo;
    @FXML private ComboBox<String> endTimeCombo;
    @FXML private Spinner<Integer> durationSpinner;
    @FXML private Label memberInfoLabel;
    @FXML private Label seatInfoLabel;
    @FXML private Label durationInfoLabel;
    @FXML private TextArea notesArea;
    @FXML private Button reserveButton;
    @FXML private Button cancelButton;
    
    private Stage dialogStage;
    private boolean confirmed = false;
    private ReservationService reservationService;
    private UserDAO userDAO;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reservationService = new ReservationService();
        userDAO = new UserDAO();
        
        setupControls();
        loadMembers();
        loadSeats();
        setupEventHandlers();
        updateDurationInfo();
    }
    
    private void setupControls() {
        // Set default date to today
        reservationDatePicker.setValue(LocalDate.now());
        
        // Setup time combos
        setupTimeComboBoxes();
        
        // Setup duration spinner
        durationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
            30, ReservationService.getMaxSeatReservationHours() * 60, 120, 30));
        
        // Setup seat type filter
        seatTypeFilter.setItems(FXCollections.observableArrayList(
            "ALL", "STUDY", "COMPUTER", "GROUP", "SILENT"));
        seatTypeFilter.setValue("ALL");
        
        // Setup combo box converters
        memberCombo.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user != null ? user.getFullName() + " (" + user.getEmail() + ")" : "";
            }
            
            @Override
            public User fromString(String string) {
                return null;
            }
        });
        
        seatCombo.setConverter(new StringConverter<Seat>() {
            @Override
            public String toString(Seat seat) {
                return seat != null ? seat.getDisplayName() : "";
            }
            
            @Override
            public Seat fromString(String string) {
                return null;
            }
        });
    }
    
    private void setupTimeComboBoxes() {
        ObservableList<String> timeSlots = FXCollections.observableArrayList();
        
        // Generate time slots from 08:00 to 22:00 in 30-minute intervals
        for (int hour = 8; hour <= 22; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                timeSlots.add(String.format("%02d:%02d", hour, minute));
            }
        }
        
        startTimeCombo.setItems(timeSlots);
        endTimeCombo.setItems(timeSlots);
        
        // Set default times
        startTimeCombo.setValue("09:00");
        endTimeCombo.setValue("11:00");
    }
    
    private void setupEventHandlers() {
        // Update member info when member is selected
        memberCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            updateMemberInfo(newValue);
        });
        
        // Update seat info when seat is selected
        seatCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            updateSeatInfo(newValue);
        });
        
        // Filter seats by type
        seatTypeFilter.valueProperty().addListener((obs, oldValue, newValue) -> {
            loadSeats();
        });
        
        // Update end time when start time or duration changes
        startTimeCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            updateEndTimeFromDuration();
        });
        
        durationSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            updateEndTimeFromDuration();
        });
        
        // Update duration when end time changes
        endTimeCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            updateDurationFromEndTime();
        });
    }
    
    private void loadMembers() {
        Task<List<User>> task = new Task<List<User>>() {
            @Override
            protected List<User> call() throws Exception {
                return userDAO.findByRole("MEMBER").stream()
                    .filter(user -> "ACTIVE".equals(user.getStatus()))
                    .collect(Collectors.toList());
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    ObservableList<User> members = FXCollections.observableArrayList(getValue());
                    memberCombo.setItems(members);
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    DialogHelper.showError("Load Error", "Failed to load members: " + getException().getMessage());
                });
            }
        };
        
        new Thread(task).start();
    }
    
    private void loadSeats() {
        Task<List<Seat>> task = new Task<List<Seat>>() {
            @Override
            protected List<Seat> call() throws Exception {
                List<Seat> seats = reservationService.getAvailableSeats();
                String typeFilter = seatTypeFilter.getValue();
                
                if (typeFilter != null && !"ALL".equals(typeFilter)) {
                    seats = seats.stream()
                        .filter(seat -> typeFilter.equals(seat.getSeatType()))
                        .collect(Collectors.toList());
                }
                
                return seats;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    ObservableList<Seat> seats = FXCollections.observableArrayList(getValue());
                    seatCombo.setItems(seats);
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    DialogHelper.showError("Load Error", "Failed to load seats: " + getException().getMessage());
                });
            }
        };
        
        new Thread(task).start();
    }
    
    private void updateMemberInfo(User member) {
        if (member != null) {
            memberInfoLabel.setText(String.format("Phone: %s | Role: %s", 
                member.getPhone(), member.getRole()));
        } else {
            memberInfoLabel.setText("");
        }
    }
    
    private void updateSeatInfo(Seat seat) {
        if (seat != null) {
            seatInfoLabel.setText(String.format("Type: %s | Status: %s", 
                seat.getSeatType(), seat.getSeatStatus()));
        } else {
            seatInfoLabel.setText("");
        }
    }
    
    private void updateDurationInfo() {
        int maxHours = ReservationService.getMaxSeatReservationHours();
        durationInfoLabel.setText(String.format("Maximum reservation duration: %d hours", maxHours));
    }
    
    private void updateEndTimeFromDuration() {
        String startTimeStr = startTimeCombo.getValue();
        Integer durationMinutes = durationSpinner.getValue();
        
        if (startTimeStr != null && durationMinutes != null) {
            try {
                LocalTime startTime = LocalTime.parse(startTimeStr);
                LocalTime endTime = startTime.plusMinutes(durationMinutes);
                
                // Format and set end time
                String endTimeStr = String.format("%02d:%02d", endTime.getHour(), endTime.getMinute());
                endTimeCombo.setValue(endTimeStr);
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }
    }
    
    private void updateDurationFromEndTime() {
        String startTimeStr = startTimeCombo.getValue();
        String endTimeStr = endTimeCombo.getValue();
        
        if (startTimeStr != null && endTimeStr != null) {
            try {
                LocalTime startTime = LocalTime.parse(startTimeStr);
                LocalTime endTime = LocalTime.parse(endTimeStr);
                
                if (endTime.isAfter(startTime)) {
                    long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
                    durationSpinner.getValueFactory().setValue((int) minutes);
                }
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }
    }
    
    @FXML
    private void handleReserve(ActionEvent event) {
        if (!validateInput()) return;
        
        User selectedMember = memberCombo.getValue();
        Seat selectedSeat = seatCombo.getValue();
        LocalDate date = reservationDatePicker.getValue();
        String startTimeStr = startTimeCombo.getValue();
        String endTimeStr = endTimeCombo.getValue();
        
        try {
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);
            LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(date, endTime);
            
            // Confirm the reservation
            String confirmMessage = String.format(
                "Create Seat Reservation?\n\n" +
                "Member: %s\n" +
                "Seat: %s\n" +
                "Date: %s\n" +
                "Time: %s - %s\n" +
                "Duration: %d minutes",
                selectedMember.getFullName(),
                selectedSeat.getDisplayName(),
                date.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                startTimeStr, endTimeStr,
                durationSpinner.getValue()
            );
            
            if (!DialogHelper.showConfirmation("Confirm Seat Reservation", confirmMessage)) {
                return;
            }
            
            // Disable controls during processing
            reserveButton.setDisable(true);
            reserveButton.setText("Processing...");
            
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    reservationService.createSeatReservation(selectedMember.getUserId(), 
                        selectedSeat.getSeatId(), startDateTime, endDateTime);
                    return null;
                }
                
                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        confirmed = true;
                        DialogHelper.showSuccess("Success", "Seat reservation created successfully!");
                        dialogStage.close();
                    });
                }
                
                @Override
                protected void failed() {
                    Platform.runLater(() -> {
                        reserveButton.setDisable(false);
                        reserveButton.setText("Create Reservation");
                        
                        Throwable exception = getException();
                        String errorMessage = exception instanceof ServiceException ? 
                            exception.getMessage() : "An error occurred while creating the seat reservation.";
                        DialogHelper.showError("Reservation Failed", errorMessage);
                    });
                }
            };
            
            new Thread(task).start();
            
        } catch (Exception e) {
            DialogHelper.showError("Time Format Error", "Invalid time format. Please select valid times.");
        }
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        dialogStage.close();
    }
    
    private boolean validateInput() {
        if (memberCombo.getValue() == null) {
            DialogHelper.showError("Validation Error", "Please select a member.");
            return false;
        }
        
        if (seatCombo.getValue() == null) {
            DialogHelper.showError("Validation Error", "Please select a seat.");
            return false;
        }
        
        if (reservationDatePicker.getValue() == null) {
            DialogHelper.showError("Validation Error", "Please select a date.");
            return false;
        }
        
        if (startTimeCombo.getValue() == null || endTimeCombo.getValue() == null) {
            DialogHelper.showError("Validation Error", "Please select start and end times.");
            return false;
        }
        
        LocalDate selectedDate = reservationDatePicker.getValue();
        if (selectedDate.isBefore(LocalDate.now())) {
            DialogHelper.showError("Validation Error", "Cannot reserve seat for past dates.");
            return false;
        }
        
        try {
            LocalTime startTime = LocalTime.parse(startTimeCombo.getValue());
            LocalTime endTime = LocalTime.parse(endTimeCombo.getValue());
            
            if (!endTime.isAfter(startTime)) {
                DialogHelper.showError("Validation Error", "End time must be after start time.");
                return false;
            }
            
            long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
            int maxMinutes = ReservationService.getMaxSeatReservationHours() * 60;
            
            if (minutes > maxMinutes) {
                DialogHelper.showError("Validation Error", 
                    String.format("Maximum reservation duration is %d hours.", maxMinutes / 60));
                return false;
            }
            
            if (minutes < 30) {
                DialogHelper.showError("Validation Error", "Minimum reservation duration is 30 minutes.");
                return false;
            }
            
        } catch (Exception e) {
            DialogHelper.showError("Time Format Error", "Invalid time format.");
            return false;
        }
        
        return true;
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}