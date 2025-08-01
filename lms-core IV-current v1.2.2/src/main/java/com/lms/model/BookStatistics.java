package com.lms.model;

public class BookStatistics {
    private int totalBooks;
    private int availableBooks;
    private int issuedBooks;
    private int reservedBooks;
    private int totalAuthors;
    private int totalCategories;
    
    // Constructors
    public BookStatistics() {}
    
    public BookStatistics(int totalBooks, int availableBooks, int issuedBooks, 
                         int reservedBooks, int totalAuthors, int totalCategories) {
        this.totalBooks = totalBooks;
        this.availableBooks = availableBooks;
        this.issuedBooks = issuedBooks;
        this.reservedBooks = reservedBooks;
        this.totalAuthors = totalAuthors;
        this.totalCategories = totalCategories;
    }
    
    // Getters and Setters
    public int getTotalBooks() { return totalBooks; }
    public void setTotalBooks(int totalBooks) { this.totalBooks = totalBooks; }
    
    public int getAvailableBooks() { return availableBooks; }
    public void setAvailableBooks(int availableBooks) { this.availableBooks = availableBooks; }
    
    public int getIssuedBooks() { return issuedBooks; }
    public void setIssuedBooks(int issuedBooks) { this.issuedBooks = issuedBooks; }
    
    public int getReservedBooks() { return reservedBooks; }
    public void setReservedBooks(int reservedBooks) { this.reservedBooks = reservedBooks; }
    
    public int getTotalAuthors() { return totalAuthors; }
    public void setTotalAuthors(int totalAuthors) { this.totalAuthors = totalAuthors; }
    
    public int getTotalCategories() { return totalCategories; }
    public void setTotalCategories(int totalCategories) { this.totalCategories = totalCategories; }
    
    public double getAvailabilityPercentage() {
        return totalBooks > 0 ? (double) availableBooks / totalBooks * 100 : 0;
    }
    
    public double getIssuedPercentage() {
        return totalBooks > 0 ? (double) issuedBooks / totalBooks * 100 : 0;
    }
    
    @Override
    public String toString() {
        return "BookStatistics{" +
                "totalBooks=" + totalBooks +
                ", availableBooks=" + availableBooks +
                ", issuedBooks=" + issuedBooks +
                ", reservedBooks=" + reservedBooks +
                ", totalAuthors=" + totalAuthors +
                ", totalCategories=" + totalCategories +
                '}';
    }
}