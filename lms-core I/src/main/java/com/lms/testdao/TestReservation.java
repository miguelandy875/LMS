package com.lms.testdao;

import com.lms.dao.ReservationDAO;
import com.lms.model.Member;
import com.lms.model.Reservation;
import com.lms.model.Seat;
import com.lms.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class TestReservation {
    public static void main(String[] args) {

        Seat seat = new Seat(); // Assuming Member is a subclass of User
        seat.setSeatId(1);

        User borrower = new Member(); // Assuming Member is a subclass of User
        borrower.setUserId(1); // Borrower ID

        ReservationDAO dao = new ReservationDAO();

        Reservation reservation = new Reservation(seat, borrower, LocalDateTime.now(), LocalDateTime.now().plusHours(3), "Pending");
        dao.insert(reservation);

        reservation.setReserveEnd(LocalDateTime.now().plusHours(4));
        dao.update(reservation);

        Reservation found = dao.findById(1);
        System.out.println("Seat " + found.getSeat() + " reserved by user " + found.getMember());

        List<Reservation> all = dao.findAll();
        System.out.println("Total Reservations: " + all.size());

        
    }
}