package com.lms.controller.dialogs;

import com.lms.dao.BookDAO;
import com.lms.dao.UserDAO;
import com.lms.model.Book;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CreateBookReservationDialogController implements Initializable {
    
    @FXML private ComboBox<User> memberCombo;
    @FXML private ComboBox<Book> bookCombo;
    @FXML private Label memberInfoLabel;
    @FXML private Label bookInfoLabel;
    @FXML private Label reservationInfoLabel;
    @FXML private TextArea notesArea;
    @FXML private Button reserveButton;
    @FXML private Button cancelButton;
    
    private Stage dialogStage;
    private boolean confirmed = false;
    private ReservationService reservationService;
    private UserDAO userDAO;
    private BookDAO bookDAO;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reservationService = new ReservationService();
        userDAO = new UserDAO();
        bookDAO = new BookDAO();
        
        setupControls();
        loadMembers();
        loadReservableBooks();
        setupEventHandlers();
        updateReservationInfo();
    }
    
    private void setupControls() {
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
        
        bookCombo.setConverter(new StringConverter<Book>() {
            @Override
            public String toString(Book book) {
                return book != null ? book.getTitle() + " by " + book.getAuthorsString() : "";
            }
            
            @Override
            public Book fromString(String string) {
                return null;
            }
        });
    }
    
    private void setupEventHandlers() {
        // Update member info when member is selected
        memberCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            updateMemberInfo(newValue);
        });
        
        // Update book info when book is selected
        bookCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            updateBookInfo(newValue);
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
    
    private void loadReservableBooks() {
        Task<List<Book>> task = new Task<List<Book>>() {
            @Override
            protected List<Book> call() throws Exception {
                // Load books that are NOT available (issued or reserved) - these can be reserved
                List<Book> allBooks = bookDAO.findAll();
                return allBooks.stream()
                    .filter(book -> !"AVAILABLE".equals(book.getStatus()))
                    .collect(Collectors.toList());
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    ObservableList<Book> books = FXCollections.observableArrayList(getValue());
                    bookCombo.setItems(books);
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    DialogHelper.showError("Load Error", "Failed to load reservable books: " + getException().getMessage());
                });
            }
        };
        
        new Thread(task).start();
    }
    
    private void updateMemberInfo(User member) {
        if (member != null) {
            Task<String> task = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    List<com.lms.model.Reservation> activeReservations = reservationService.getBookReservationsByUser(member.getUserId())
                        .stream().filter(com.lms.model.Reservation::isActive).collect(Collectors.toList());
                    
                    int maxReservations = ReservationService.getMaxReservationsPerUser();
                    
                    return String.format("Active Reservations: %d/%d | Phone: %s", 
                        activeReservations.size(), maxReservations, member.getPhone());
                }
                
                @Override
                protected void succeeded() {
                    Platform.runLater(() -> memberInfoLabel.setText(getValue()));
                }
            };
            
            new Thread(task).start();
        } else {
            memberInfoLabel.setText("");
        }
    }
    
    private void updateBookInfo(Book book) {
        if (book != null) {
            bookInfoLabel.setText(String.format("Status: %s | Category: %s | Pages: %d", 
                book.getStatus(), book.getCategoryName(), book.getPages()));
        } else {
            bookInfoLabel.setText("");
        }
    }
    
    private void updateReservationInfo() {
        int hours = ReservationService.getDefaultReservationHours();
        reservationInfoLabel.setText(String.format("Reservation will expire in %d hours (%d days)", 
            hours, hours / 24));
    }
    
    @FXML
    private void handleReserve(ActionEvent event) {
        if (!validateInput()) return;
        
        User selectedMember = memberCombo.getValue();
        Book selectedBook = bookCombo.getValue();
        
        // Confirm the reservation
        String confirmMessage = String.format(
            "Create Book Reservation?\n\n" +
            "Member: %s\n" +
            "Book: %s\n" +
            "Status: %s\n" +
            "Expires in: %d hours\n\n" +
            "The member will be notified when the book becomes available.",
            selectedMember.getFullName(),
            selectedBook.getTitle(),
            selectedBook.getStatus(),
            ReservationService.getDefaultReservationHours()
        );
        
        if (!DialogHelper.showConfirmation("Confirm Book Reservation", confirmMessage)) {
            return;
        }
        
        // Disable controls during processing
        reserveButton.setDisable(true);
        reserveButton.setText("Processing...");
        
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                reservationService.createBookReservation(selectedMember.getUserId(), selectedBook.getBookId());
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    confirmed = true;
                    DialogHelper.showSuccess("Success", "Book reservation created successfully!");
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
                        exception.getMessage() : "An error occurred while creating the reservation.";
                    DialogHelper.showError("Reservation Failed", errorMessage);
                });
            }
        };
        
        new Thread(task).start();
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
        
        if (bookCombo.getValue() == null) {
            DialogHelper.showError("Validation Error", "Please select a book.");
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
