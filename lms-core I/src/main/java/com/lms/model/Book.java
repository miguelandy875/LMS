package com.lms.model;

import java.time.LocalDate;

/**
 * Represents a book in the library system.
 */
public class Book {

    private int bookId;
    private String bookTitle;
    private int bookPages;
    private LocalDate bookPubYear;
    private boolean status;           // true = available, false = unavailable
    private Category category;        // Relationship to Category (many books belong to one category)

    public Book() {
        // Default constructor
    }

    public Book(int bookId, String bookTitle, int bookPages,
                LocalDate bookPubYear, boolean status, Category category) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookPages = bookPages;
        this.bookPubYear = bookPubYear;
        this.status = status;
        this.category = category;
    }

    // Getters and Setters
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getBookPages() {
        return bookPages;
    }

    public void setBookPages(int bookPages) {
        this.bookPages = bookPages;
    }

    public LocalDate getBookPubYear() {
        return bookPubYear;
    }

    public void setBookPubYear(LocalDate bookPubYear) {
        this.bookPubYear = bookPubYear;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}