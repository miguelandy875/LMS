package com.lms.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class SeatReservation {
    private int seatId;
    private int userId;
    private LocalDateTime reserveStart;
    private LocalDateTime reserveEnd;
    private String status; // ACTIVE, COMPLETED, CANCELLED, NO_SHOW
    
    // Display fields
    private String seatLocation;
    private String seatType;
    private String memberName;
    private String memberEmail;
    
    // Constructors
    public SeatReservation() {}
    
    public SeatReservation(int seatId, int userId, LocalDateTime reserveStart, LocalDateTime reserveEnd) {
        this.seatId = seatId;
        this.userId = userId;
        this.reserveStart = reserveStart;
        this.reserveEnd = reserveEnd;
        this.status = "ACTIVE";
    }
    
    // Getters and Setters
    public int getSeatId() { return seatId; }
    public void setSeatId(int seatId) { this.seatId = seatId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public LocalDateTime getReserveStart() { return reserveStart; }
    public void setReserveStart(LocalDateTime reserveStart) { this.reserveStart = reserveStart; }
    
    public LocalDateTime getReserveEnd() { return reserveEnd; }
    public void setReserveEnd(LocalDateTime reserveEnd) { this.reserveEnd = reserveEnd; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // Display fields
    public String getSeatLocation() { return seatLocation; }
    public void setSeatLocation(String seatLocation) { this.seatLocation = seatLocation; }
    
    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }
    
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    
    public String getMemberEmail() { return memberEmail; }
    public void setMemberEmail(String memberEmail) { this.memberEmail = memberEmail; }
    
    // Business logic methods
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    public boolean isOngoing() {
        LocalDateTime now = LocalDateTime.now();
        return isActive() && now.isAfter(reserveStart) && now.isBefore(reserveEnd);
    }
    
    public boolean isUpcoming() {
        return isActive() && LocalDateTime.now().isBefore(reserveStart);
    }
    
    public boolean isPast() {
        return LocalDateTime.now().isAfter(reserveEnd);
    }
    
    public long getDurationMinutes() {
        return ChronoUnit.MINUTES.between(reserveStart, reserveEnd);
    }
    
    public long getMinutesUntilStart() {
        if (!isUpcoming()) return 0;
        return ChronoUnit.MINUTES.between(LocalDateTime.now(), reserveStart);
    }
    
    public long getMinutesRemaining() {
        if (!isOngoing()) return 0;
        return ChronoUnit.MINUTES.between(LocalDateTime.now(), reserveEnd);
    }
    
    public String getTimeRange() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm");
        return reserveStart.format(formatter) + " - " + reserveEnd.format(formatter);
    }
    
    public String getStatusDisplayText() {
        switch (status) {
            case "ACTIVE":
                if (isOngoing()) {
                    return "Ongoing (" + getMinutesRemaining() + " min left)";
                } else if (isUpcoming()) {
                    return "Upcoming (" + getMinutesUntilStart() + " min)";
                } else {
                    return "Past";
                }
            case "COMPLETED":
                return "Completed";
            case "CANCELLED":
                return "Cancelled";
            case "NO_SHOW":
                return "No Show";
            default:
                return status;
        }
    }
}