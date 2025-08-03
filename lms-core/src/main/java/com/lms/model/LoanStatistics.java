package com.lms.model;

public class LoanStatistics {
    private int totalLoans;
    private int activeLoans;
    private int overdueLoans;
    private int dueSoonLoans;
    private int returnedLoans;
    private int totalMembers;
    private double totalFines;
    private int booksIssued;
    
    // Constructors
    public LoanStatistics() {}
    
    // Getters and Setters
    public int getTotalLoans() { return totalLoans; }
    public void setTotalLoans(int totalLoans) { this.totalLoans = totalLoans; }
    
    public int getActiveLoans() { return activeLoans; }
    public void setActiveLoans(int activeLoans) { this.activeLoans = activeLoans; }
    
    public int getOverdueLoans() { return overdueLoans; }
    public void setOverdueLoans(int overdueLoans) { this.overdueLoans = overdueLoans; }
    
    public int getDueSoonLoans() { return dueSoonLoans; }
    public void setDueSoonLoans(int dueSoonLoans) { this.dueSoonLoans = dueSoonLoans; }
    
    public int getReturnedLoans() { return returnedLoans; }
    public void setReturnedLoans(int returnedLoans) { this.returnedLoans = returnedLoans; }
    
    public int getTotalMembers() { return totalMembers; }
    public void setTotalMembers(int totalMembers) { this.totalMembers = totalMembers; }
    
    public double getTotalFines() { return totalFines; }
    public void setTotalFines(double totalFines) { this.totalFines = totalFines; }
    
    public int getBooksIssued() { return booksIssued; }
    public void setBooksIssued(int booksIssued) { this.booksIssued = booksIssued; }
    
    // Calculated properties
    public double getReturnRate() {
        return totalLoans > 0 ? (double) returnedLoans / totalLoans * 100 : 0.0;
    }
    
    public double getOverdueRate() {
        return activeLoans > 0 ? (double) overdueLoans / activeLoans * 100 : 0.0;
    }
}
