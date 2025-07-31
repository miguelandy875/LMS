package com.lms.service;

import com.lms.model.Seat;
import java.util.List;

public interface SeatService {
    void addSeat(Seat seat);
    void updateSeat(Seat seat);
    void deleteSeat(int seatId);
    Seat findSeatById(int seatId);
    List<Seat> findAllSeats();
}