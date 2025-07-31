package com.lms.dao;

import com.lms.model.Reservation;
import com.lms.model.Seat;
import com.lms.model.User;
import com.lms.utils.DatabaseUtil;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO implements GenericDAO<Reservation> {

    private final SeatDAO seatDAO = new SeatDAO();
    private final UserDAO memberDAO = new UserDAO();

    @Override
    public boolean insert(Reservation reservation) {
        String sql = "INSERT INTO reserve (seat_id, user_id, reserve_due, reserve_end, status) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservation.getSeat().getSeatId());
            stmt.setInt(2, reservation.getMember().getUserId());
            stmt.setTimestamp(3, Timestamp.valueOf(reservation.getReserveDue()));
            stmt.setTimestamp(4, Timestamp.valueOf(reservation.getReserveEnd()));
            stmt.setString(5, reservation.getStatus());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Reservation reservation) {
        String sql = "UPDATE reserve SET user_id=?, reserve_due=?, reserve_end=?, status=? WHERE seat_id=?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, reservation.getSeat().getSeatId());
                stmt.setInt(2, reservation.getMember().getUserId());
                stmt.setTimestamp(3, Timestamp.valueOf(reservation.getReserveDue()));
                stmt.setTimestamp(4, Timestamp.valueOf(reservation.getReserveEnd()));
                stmt.setString(5, reservation.getStatus());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM reserve WHERE seat_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Reservation findById(int id) {
        String sql = "SELECT * FROM reserve WHERE seat_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Integer seatId = rs.getObject("seat_id")!= null ? rs.getInt("seat_id") : null;
                Integer memberId = rs.getObject("user_id")!= null ? rs.getInt("user_id") : null;


                Seat seat = seatId != null ? seatDAO.findById(seatId) : null;
                User member = memberId != null ? memberDAO.findById(memberId) : null;

                return new Reservation(
                    seat,
                    member,
                    rs.getTimestamp("reserve_due").toLocalDateTime(),
                    rs.getTimestamp("reserve_end").toLocalDateTime(),
                    rs.getString("status")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Reservation> findAll() {
        String sql = "SELECT * FROM reserve";
        List<Reservation> reservations = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Integer seatId = rs.getObject("seat_id")!= null ? rs.getInt("seat_id") : null;
                Integer memberId = rs.getObject("user_id")!= null ? rs.getInt("user_id") : null;


                Seat seat = seatId != null ? seatDAO.findById(seatId) : null;
                User member = memberId != null ? memberDAO.findById(memberId) : null;

                reservations.add(new Reservation(
                    seat,
                    member,
                    rs.getTimestamp("reserve_due").toLocalDateTime(),
                    rs.getTimestamp("reserve_end").toLocalDateTime(),
                    rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservations;
    }
}