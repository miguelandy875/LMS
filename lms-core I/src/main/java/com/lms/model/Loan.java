package com.lms.model;

import java.time.LocalDate;

/**
 * Loan class represents a book being loaned to a member by a librarian.
 * It is an association class that connects Book, Member, and Librarian (all Users).
 */
public class Loan {

    // === Fields ===
    private int loanId;
    private Book book;
    private User borrower;         // the one who borrows
    private User issuer;        // the librarian who issued it
    private LocalDate issueDate;
    private LocalDate returnDate;
    private boolean returned;

    // === Constructor ===
    public Loan() {
        // Default constructor
    }
    // Constructor for creating a new loan
    public Loan(int loanId, Book book, User borrower, User issuer,
                LocalDate issueDate, LocalDate returnDate, boolean returned) {
        this.loanId = loanId;
        this.book = book;
        this.borrower = borrower;
        this.issuer = issuer;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.returned = returned;
    }
    // Removed invalid constructor declaration

    // === Getters and Setters ===
    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getBorrower() {
        return borrower;
    }

    public void setBorrower(Member borrower) {
        this.borrower = borrower;
    }

    public User getIssuer() {
        return issuer;
    }

    public void setIssuer(Librarian issuer) {
        this.issuer = issuer;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    // === Business Methods ===

    public boolean isOverdue() {
        return !returned && returnDate.isBefore(LocalDate.now());
    }

    public int daysRemaining() {
        return returned ? 0 : (int) LocalDate.now().until(returnDate).getDays();
    }
}