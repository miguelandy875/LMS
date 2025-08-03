package com.lms.controller.modules;

import com.lms.dao.BookDAO;
import com.lms.dao.UserDAO;
import com.lms.model.Book;
import com.lms.model.Loan;
import com.lms.model.LoanStatistics;
import com.lms.model.User;
import com.lms.controller.dialogs.IssueLoanDialogController;
import com.lms.controller.dialogs.LoanDetailsDialogController;
import com.lms.service.LoanService;
import com.lms.service.ServiceException;
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
import java.util.List;
import java.util.ResourceBundle;

public class LoanController implements Initializable {
    
    // Statistics Labels
    @FXML private Label totalLoansLabel;
    @FXML private Label activeLoansLabel;
    @FXML private Label overdueLoansLabel;
    @FXML private Label dueSoonLoansLabel;
    @FXML private Label returnedLoansLabel;
    @FXML private Label totalMembersLabel;
    
    // Search and Filter Controls
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ComboBox<Integer> pageSizeCombo;
    
    // Action Buttons
    @FXML private Button clearFiltersButton;
    @FXML private Button issueLoanButton;
    @FXML private Button refreshButton;
    @FXML private Button returnBookButton;
    @FXML private Button renewLoanButton;
    @FXML private Button viewLoanButton;
    @FXML private Button deleteLoanButton;
    
    // Table and Columns
    @FXML private TableView<Loan> loansTable;
    @FXML private TableColumn<Loan, Integer> idColumn;
    @FXML private TableColumn<Loan, String> memberColumn;
    @FXML private TableColumn<Loan, String> bookColumn;
    @FXML private TableColumn<Loan, String> issueDateColumn;
    @FXML private TableColumn<Loan, String> dueDateColumn;
    @FXML private TableColumn<Loan, String> statusColumn;
    @FXML private TableColumn<Loan, String> fineColumn;
    
    // Pagination Controls
    @FXML private Button firstPageBtn;
    @FXML private Button previousPageBtn;
    @FXML private Button nextPageBtn;
    @FXML private Button lastPageBtn;
    @FXML private Label pageInfoLabel;
    @FXML private TextField goToPageField;
    @FXML private Button goToPageBtn;
    
    // Service and DAO layers
    private LoanService loanService;
    private UserDAO userDAO;
    private BookDAO bookDAO;
    
    // Data and pagination
    private ObservableList<Loan> loansList;
    private int currentPage = 1;
    private int pageSize = 25;
    private int totalRecords = 0;
    private int totalPages = 0;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeServices();
        initializeTableColumns();
        initializeControls();
        setupEventHandlers();
        loadStatistics();
        loadLoansData();
    }
    
    private void initializeServices() {
        loanService = new LoanService();
        userDAO = new UserDAO();
        bookDAO = new BookDAO();
        loansList = FXCollections.observableArrayList();
    }
    
    private void initializeTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("loanId"));
        
        memberColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getMemberName() + " (" + cellData.getValue().getMemberEmail() + ")"));
        
        bookColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getBookTitle()));
        
        issueDateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getLoanIssueDate() != null) {
                return new SimpleStringProperty(cellData.getValue().getLoanIssueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            }
            return new SimpleStringProperty("");
        });
        
        dueDateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getLoanReturnDate() != null) {
                return new SimpleStringProperty(cellData.getValue().getLoanReturnDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            }
            return new SimpleStringProperty("");
        });
        
        statusColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatusDisplayText()));
        
        fineColumn.setCellValueFactory(cellData -> {
            double fine = cellData.getValue().getFineAmount();
            return new SimpleStringProperty(fine > 0 ? String.format("$%.2f", fine) : "-");
        });
        
        // Set custom row factory for status-based styling
        loansTable.setRowFactory(tv -> {
            TableRow<Loan> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldLoan, newLoan) -> {
                if (newLoan == null) {
                    row.getStyleClass().removeAll("loan-returned", "loan-active", "loan-overdue", "loan-due-soon");
                } else {
                    row.getStyleClass().removeAll("loan-returned", "loan-active", "loan-overdue", "loan-due-soon");
                    switch (newLoan.getStatus()) {
                        case "RETURNED":
                            row.getStyleClass().add("loan-returned");
                            break;
                        case "OVERDUE":
                            row.getStyleClass().add("loan-overdue");
                            break;
                        case "DUE_SOON":
                            row.getStyleClass().add("loan-due-soon");
                            break;
                        case "ACTIVE":
                            row.getStyleClass().add("loan-active");
                            break;
                    }
                }
            });
            return row;
        });
        
        loansTable.setItems(loansList);
    }
    
    private void initializeControls() {
        // Initialize status filter
        statusFilter.setItems(FXCollections.observableArrayList(
            "ALL", "ACTIVE", "OVERDUE", "DUE_SOON", "RETURNED"));
        statusFilter.setValue("ALL");
        
        // Initialize page size combo
        pageSizeCombo.setItems(FXCollections.observableArrayList(10, 25, 50, 100));
        pageSizeCombo.setValue(25);
        
        // Initially disable action buttons
        updateActionButtonStates();
    }
    
    private void setupEventHandlers() {
        // Table selection handler
        loansTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> updateActionButtonStates());
        
        // Double-click to view loan details
        loansTable.setRowFactory(tv -> {
            TableRow<Loan> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    handleViewLoan(null);
                }
            });
            return row;
        });
        
        // Real-time search
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            currentPage = 1;
            loadLoansData();
        });
        
        // Filter change handlers
        statusFilter.valueProperty().addListener((obs, oldValue, newValue) -> {
            currentPage = 1;
            loadLoansData();
        });
        
        pageSizeCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            pageSize = newValue;
            currentPage = 1;
            loadLoansData();
        });
        
        // Pagination handlers
        firstPageBtn.setOnAction(e -> goToPage(1));
        previousPageBtn.setOnAction(e -> goToPage(currentPage - 1));
        nextPageBtn.setOnAction(e -> goToPage(currentPage + 1));
        lastPageBtn.setOnAction(e -> goToPage(totalPages));
        goToPageBtn.setOnAction(e -> {
            try {
                int page = Integer.parseInt(goToPageField.getText());
                goToPage(page);
            } catch (NumberFormatException ex) {
                DialogHelper.showError("Invalid Page", "Please enter a valid page number.");
            }
        });
    }
    
    private void updateActionButtonStates() {
        Loan selectedLoan = loansTable.getSelectionModel().getSelectedItem();
        boolean hasSelection = selectedLoan != null;
        boolean isActive = hasSelection && !selectedLoan.isReturned();
        
        viewLoanButton.setDisable(!hasSelection);
        returnBookButton.setDisable(!isActive);
        renewLoanButton.setDisable(!isActive || selectedLoan.isOverdue());
        deleteLoanButton.setDisable(!hasSelection);
    }
    
    @FXML
    private void handleIssueLoan(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/issue_loan_dialog.fxml"));
            Parent root = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Issue New Loan");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(issueLoanButton.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            
            IssueLoanDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            
            dialogStage.showAndWait();
            
            if (controller.isConfirmed()) {
                // Refresh data after successful loan issue
                loadStatistics();
                loadLoansData();
            }
        } catch (IOException e) {
            DialogHelper.showError("Error", "Could not open Issue Loan dialog: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleReturnBook(ActionEvent event) {
        Loan selectedLoan = loansTable.getSelectionModel().getSelectedItem();
        if (selectedLoan == null) return;
        
        if (selectedLoan.isReturned()) {
            DialogHelper.showWarning("Book Already Returned", "This book has already been returned.");
            return;
        }
        
        String message = String.format(
            "Return Book?\n\n" +
            "Book: %s\n" +
            "Member: %s\n" +
            "Issue Date: %s\n" +
            "Due Date: %s\n" +
            "Status: %s",
            selectedLoan.getBookTitle(),
            selectedLoan.getMemberName(),
            selectedLoan.getLoanIssueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            selectedLoan.getLoanReturnDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            selectedLoan.getStatusDisplayText()
        );
        
        if (selectedLoan.getFineAmount() > 0) {
            message += String.format("\n\nFine Amount: $%.2f", selectedLoan.getFineAmount());
        }
        
        if (DialogHelper.showConfirmation("Return Book", message)) {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    User currentUser = SessionManager.getInstance().getCurrentUser();
                    loanService.returnBook(selectedLoan.getLoanId(), currentUser.getUserId());
                    return null;
                }
                
                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        DialogHelper.showSuccess("Success", "Book returned successfully!");
                        loadStatistics();
                        loadLoansData();
                    });
                }
                
                @Override
                protected void failed() {
                    Platform.runLater(() -> {
                        Throwable exception = getException();
                        String errorMessage = exception instanceof ServiceException ? 
                            exception.getMessage() : "An error occurred while returning the book.";
                        DialogHelper.showError("Return Failed", errorMessage);
                    });
                }
            };
            
            new Thread(task).start();
        }
    }
    
    @FXML
    private void handleRenewLoan(ActionEvent event) {
        Loan selectedLoan = loansTable.getSelectionModel().getSelectedItem();
        if (selectedLoan == null) return;
        
        if (selectedLoan.isReturned()) {
            DialogHelper.showWarning("Cannot Renew", "Cannot renew a returned book.");
            return;
        }
        
        if (selectedLoan.isOverdue()) {
            DialogHelper.showWarning("Cannot Renew", "Cannot renew an overdue book. Please return the book first.");
            return;
        }
        
        String renewalDays = DialogHelper.showInputDialog("Renew Loan", 
            "Enter number of additional days:", "14");
        
        if (renewalDays != null && !renewalDays.trim().isEmpty()) {
            try {
                int additionalDays = Integer.parseInt(renewalDays.trim());
                if (additionalDays <= 0 || additionalDays > 30) {
                    DialogHelper.showError("Invalid Input", "Please enter a number between 1 and 30.");
                    return;
                }
                
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        loanService.renewLoan(selectedLoan.getLoanId(), additionalDays);
                        return null;
                    }
                    
                    @Override
                    protected void succeeded() {
                        Platform.runLater(() -> {
                            DialogHelper.showSuccess("Success", 
                                String.format("Loan renewed for %d additional days!", additionalDays));
                            loadLoansData();
                        });
                    }
                    
                    @Override
                    protected void failed() {
                        Platform.runLater(() -> {
                            Throwable exception = getException();
                            String errorMessage = exception instanceof ServiceException ? 
                                exception.getMessage() : "An error occurred while renewing the loan.";
                            DialogHelper.showError("Renewal Failed", errorMessage);
                        });
                    }
                };
                
                new Thread(task).start();
                
            } catch (NumberFormatException e) {
                DialogHelper.showError("Invalid Input", "Please enter a valid number of days.");
            }
        }
    }
    
    @FXML
    private void handleViewLoan(ActionEvent event) {
        Loan selectedLoan = loansTable.getSelectionModel().getSelectedItem();
        if (selectedLoan == null) return;
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/loan_details_dialog.fxml"));
            Parent root = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Loan Details");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(viewLoanButton.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            
            LoanDetailsDialogController controller = loader.getController();
            controller.setLoan(selectedLoan);
            controller.setDialogStage(dialogStage);
            
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            DialogHelper.showError("Error", "Could not open Loan Details dialog: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleDeleteLoan(ActionEvent event) {
        Loan selectedLoan = loansTable.getSelectionModel().getSelectedItem();
        if (selectedLoan == null) return;
        
        // Only allow deletion of returned loans for data cleanup
        if (!selectedLoan.isReturned()) {
            DialogHelper.showWarning("Cannot Delete", 
                "Cannot delete an active loan. Please return the book first.");
            return;
        }
        
        String message = String.format(
            "Delete Loan Record?\n\n" +
            "This will permanently delete the loan record:\n\n" +
            "Book: %s\n" +
            "Member: %s\n" +
            "Issue Date: %s\n" +
            "Return Date: %s\n\n" +
            "This action cannot be undone.",
            selectedLoan.getBookTitle(),
            selectedLoan.getMemberName(),
            selectedLoan.getLoanIssueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            selectedLoan.getLoanReturnDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        );
        
        if (DialogHelper.showConfirmation("Delete Loan Record", message)) {
            try {
                loanService.deleteLoan(selectedLoan.getLoanId());
                DialogHelper.showSuccess("Success", "Loan record deleted successfully.");
                loadStatistics();
                loadLoansData();
            } catch (Exception e) {
                DialogHelper.showError("Delete Failed", 
                    "Could not delete loan record: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleClearFilters(ActionEvent event) {
        searchField.clear();
        statusFilter.setValue("ALL");
        currentPage = 1;
        loadLoansData();
    }
    
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadStatistics();
        loadLoansData();
    }
    
    private void loadStatistics() {
        Task<LoanStatistics> task = new Task<LoanStatistics>() {
            @Override
            protected LoanStatistics call() throws Exception {
                return loanService.getStatistics();
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> updateStatisticsUI(getValue()));
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.err.println("Failed to load loan statistics: " + getException().getMessage());
                });
            }
        };
        
        new Thread(task).start();
    }
    
    private void updateStatisticsUI(LoanStatistics stats) {
        totalLoansLabel.setText(String.valueOf(stats.getTotalLoans()));
        activeLoansLabel.setText(String.valueOf(stats.getActiveLoans()));
        overdueLoansLabel.setText(String.valueOf(stats.getOverdueLoans()));
        dueSoonLoansLabel.setText(String.valueOf(stats.getDueSoonLoans()));
        returnedLoansLabel.setText(String.valueOf(stats.getReturnedLoans()));
        totalMembersLabel.setText(String.valueOf(stats.getTotalMembers()));
    }
    
    private void loadLoansData() {
        Task<List<Loan>> task = new Task<List<Loan>>() {
            @Override
            protected List<Loan> call() throws Exception {
                String searchTerm = searchField.getText();
                String status = statusFilter.getValue();
                int offset = (currentPage - 1) * pageSize;
                
                // Get total count for pagination
                totalRecords = loanService.countSearchResults(searchTerm, status);
                totalPages = (int) Math.ceil((double) totalRecords / pageSize);
                
                return loanService.searchLoans(searchTerm, status, pageSize, offset);
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    loansList.clear();
                    loansList.addAll(getValue());
                    updatePaginationUI();
                    updateActionButtonStates();
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    DialogHelper.showError("Load Error", 
                        "Failed to load loans data: " + getException().getMessage());
                });
            }
        };
        
        new Thread(task).start();
    }
    
    private void updatePaginationUI() {
        // Update pagination buttons
        firstPageBtn.setDisable(currentPage <= 1);
        previousPageBtn.setDisable(currentPage <= 1);
        nextPageBtn.setDisable(currentPage >= totalPages);
        lastPageBtn.setDisable(currentPage >= totalPages);
        
        // Update page info
        int startRecord = totalRecords > 0 ? (currentPage - 1) * pageSize + 1 : 0;
        int endRecord = Math.min(currentPage * pageSize, totalRecords);
        
        pageInfoLabel.setText(String.format("Page %d of %d (%d-%d of %d records)",
            currentPage, Math.max(totalPages, 1), startRecord, endRecord, totalRecords));
        
        goToPageField.setPromptText(String.valueOf(currentPage));
    }
    
    private void goToPage(int page) {
        if (page >= 1 && page <= totalPages) {
            currentPage = page;
            loadLoansData();
        }
    }
}