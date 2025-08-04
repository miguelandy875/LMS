package com.lms.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Reservation {
    private int reservationId;
    private int userId;
    private int bookId;
    private LocalDateTime reservationDate;
    private LocalDateTime expiryDate;
    private String status; // ACTIVE, FULFILLED, EXPIRED, CANCELLED
    
    // Display fields (populated from joins)
    private String memberName;
    private String memberEmail;
    private String bookTitle;
    private String bookAuthors;
    private int queuePosition;
    
    // Constructors
    public Reservation() {}
    
    public Reservation(int userId, int bookId, LocalDateTime reservationDate, LocalDateTime expiryDate) {
        this.userId = userId;
        this.bookId = bookId;
        this.reservationDate = reservationDate;
        this.expiryDate = expiryDate;
        this.status = "ACTIVE";
    }
    
    // Getters and Setters
    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    
    public LocalDateTime getReservationDate() { return reservationDate; }
    public void setReservationDate(LocalDateTime reservationDate) { this.reservationDate = reservationDate; }
    
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // Display fields
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    
    public String getMemberEmail() { return memberEmail; }
    public void setMemberEmail(String memberEmail) { this.memberEmail = memberEmail; }
    
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    
    public String getBookAuthors() { return bookAuthors; }
    public void setBookAuthors(String bookAuthors) { this.bookAuthors = bookAuthors; }
    
    public int getQueuePosition() { return queuePosition; }
    public void setQueuePosition(int queuePosition) { this.queuePosition = queuePosition; }
    
    // Business logic methods
    public boolean isActive() {
        return "ACTIVE".equals(status) && !isExpired();
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
    
    public long getHoursUntilExpiry() {
        if (isExpired()) return 0;
        return ChronoUnit.HOURS.between(LocalDateTime.now(), expiryDate);
    }
    
    public String getStatusDisplayText() {
        switch (status) {
            case "ACTIVE":
                if (isExpired()) return "Expired";
                long hours = getHoursUntilExpiry();
                return hours > 24 ? "Active" : "Expires in " + hours + "h";
            case "FULFILLED":
                return "Fulfilled";
            case "EXPIRED":
                return "Expired";
            case "CANCELLED":
                return "Cancelled";
            default:
                return status;
        }
    }
}