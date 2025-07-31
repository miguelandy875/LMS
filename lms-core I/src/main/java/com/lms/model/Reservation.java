package com.lms.model;

import java.time.LocalDateTime;

/**
 * Represents a Reservation - an association between a Seat and a Member
 */
public class Reservation {

    // === Fields ===
    private Seat seat;            // Association with Seat
    private User member;        // Association with Member (inherits from User)
    private LocalDateTime reserveDue;
    private LocalDateTime reserveEnd;
    private String status;

    // === Constructors ===

    public Reservation() {
        // Default constructor
    }

    public Reservation(Seat seat, User member, LocalDateTime reserveDue,
                       LocalDateTime reserveEnd, String status) {
        
        this.seat = seat;
        this.member = member;
        this.reserveDue = reserveDue;
        this.reserveEnd = reserveEnd;
        this.status = status;
    }

    // === Getters and Setters ===

  

 

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public User getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public LocalDateTime getReserveDue() {
        return reserveDue;
    }

    public void setReserveDue(LocalDateTime reserveDue) {
        this.reserveDue = reserveDue;
    }

    public LocalDateTime getReserveEnd() {
        return reserveEnd;
    }

    public void setReserveEnd(LocalDateTime reserveEnd) {
        this.reserveEnd = reserveEnd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
	// turn it into an enum like PENDING, ACTIVE, EXPIRED
        this.status = status;
    }
}