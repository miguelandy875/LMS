package com.lms.controller.dialogs;

import com.lms.dao.BookDAO;
import com.lms.dao.UserDAO;
import com.lms.model.Book;
import com.lms.model.User;
import com.lms.service.LoanService;
import com.lms.service.ServiceException;
import com.lms.util.DialogHelper;
import com.lms.util.SessionManager;
import javafx.util.StringConverter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class IssueLoanDialogController implements Initializable {
    
    @FXML private ComboBox<User> memberCombo;
    @FXML private ComboBox<Book> bookCombo;
    @FXML private DatePicker issueDatePicker;
    @FXML private DatePicker returnDatePicker;
    @FXML private Spinner<Integer> loanPeriodSpinner;
    @FXML private TextArea notesArea;
    @FXML private Button issueButton;
    @FXML private Button cancelButton;
    @FXML private Label memberInfoLabel;
    @FXML private Label bookInfoLabel;
    
    private Stage dialogStage;
    private boolean confirmed = false;
    private LoanService loanService;
    private UserDAO userDAO;
    private BookDAO bookDAO;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loanService = new LoanService();
        userDAO = new UserDAO();
        bookDAO = new BookDAO();
        
        setupControls();
        loadMembers();
        loadAvailableBooks();
        setupEventHandlers();
    }
    
    private void setupControls() {
        // Set default dates
        issueDatePicker.setValue(LocalDate.now());
        returnDatePicker.setValue(LocalDate.now().plusDays(LoanService.getDefaultLoanPeriodDays()));
        
        // Setup loan period spinner
        loanPeriodSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 90, 
            LoanService.getDefaultLoanPeriodDays()));
        
        // Setup combo box converters
        memberCombo.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user != null ? user.getFullName() + " (" + user.getEmail() + ")" : "";
            }
            
            @Override
            public User fromString(String string) {
                return null; // Not used for combo box
            }
        });
        
        bookCombo.setConverter(new StringConverter<Book>() {
            @Override
            public String toString(Book book) {
                return book != null ? book.getTitle() + " - " + book.getAuthorsString() : "";
            }
            
            @Override
            public Book fromString(String string) {
                return null; // Not used for combo box
            }
        });
    }
    
    private void setupEventHandlers() {
        // Update return date when loan period changes
        loanPeriodSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (issueDatePicker.getValue() != null) {
                returnDatePicker.setValue(issueDatePicker.getValue().plusDays(newValue));
            }
        });
        
        // Update return date when issue date changes
        issueDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                returnDatePicker.setValue(newValue.plusDays(loanPeriodSpinner.getValue()));
            }
        });
        
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
    
    private void loadAvailableBooks() {
        
        Task<List<Book>> task = new Task<List<Book>>() {
            @Override
            protected List<Book> call() throws Exception {
                return bookDAO.findByStatus("AVAILABLE");
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
                    DialogHelper.showError("Load Error", "Failed to load available books: " + getException().getMessage());
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
                    int activeLoans = loanService.getActiveLoanCount(member.getUserId());
                    int maxLoans = LoanService.getMaxLoansPerUser();
                    
                    return String.format("Active Loans: %d/%d | Phone: %s", 
                        activeLoans, maxLoans, member.getPhone());
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
            bookInfoLabel.setText(String.format("Category: %s | Pages: %d | Year: %s", 
                book.getCategoryName(), book.getPages(), book.getPublicationYear()));
        } else {
            bookInfoLabel.setText("");
        }
    }
    
    @FXML
    private void handleIssue(ActionEvent event) {
        if (!validateInput()) return;
        
        User selectedMember = memberCombo.getValue();
        Book selectedBook = bookCombo.getValue();
        LocalDate issueDate = issueDatePicker.getValue();
        LocalDate returnDate = returnDatePicker.getValue();
        
        // Confirm the loan details
        String confirmMessage = String.format(
            "Issue Loan?\n\n" +
            "Member: %s\n" +
            "Book: %s\n" +
            "Issue Date: %s\n" +
            "Return Date: %s\n" +
            "Loan Period: %d days",
            selectedMember.getFullName(),
            selectedBook.getTitle(),
            issueDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            returnDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            loanPeriodSpinner.getValue()
        );
        
        if (!DialogHelper.showConfirmation("Confirm Loan Issue", confirmMessage)) {
            return;
        }
        
        // Disable controls during processing
        issueButton.setDisable(true);
        issueButton.setText("Processing...");
        
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                User currentUser = SessionManager.getInstance().getCurrentUser();
                loanService.issueLoan(selectedMember.getUserId(), selectedBook.getBookId(), 
                    currentUser.getUserId(), loanPeriodSpinner.getValue());
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    confirmed = true;
                    DialogHelper.showSuccess("Success", "Loan issued successfully!");
                    dialogStage.close();
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    issueButton.setDisable(false);
                    issueButton.setText("Issue Loan");
                    
                    Throwable exception = getException();
                    String errorMessage = exception instanceof ServiceException ? 
                        exception.getMessage() : "An error occurred while issuing the loan.";
                    DialogHelper.showError("Issue Failed", errorMessage);
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
        
        if (issueDatePicker.getValue() == null) {
            DialogHelper.showError("Validation Error", "Please select an issue date.");
            return false;
        }
        
        if (returnDatePicker.getValue() == null) {
            DialogHelper.showError("Validation Error", "Please select a return date.");
            return false;
        }
        
        if (issueDatePicker.getValue().isAfter(returnDatePicker.getValue())) {
            DialogHelper.showError("Validation Error", "Return date must be after issue date.");
            return false;
        }
        
        if (issueDatePicker.getValue().isBefore(LocalDate.now().minusDays(1))) {
            DialogHelper.showError("Validation Error", "Issue date cannot be in the past.");
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