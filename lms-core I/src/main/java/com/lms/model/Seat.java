package com.lms.model;

/**
 * Represents a Seat in the library
 */
public class Seat {

    // === Fields ===
    private int seatId;
    private String seatLocation;
    private String seatType; // e.g., "Desk", "Study Room", "Computer Station"
    private String seatStatus; // e.g., public enum SeatStatus { AVAILABLE, RESERVED, OCCUPIED }
    // isAvailable() or markAsReserved()
    // === Constructors ===

    public Seat() {
        // Default constructor
    }

    public Seat(int seatId, String seatLocation, String seatType, String seatStatus) {
        this.seatId = seatId;
        this.seatLocation = seatLocation;
        this.seatType = seatType;
        this.seatStatus = seatStatus;
    }

    // === Getters and Setters ===

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public String getSeatLocation() {
        return seatLocation;
    }

    public void setSeatLocation(String seatLocation) {
        this.seatLocation = seatLocation;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public String getSeatStatus() {
        return seatStatus;
    }

    public void setSeatStatus(String seatStatus) {
        this.seatStatus = seatStatus;
    }

    @Override
    public String toString() {
        return "[ID =" + seatId + ", Location =" + seatLocation + ", Types =" + seatType + ", Status=" + seatStatus +"]";
    }
}