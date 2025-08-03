package com.lms.controller.dialogs;

import com.lms.model.Loan;
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

public class LoanDetailsDialogController implements Initializable {
    
    @FXML private Label loanIdLabel;
    @FXML private Label statusBadge;
    @FXML private Label memberNameLabel;
    @FXML private Label memberEmailLabel;
    @FXML private Label bookTitleLabel;
    @FXML private Label bookAuthorsLabel;
    @FXML private Label issueDateLabel;
    @FXML private Label dueDateLabel;
    @FXML private Label daysLeftLabel;
    @FXML private Label librarianLabel;
    @FXML private VBox fineSection;
    @FXML private Label overdueLabel;
    @FXML private Label fineAmountLabel;
    @FXML private Button closeButton;
    
    private Stage dialogStage;
    private Loan loan;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initially hide fine section
        fineSection.setVisible(false);
        fineSection.setManaged(false);
    }
    
    public void setLoan(Loan loan) {
        this.loan = loan;
        populateFields();
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    private void populateFields() {
        if (loan == null) return;
        
        // Basic loan information
        loanIdLabel.setText("Loan ID: #" + loan.getLoanId());
        
        // Member information
        memberNameLabel.setText(loan.getMemberName());
        memberEmailLabel.setText(loan.getMemberEmail());
        
        // Book information
        bookTitleLabel.setText(loan.getBookTitle());
        bookAuthorsLabel.setText(loan.getBookAuthors() != null ? loan.getBookAuthors() : "Unknown Author");
        
        // Date information
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        issueDateLabel.setText(loan.getLoanIssueDate().format(formatter));
        dueDateLabel.setText(loan.getLoanReturnDate().format(formatter));
        
        // Librarian information
        librarianLabel.setText(loan.getLibrarianName());
        
        // Status and status-specific information
        String status = loan.getStatus();
        statusBadge.setText(status);
        updateStatusBadgeStyle(status);
        
        // Days left/overdue information
        if (loan.isReturned()) {
            daysLeftLabel.setText("Book returned");
            daysLeftLabel.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
        } else if (loan.isOverdue()) {
            long daysOverdue = loan.getDaysOverdueCount();
            daysLeftLabel.setText(daysOverdue + " days overdue");
            daysLeftLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
            
            // Show fine section
            showFineSection(daysOverdue, loan.getFineAmount());
        } else {
            long daysLeft = loan.getDaysUntilDue();
            daysLeftLabel.setText(daysLeft + " days remaining");
            
            if (daysLeft <= 1) {
                daysLeftLabel.setStyle("-fx-text-fill: #ffc107; -fx-font-weight: bold;");
            } else {
                daysLeftLabel.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
            }
        }
    }
    
    private void updateStatusBadgeStyle(String status) {
        statusBadge.getStyleClass().removeAll("status-badge-active", "status-badge-overdue", 
            "status-badge-due-soon", "status-badge-returned");
        
        switch (status) {
            case "ACTIVE":
                statusBadge.getStyleClass().add("status-badge-active");
                statusBadge.setStyle("-fx-background-color: #17a2b8;");
                break;
            case "OVERDUE":
                statusBadge.getStyleClass().add("status-badge-overdue");
                statusBadge.setStyle("-fx-background-color: #dc3545;");
                break;
            case "DUE_SOON":
                statusBadge.getStyleClass().add("status-badge-due-soon");
                statusBadge.setStyle("-fx-background-color: #ffc107; -fx-text-fill: #212529;");
                break;
            case "RETURNED":
                statusBadge.getStyleClass().add("status-badge-returned");
                statusBadge.setStyle("-fx-background-color: #28a745;");
                break;
            default:
                statusBadge.setStyle("-fx-background-color: #6c757d;");
                break;
        }
    }
    
    private void showFineSection(long daysOverdue, double fineAmount) {
        fineSection.setVisible(true);
        fineSection.setManaged(true);
        
        overdueLabel.setText(daysOverdue + " days");
        fineAmountLabel.setText(String.format("$%.2f", fineAmount));
    }
    
    @FXML
    private void handleClose(ActionEvent event) {
        dialogStage.close();
    }
}