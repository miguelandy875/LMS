package com.lms.testdao;

import com.lms.dao.SeatDAO;
import com.lms.model.Seat;

import java.util.List;

public class TestSeat {
    public static void main(String[] args) {
        SeatDAO dao = new SeatDAO();

        Seat seat = new Seat(1, "A-01", "desk", "free");
        dao.insert(seat);

        // seat.setSeatStatus("free");
        // dao.update(seat);

        Seat retrieved = dao.findById(1);
        System.out.println("Seat ID 1: " + retrieved.getSeatId());

        List<Seat> allSeats = dao.findAll();
        System.out.println("Total Seats: " + allSeats.size());

    
        
    }
}