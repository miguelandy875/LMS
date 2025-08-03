package com.lms.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Loan {
    private int loanId;
    private int userId;          // Member who borrowed the book
    private int bookId;          // Book that was borrowed
    private int userIdIssue;     // Librarian who issued the loan
    private LocalDate loanIssueDate;
    private LocalDate loanReturnDate;
    private boolean returned;
    
    // Additional fields for display purposes (not in database)
    private String memberName;
    private String memberEmail;
    private String bookTitle;
    private String bookAuthors;
    private String librarianName;
    private int daysOverdue;
    private double fineAmount;
    
    // Constructors
    public Loan() {}
    
    public Loan(int userId, int bookId, int userIdIssue, LocalDate loanIssueDate, LocalDate loanReturnDate) {
        this.userId = userId;
        this.bookId = bookId;
        this.userIdIssue = userIdIssue;
        this.loanIssueDate = loanIssueDate;
        this.loanReturnDate = loanReturnDate;
        this.returned = false;
    }
    
    // Getters and Setters
    public int getLoanId() { return loanId; }
    public void setLoanId(int loanId) { this.loanId = loanId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    
    public int getUserIdIssue() { return userIdIssue; }
    public void setUserIdIssue(int userIdIssue) { this.userIdIssue = userIdIssue; }
    
    public LocalDate getLoanIssueDate() { return loanIssueDate; }
    public void setLoanIssueDate(LocalDate loanIssueDate) { this.loanIssueDate = loanIssueDate; }
    
    public LocalDate getLoanReturnDate() { return loanReturnDate; }
    public void setLoanReturnDate(LocalDate loanReturnDate) { this.loanReturnDate = loanReturnDate; }
    
    public boolean isReturned() { return returned; }
    public void setReturned(boolean returned) { this.returned = returned; }
    
    // Display fields
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    
    public String getMemberEmail() { return memberEmail; }
    public void setMemberEmail(String memberEmail) { this.memberEmail = memberEmail; }
    
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    
    public String getBookAuthors() { return bookAuthors; }
    public void setBookAuthors(String bookAuthors) { this.bookAuthors = bookAuthors; }
    
    public String getLibrarianName() { return librarianName; }
    public void setLibrarianName(String librarianName) { this.librarianName = librarianName; }
    
    public int getDaysOverdue() { return daysOverdue; }
    public void setDaysOverdue(int daysOverdue) { this.daysOverdue = daysOverdue; }
    
    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }
    
    // Business logic methods
    public boolean isOverdue() {
        if (returned) return false;
        return LocalDate.now().isAfter(loanReturnDate);
    }
    
    public long getDaysUntilDue() {
        if (returned) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), loanReturnDate);
    }
    
    public long getDaysOverdueCount() {
        if (!isOverdue()) return 0;
        return ChronoUnit.DAYS.between(loanReturnDate, LocalDate.now());
    }
    
    public double calculateFine(double finePerDay) {
        long daysOverdue = getDaysOverdueCount();
        return daysOverdue > 0 ? daysOverdue * finePerDay : 0.0;
    }
    
    public String getStatus() {
        if (returned) {
            return "RETURNED";
        } else if (isOverdue()) {
            return "OVERDUE";
        } else if (getDaysUntilDue() <= 1) {
            return "DUE_SOON";
        } else {
            return "ACTIVE";
        }
    }
    
    public String getStatusDisplayText() {
        switch (getStatus()) {
            case "RETURNED": return "Returned";
            case "OVERDUE": return "Overdue (" + getDaysOverdueCount() + " days)";
            case "DUE_SOON": return "Due Soon";
            case "ACTIVE": return "Active (" + getDaysUntilDue() + " days left)";
            default: return "Unknown";
        }
    }
    
    @Override
    public String toString() {
        return "Loan{" +
                "loanId=" + loanId +
                ", userId=" + userId +
                ", bookId=" + bookId +
                ", memberName='" + memberName + '\'' +
                ", bookTitle='" + bookTitle + '\'' +
                ", loanIssueDate=" + loanIssueDate +
                ", loanReturnDate=" + loanReturnDate +
                ", returned=" + returned +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}