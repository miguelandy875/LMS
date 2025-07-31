package com.lms.service;

import com.lms.model.Reservation;
import java.util.List;

public interface ReservationService {
    void addReservation(Reservation reservation);
    void updateReservation(Reservation reservation);
    void deleteReservation(int reservationId);
    Reservation findReservationById(int reservationId);
    List<Reservation> findAllReservations();
}
