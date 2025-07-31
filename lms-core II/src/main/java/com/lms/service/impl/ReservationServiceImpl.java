package com.lms.service.impl;

import com.lms.dao.ReservationDAO;

import com.lms.model.Reservation;
import com.lms.service.ReservationService;

import java.util.List;

public class ReservationServiceImpl implements ReservationService {

    private final ReservationDAO reservationDAO;

    public ReservationServiceImpl() {
        this.reservationDAO = new ReservationDAO();
    }

    @Override
    public void addReservation(Reservation reservation) {
        reservationDAO.insert(reservation);
    }

    @Override
    public void updateReservation(Reservation reservation) {
        reservationDAO.update(reservation);
    }

    @Override
    public void deleteReservation(int reservationId) {
        reservationDAO.delete(reservationId);
    }

    @Override
    public Reservation findReservationById(int reservationId) {
        return reservationDAO.findById(reservationId);
    }

    @Override
    public List<Reservation> findAllReservations() {
        return reservationDAO.findAll();
    }
}
