package com.lms.model;

public class Seat {
    private int seatId;
    private String seatLocation;
    private String seatType; // STUDY, COMPUTER, GROUP, SILENT
    private String seatStatus; // AVAILABLE, OCCUPIED, MAINTENANCE, RESERVED
    
    // Constructors
    public Seat() {}
    
    public Seat(String seatLocation, String seatType, String seatStatus) {
        this.seatLocation = seatLocation;
        this.seatType = seatType;
        this.seatStatus = seatStatus;
    }
    
    // Getters and Setters
    public int getSeatId() { return seatId; }
    public void setSeatId(int seatId) { this.seatId = seatId; }
    
    public String getSeatLocation() { return seatLocation; }
    public void setSeatLocation(String seatLocation) { this.seatLocation = seatLocation; }
    
    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }
    
    public String getSeatStatus() { return seatStatus; }
    public void setSeatStatus(String seatStatus) { this.seatStatus = seatStatus; }
    
    public boolean isAvailable() {
        return "AVAILABLE".equals(seatStatus);
    }
    
    public String getDisplayName() {
        return seatLocation + " (" + seatType + ")";
    }
    
    @Override
    public String toString() {
        return getDisplayName() + " - " + seatStatus;
    }
}