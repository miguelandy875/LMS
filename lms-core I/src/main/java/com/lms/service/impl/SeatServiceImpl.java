package com.lms.service.impl;

import com.lms.dao.SeatDAO;

import com.lms.model.Seat;
import com.lms.service.SeatService;

import java.util.List;

public class SeatServiceImpl implements SeatService {

    private final SeatDAO seatDAO;

    public SeatServiceImpl() {
        this.seatDAO = new SeatDAO();
    }

    @Override
    public void addSeat(Seat seat) {
        seatDAO.insert(seat);
    }

    @Override
    public void updateSeat(Seat seat) {
        seatDAO.update(seat);
    }

    @Override
    public void deleteSeat(int seatId) {
        seatDAO.delete(seatId);
    }

    @Override
    public Seat findSeatById(int seatId) {
        return seatDAO.findById(seatId);
    }

    @Override
    public List<Seat> findAllSeats() {
        return seatDAO.findAll();
    }
}