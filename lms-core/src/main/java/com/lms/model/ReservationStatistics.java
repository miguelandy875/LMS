package com.lms.model;

public class ReservationStatistics {
    // Book reservation stats
    private int totalBookReservations;
    private int activeBookReservations;
    private int expiredBookReservations;
    private int fulfilledBookReservations;
    
    // Seat reservation stats
    private int totalSeatReservations;
    private int activeSeatReservations;
    private int ongoingSeatReservations;
    private int upcomingSeatReservations;
    
    // Seat availability
    private int totalSeats;
    private int availableSeats;
    private int occupiedSeats;
    private int maintenanceSeats;
    
    // Constructors
    public ReservationStatistics() {}
    
    // Getters and Setters
    public int getTotalBookReservations() { return totalBookReservations; }
    public void setTotalBookReservations(int totalBookReservations) { this.totalBookReservations = totalBookReservations; }
    
    public int getActiveBookReservations() { return activeBookReservations; }
    public void setActiveBookReservations(int activeBookReservations) { this.activeBookReservations = activeBookReservations; }
    
    public int getExpiredBookReservations() { return expiredBookReservations; }
    public void setExpiredBookReservations(int expiredBookReservations) { this.expiredBookReservations = expiredBookReservations; }
    
    public int getFulfilledBookReservations() { return fulfilledBookReservations; }
    public void setFulfilledBookReservations(int fulfilledBookReservations) { this.fulfilledBookReservations = fulfilledBookReservations; }
    
    public int getTotalSeatReservations() { return totalSeatReservations; }
    public void setTotalSeatReservations(int totalSeatReservations) { this.totalSeatReservations = totalSeatReservations; }
    
    public int getActiveSeatReservations() { return activeSeatReservations; }
    public void setActiveSeatReservations(int activeSeatReservations) { this.activeSeatReservations = activeSeatReservations; }
    
    public int getOngoingSeatReservations() { return ongoingSeatReservations; }
    public void setOngoingSeatReservations(int ongoingSeatReservations) { this.ongoingSeatReservations = ongoingSeatReservations; }
    
    public int getUpcomingSeatReservations() { return upcomingSeatReservations; }
    public void setUpcomingSeatReservations(int upcomingSeatReservations) { this.upcomingSeatReservations = upcomingSeatReservations; }
    
    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
    
    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    
    public int getOccupiedSeats() { return occupiedSeats; }
    public void setOccupiedSeats(int occupiedSeats) { this.occupiedSeats = occupiedSeats; }
    
    public int getMaintenanceSeats() { return maintenanceSeats; }
    public void setMaintenanceSeats(int maintenanceSeats) { this.maintenanceSeats = maintenanceSeats; }
    
    // Calculated properties
    public double getSeatOccupancyRate() {
        return totalSeats > 0 ? (double) occupiedSeats / totalSeats * 100 : 0.0;
    }
    
    public double getBookReservationFulfillmentRate() {
        return totalBookReservations > 0 ? (double) fulfilledBookReservations / totalBookReservations * 100 : 0.0;
    }
}