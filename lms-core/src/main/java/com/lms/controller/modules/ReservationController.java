package com.lms.controller.modules;

import com.lms.model.*;
import com.lms.service.ReservationService;
import com.lms.service.ServiceException;
import com.lms.controller.dialogs.CreateBookReservationDialogController;
import com.lms.controller.dialogs.BookReservationDetailsDialogController;
import com.lms.controller.dialogs.CreateSeatReservationDialogController;
import com.lms.controller.dialogs.SeatReservationDetailsDialogController;
import com.lms.util.DialogHelper;
import com.lms.util.SessionManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ReservationController implements Initializable {
    
    // Statistics Labels
    @FXML private Label totalBookReservationsLabel;
    @FXML private Label activeBookReservationsLabel;
    @FXML private Label expiredBookReservationsLabel;
    @FXML private Label fulfilledBookReservationsLabel;
    @FXML private Label totalSeatReservationsLabel;
    @FXML private Label activeSeatReservationsLabel;
    @FXML private Label ongoingSeatReservationsLabel;
    @FXML private Label availableSeatsLabel;
    
    // Tab Pane
    @FXML private TabPane reservationTabPane;
    @FXML private Tab bookReservationsTab;
    @FXML private Tab seatReservationsTab;
    
    // Book Reservations Tab Controls
    @FXML private TextField bookSearchField;
    @FXML private ComboBox<String> bookStatusFilter;
    @FXML private ComboBox<Integer> bookPageSizeCombo;
    @FXML private Button clearBookFiltersButton;
    @FXML private Button createBookReservationButton;
    @FXML private Button refreshBookButton;
    @FXML private Button viewBookReservationButton;
    @FXML private Button cancelBookReservationButton;
    @FXML private Button fulfillBookReservationButton;
    @FXML private TableView<Reservation> bookReservationsTable;
    @FXML private TableColumn<Reservation, Integer> bookResIdColumn;
    @FXML private TableColumn<Reservation, String> bookResMemberColumn;
    @FXML private TableColumn<Reservation, String> bookResBookColumn;
    @FXML private TableColumn<Reservation, String> bookResDateColumn;
    @FXML private TableColumn<Reservation, String> bookResExpiryColumn;
    @FXML private TableColumn<Reservation, String> bookResStatusColumn;
    @FXML private TableColumn<Reservation, String> bookResQueueColumn;
    
    // Seat Reservations Tab Controls
    @FXML private TextField seatSearchField;
    @FXML private ComboBox<String> seatStatusFilter;
    @FXML private ComboBox<Integer> seatPageSizeCombo;
    @FXML private Button clearSeatFiltersButton;
    @FXML private Button createSeatReservationButton;
    @FXML private Button refreshSeatButton;
    @FXML private Button viewSeatReservationButton;
    @FXML private Button cancelSeatReservationButton;
    @FXML private TableView<SeatReservation> seatReservationsTable;
    @FXML private TableColumn<SeatReservation, String> seatResMemberColumn;
    @FXML private TableColumn<SeatReservation, String> seatResSeatColumn;
    @FXML private TableColumn<SeatReservation, String> seatResStartColumn;
    @FXML private TableColumn<SeatReservation, String> seatResEndColumn;
    @FXML private TableColumn<SeatReservation, String> seatResDurationColumn;
    @FXML private TableColumn<SeatReservation, String> seatResStatusColumn;
    
    // Pagination Controls (Book Reservations)
    @FXML private Button bookFirstPageBtn;
    @FXML private Button bookPreviousPageBtn;
    @FXML private Button bookNextPageBtn;
    @FXML private Button bookLastPageBtn;
    @FXML private Label bookPageInfoLabel;
    @FXML private TextField bookGoToPageField;
    @FXML private Button bookGoToPageBtn;
    
    // Pagination Controls (Seat Reservations)
    @FXML private Button seatFirstPageBtn;
    @FXML private Button seatPreviousPageBtn;
    @FXML private Button seatNextPageBtn;
    @FXML private Button seatLastPageBtn;
    @FXML private Label seatPageInfoLabel;
    @FXML private TextField seatGoToPageField;
    @FXML private Button seatGoToPageBtn;
    
    // Service layer
    private ReservationService reservationService;
    
    // Data and pagination for book reservations
    private ObservableList<Reservation> bookReservationsList;
    private int bookCurrentPage = 1;
    private int bookPageSize = 25;
    private int bookTotalRecords = 0;
    private int bookTotalPages = 0;
    
    // Data and pagination for seat reservations
    private ObservableList<SeatReservation> seatReservationsList;
    private int seatCurrentPage = 1;
    private int seatPageSize = 25;
    private int seatTotalRecords = 0;
    private int seatTotalPages = 0;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeServices();
        initializeBookReservationsTab();
        initializeSeatReservationsTab();
        setupEventHandlers();
        loadStatistics();
        loadBookReservationsData();
        loadSeatReservationsData();
    }
    
    private void initializeServices() {
        reservationService = new ReservationService();
        bookReservationsList = FXCollections.observableArrayList();
        seatReservationsList = FXCollections.observableArrayList();
    }
    
    private void initializeBookReservationsTab() {
        // Initialize table columns
        bookResIdColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        
        bookResMemberColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getMemberName() + " (" + cellData.getValue().getMemberEmail() + ")"));
        
        bookResBookColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getBookTitle()));
        
        bookResDateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getReservationDate() != null) {
                return new SimpleStringProperty(cellData.getValue().getReservationDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
            }
            return new SimpleStringProperty("");
        });
        
        bookResExpiryColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getExpiryDate() != null) {
                return new SimpleStringProperty(cellData.getValue().getExpiryDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
            }
            return new SimpleStringProperty("");
        });
        
        bookResStatusColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatusDisplayText()));
        
        bookResQueueColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty("#" + cellData.getValue().getQueuePosition()));
        
        // Initialize controls
        bookStatusFilter.setItems(FXCollections.observableArrayList(
            "ALL", "ACTIVE", "EXPIRED", "FULFILLED", "CANCELLED"));
        bookStatusFilter.setValue("ALL");
        
        bookPageSizeCombo.setItems(FXCollections.observableArrayList(10, 25, 50, 100));
        bookPageSizeCombo.setValue(25);
        
        bookReservationsTable.setItems(bookReservationsList);
        updateBookActionButtonStates();
    }
    
    private void initializeSeatReservationsTab() {
        // Initialize table columns
        seatResMemberColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getMemberName() + " (" + cellData.getValue().getMemberEmail() + ")"));
        
        seatResSeatColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getSeatLocation() + " (" + cellData.getValue().getSeatType() + ")"));
        
        seatResStartColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getReserveStart() != null) {
                return new SimpleStringProperty(cellData.getValue().getReserveStart().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")));
            }
            return new SimpleStringProperty("");
        });
        
        seatResEndColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getReserveEnd() != null) {
                return new SimpleStringProperty(cellData.getValue().getReserveEnd().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")));
            }
            return new SimpleStringProperty("");
        });
        
        seatResDurationColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDurationMinutes() + " min"));
        
        seatResStatusColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatusDisplayText()));
        
        // Initialize controls
        seatStatusFilter.setItems(FXCollections.observableArrayList(
            "ALL", "ACTIVE", "COMPLETED", "CANCELLED", "NO_SHOW"));
        seatStatusFilter.setValue("ALL");
        
        seatPageSizeCombo.setItems(FXCollections.observableArrayList(10, 25, 50, 100));
        seatPageSizeCombo.setValue(25);
        
        seatReservationsTable.setItems(seatReservationsList);
        updateSeatActionButtonStates();
    }
    
    private void setupEventHandlers() {
        // Book reservations table selection
        bookReservationsTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> updateBookActionButtonStates());
        
        // Seat reservations table selection
        seatReservationsTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> updateSeatActionButtonStates());
        
        // Real-time search for book reservations
        bookSearchField.textProperty().addListener((obs, oldText, newText) -> {
            bookCurrentPage = 1;
            loadBookReservationsData();
        });
        
        // Real-time search for seat reservations
        seatSearchField.textProperty().addListener((obs, oldText, newText) -> {
            seatCurrentPage = 1;
            loadSeatReservationsData();
        });
        
        // Filter change handlers
        bookStatusFilter.valueProperty().addListener((obs, oldValue, newValue) -> {
            bookCurrentPage = 1;
            loadBookReservationsData();
        });
        
        seatStatusFilter.valueProperty().addListener((obs, oldValue, newValue) -> {
            seatCurrentPage = 1;
            loadSeatReservationsData();
        });
        
        bookPageSizeCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            bookPageSize = newValue;
            bookCurrentPage = 1;
            loadBookReservationsData();
        });
        
        seatPageSizeCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            seatPageSize = newValue;
            seatCurrentPage = 1;
            loadSeatReservationsData();
        });
        
        // Book reservation pagination handlers
        bookFirstPageBtn.setOnAction(e -> goToBookPage(1));
        bookPreviousPageBtn.setOnAction(e -> goToBookPage(bookCurrentPage - 1));
        bookNextPageBtn.setOnAction(e -> goToBookPage(bookCurrentPage + 1));
        bookLastPageBtn.setOnAction(e -> goToBookPage(bookTotalPages));
        bookGoToPageBtn.setOnAction(e -> {
            try {
                int page = Integer.parseInt(bookGoToPageField.getText());
                goToBookPage(page);
            } catch (NumberFormatException ex) {
                DialogHelper.showError("Invalid Page", "Please enter a valid page number.");
            }
        });
        
        // Seat reservation pagination handlers
        seatFirstPageBtn.setOnAction(e -> goToSeatPage(1));
        seatPreviousPageBtn.setOnAction(e -> goToSeatPage(seatCurrentPage - 1));
        seatNextPageBtn.setOnAction(e -> goToSeatPage(seatCurrentPage + 1));
        seatLastPageBtn.setOnAction(e -> goToSeatPage(seatTotalPages));
        seatGoToPageBtn.setOnAction(e -> {
            try {
                int page = Integer.parseInt(seatGoToPageField.getText());
                goToSeatPage(page);
            } catch (NumberFormatException ex) {
                DialogHelper.showError("Invalid Page", "Please enter a valid page number.");
            }
        });
        
        // Double-click handlers
        bookReservationsTable.setRowFactory(tv -> {
            TableRow<Reservation> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    handleViewBookReservation(null);
                }
            });
            return row;
        });
        
        seatReservationsTable.setRowFactory(tv -> {
            TableRow<SeatReservation> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    handleViewSeatReservation(null);
                }
            });
            return row;
        });
    }
    
    private void updateBookActionButtonStates() {
        Reservation selectedReservation = bookReservationsTable.getSelectionModel().getSelectedItem();
        boolean hasSelection = selectedReservation != null;
        boolean isActive = hasSelection && selectedReservation.isActive();
        
        viewBookReservationButton.setDisable(!hasSelection);
        cancelBookReservationButton.setDisable(!isActive);
        fulfillBookReservationButton.setDisable(!isActive);
    }
    
    private void updateSeatActionButtonStates() {
        SeatReservation selectedReservation = seatReservationsTable.getSelectionModel().getSelectedItem();
        boolean hasSelection = selectedReservation != null;
        boolean isActive = hasSelection && selectedReservation.isActive();
        
        viewSeatReservationButton.setDisable(!hasSelection);
        cancelSeatReservationButton.setDisable(!isActive);
    }
    
    // ========== BOOK RESERVATION HANDLERS ==========
    
    @FXML
    private void handleCreateBookReservation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/create_book_reservation_dialog.fxml"));
            Parent root = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create Book Reservation");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(createBookReservationButton.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            
            CreateBookReservationDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            
            dialogStage.showAndWait();
            
            if (controller.isConfirmed()) {
                loadStatistics();
                loadBookReservationsData();
            }
        } catch (IOException e) {
            DialogHelper.showError("Error", "Could not open Create Book Reservation dialog: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleViewBookReservation(ActionEvent event) {
        Reservation selectedReservation = bookReservationsTable.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) return;
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/book_reservation_details_dialog.fxml"));
            Parent root = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Book Reservation Details");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(viewBookReservationButton.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            
            BookReservationDetailsDialogController controller = loader.getController();
            controller.setReservation(selectedReservation);
            controller.setDialogStage(dialogStage);
            
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            DialogHelper.showError("Error", "Could not open Reservation Details dialog: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancelBookReservation(ActionEvent event) {
        Reservation selectedReservation = bookReservationsTable.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) return;
        
        if (!selectedReservation.isActive()) {
            DialogHelper.showWarning("Cannot Cancel", "Only active reservations can be cancelled.");
            return;
        }
        
        String message = String.format(
            "Cancel Book Reservation?\n\n" +
            "Book: %s\n" +
            "Member: %s\n" +
            "Reserved: %s\n" +
            "Expires: %s\n\n" +
            "This action cannot be undone.",
            selectedReservation.getBookTitle(),
            selectedReservation.getMemberName(),
            selectedReservation.getReservationDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")),
            selectedReservation.getExpiryDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
        );
        
        if (DialogHelper.showConfirmation("Cancel Reservation", message)) {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    reservationService.cancelBookReservation(selectedReservation.getReservationId());
                    return null;
                }
                
                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        DialogHelper.showSuccess("Success", "Book reservation cancelled successfully!");
                        loadStatistics();
                        loadBookReservationsData();
                    });
                }
                
                @Override
                protected void failed() {
                    Platform.runLater(() -> {
                        Throwable exception = getException();
                        String errorMessage = exception instanceof ServiceException ? 
                            exception.getMessage() : "An error occurred while cancelling the reservation.";
                        DialogHelper.showError("Cancellation Failed", errorMessage);
                    });
                }
            };
            
            new Thread(task).start();
        }
    }
    
    @FXML
    private void handleFulfillBookReservation(ActionEvent event) {
        Reservation selectedReservation = bookReservationsTable.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) return;
        
        if (!selectedReservation.isActive()) {
            DialogHelper.showWarning("Cannot Fulfill", "Only active reservations can be fulfilled.");
            return;
        }
        
        String message = String.format(
            "Fulfill Book Reservation?\n\n" +
            "This will mark the reservation as fulfilled, indicating that the book\n" +
            "has been made available to the member.\n\n" +
            "Book: %s\n" +
            "Member: %s\n" +
            "Reserved: %s",
            selectedReservation.getBookTitle(),
            selectedReservation.getMemberName(),
            selectedReservation.getReservationDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
        );
        
        if (DialogHelper.showConfirmation("Fulfill Reservation", message)) {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    reservationService.fulfillBookReservation(selectedReservation.getReservationId());
                    return null;
                }
                
                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        DialogHelper.showSuccess("Success", "Book reservation fulfilled successfully!");
                        loadStatistics();
                        loadBookReservationsData();
                    });
                }
                
                @Override
                protected void failed() {
                    Platform.runLater(() -> {
                        Throwable exception = getException();
                        String errorMessage = exception instanceof ServiceException ? 
                            exception.getMessage() : "An error occurred while fulfilling the reservation.";
                        DialogHelper.showError("Fulfillment Failed", errorMessage);
                    });
                }
            };
            
            new Thread(task).start();
        }
    }
    
    @FXML
    private void handleClearBookFilters(ActionEvent event) {
        bookSearchField.clear();
        bookStatusFilter.setValue("ALL");
        bookCurrentPage = 1;
        loadBookReservationsData();
    }
    
    @FXML
    private void handleRefreshBook(ActionEvent event) {
        loadStatistics();
        loadBookReservationsData();
    }
    
    // ========== SEAT RESERVATION HANDLERS ==========
    
    @FXML
    private void handleCreateSeatReservation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/create_seat_reservation_dialog.fxml"));
            Parent root = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create Seat Reservation");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(createSeatReservationButton.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            
            CreateSeatReservationDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            
            dialogStage.showAndWait();
            
            if (controller.isConfirmed()) {
                loadStatistics();
                loadSeatReservationsData();
            }
        } catch (IOException e) {
            DialogHelper.showError("Error", "Could not open Create Seat Reservation dialog: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleViewSeatReservation(ActionEvent event) {
        SeatReservation selectedReservation = seatReservationsTable.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) return;
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/seat_reservation_details_dialog.fxml"));
            Parent root = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Seat Reservation Details");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(viewSeatReservationButton.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            
            SeatReservationDetailsDialogController controller = loader.getController();
            controller.setReservation(selectedReservation);
            controller.setDialogStage(dialogStage);
            
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            DialogHelper.showError("Error", "Could not open Seat Reservation Details dialog: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancelSeatReservation(ActionEvent event) {
        SeatReservation selectedReservation = seatReservationsTable.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) return;
        
        if (!selectedReservation.isActive()) {
            DialogHelper.showWarning("Cannot Cancel", "Only active reservations can be cancelled.");
            return;
        }
        
        String message = String.format(
            "Cancel Seat Reservation?\n\n" +
            "Seat: %s\n" +
            "Member: %s\n" +
            "Time: %s\n" +
            "Duration: %d minutes\n\n" +
            "This action cannot be undone.",
            selectedReservation.getSeatLocation() + " (" + selectedReservation.getSeatType() + ")",
            selectedReservation.getMemberName(),
            selectedReservation.getTimeRange(),
            selectedReservation.getDurationMinutes()
        );
        
        if (DialogHelper.showConfirmation("Cancel Seat Reservation", message)) {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    reservationService.cancelSeatReservation(selectedReservation.getSeatId(), selectedReservation.getUserId());
                    return null;
                }
                
                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        DialogHelper.showSuccess("Success", "Seat reservation cancelled successfully!");
                        loadStatistics();
                        loadSeatReservationsData();
                    });
                }
                
                @Override
                protected void failed() {
                    Platform.runLater(() -> {
                        Throwable exception = getException();
                        String errorMessage = exception instanceof ServiceException ? 
                            exception.getMessage() : "An error occurred while cancelling the seat reservation.";
                        DialogHelper.showError("Cancellation Failed", errorMessage);
                    });
                }
            };
            
            new Thread(task).start();
        }
    }
    
    @FXML
    private void handleClearSeatFilters(ActionEvent event) {
        seatSearchField.clear();
        seatStatusFilter.setValue("ALL");
        seatCurrentPage = 1;
        loadSeatReservationsData();
    }
    
    @FXML
    private void handleRefreshSeat(ActionEvent event) {
        loadStatistics();
        loadSeatReservationsData();
    }
    
    // ========== DATA LOADING METHODS ==========
    
    private void loadStatistics() {
        Task<ReservationStatistics> task = new Task<ReservationStatistics>() {
            @Override
            protected ReservationStatistics call() throws Exception {
                return reservationService.getStatistics();
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> updateStatisticsUI(getValue()));
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.err.println("Failed to load reservation statistics: " + getException().getMessage());
                });
            }
        };
        
        new Thread(task).start();
    }
    
    private void updateStatisticsUI(ReservationStatistics stats) {
        totalBookReservationsLabel.setText(String.valueOf(stats.getTotalBookReservations()));
        activeBookReservationsLabel.setText(String.valueOf(stats.getActiveBookReservations()));
        expiredBookReservationsLabel.setText(String.valueOf(stats.getExpiredBookReservations()));
        fulfilledBookReservationsLabel.setText(String.valueOf(stats.getFulfilledBookReservations()));
        totalSeatReservationsLabel.setText(String.valueOf(stats.getTotalSeatReservations()));
        activeSeatReservationsLabel.setText(String.valueOf(stats.getActiveSeatReservations()));
        ongoingSeatReservationsLabel.setText(String.valueOf(stats.getOngoingSeatReservations()));
        availableSeatsLabel.setText(String.valueOf(stats.getAvailableSeats()));
    }
    
    private void loadBookReservationsData() {
        Task<java.util.List<Reservation>> task = new Task<java.util.List<Reservation>>() {
            @Override
            protected java.util.List<Reservation> call() throws Exception {
                String searchTerm = bookSearchField.getText();
                String status = bookStatusFilter.getValue();
                int offset = (bookCurrentPage - 1) * bookPageSize;
                
                // Get total count for pagination
                bookTotalRecords = reservationService.countBookReservationSearchResults(searchTerm, status);
                bookTotalPages = (int) Math.ceil((double) bookTotalRecords / bookPageSize);
                
                return reservationService.searchBookReservations(searchTerm, status, bookPageSize, offset);
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    bookReservationsList.clear();
                    bookReservationsList.addAll(getValue());
                    updateBookPaginationUI();
                    updateBookActionButtonStates();
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    DialogHelper.showError("Load Error", 
                        "Failed to load book reservations data: " + getException().getMessage());
                });
            }
        };
        
        new Thread(task).start();
    }
    
    private void loadSeatReservationsData() {
        Task<java.util.List<SeatReservation>> task = new Task<java.util.List<SeatReservation>>() {
            @Override
            protected java.util.List<SeatReservation> call() throws Exception {
                String searchTerm = seatSearchField.getText();
                String status = seatStatusFilter.getValue();
                int offset = (seatCurrentPage - 1) * seatPageSize;
                
                // Get total count for pagination
                seatTotalRecords = reservationService.countSeatReservationSearchResults(searchTerm, status);
                seatTotalPages = (int) Math.ceil((double) seatTotalRecords / seatPageSize);
                
                return reservationService.searchSeatReservations(searchTerm, status, seatPageSize, offset);
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    seatReservationsList.clear();
                    seatReservationsList.addAll(getValue());
                    updateSeatPaginationUI();
                    updateSeatActionButtonStates();
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    DialogHelper.showError("Load Error", 
                        "Failed to load seat reservations data: " + getException().getMessage());
                });
            }
        };
        
        new Thread(task).start();
    }
    
    // ========== PAGINATION METHODS ==========
    
    private void updateBookPaginationUI() {
        bookFirstPageBtn.setDisable(bookCurrentPage <= 1);
        bookPreviousPageBtn.setDisable(bookCurrentPage <= 1);
        bookNextPageBtn.setDisable(bookCurrentPage >= bookTotalPages);
        bookLastPageBtn.setDisable(bookCurrentPage >= bookTotalPages);
        
        int startRecord = bookTotalRecords > 0 ? (bookCurrentPage - 1) * bookPageSize + 1 : 0;
        int endRecord = Math.min(bookCurrentPage * bookPageSize, bookTotalRecords);
        
        bookPageInfoLabel.setText(String.format("Page %d of %d (%d-%d of %d records)",
            bookCurrentPage, Math.max(bookTotalPages, 1), startRecord, endRecord, bookTotalRecords));
        
        bookGoToPageField.setPromptText(String.valueOf(bookCurrentPage));
    }
    
    private void updateSeatPaginationUI() {
        seatFirstPageBtn.setDisable(seatCurrentPage <= 1);
        seatPreviousPageBtn.setDisable(seatCurrentPage <= 1);
        seatNextPageBtn.setDisable(seatCurrentPage >= seatTotalPages);
        seatLastPageBtn.setDisable(seatCurrentPage >= seatTotalPages);
        
        int startRecord = seatTotalRecords > 0 ? (seatCurrentPage - 1) * seatPageSize + 1 : 0;
        int endRecord = Math.min(seatCurrentPage * seatPageSize, seatTotalRecords);
        
        seatPageInfoLabel.setText(String.format("Page %d of %d (%d-%d of %d records)",
            seatCurrentPage, Math.max(seatTotalPages, 1), startRecord, endRecord, seatTotalRecords));
        
        seatGoToPageField.setPromptText(String.valueOf(seatCurrentPage));
    }
    
    private void goToBookPage(int page) {
        if (page >= 1 && page <= bookTotalPages) {
            bookCurrentPage = page;
            loadBookReservationsData();
        }
    }
    
    private void goToSeatPage(int page) {
        if (page >= 1 && page <= seatTotalPages) {
            seatCurrentPage = page;
            loadSeatReservationsData();
        }
    }
}