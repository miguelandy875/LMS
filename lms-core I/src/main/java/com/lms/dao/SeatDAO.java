package com.lms.dao;

import com.lms.model.Seat;
import com.lms.utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO implements GenericDAO<Seat> {

    @Override
    public boolean insert(Seat seat) {
        String sql = "INSERT INTO seats (seat_id,  seat_location, seat_type, seat_status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, seat.getSeatId());
            stmt.setString(2, seat.getSeatLocation());
            stmt.setString(3, seat.getSeatType());
            stmt.setString(4, seat.getSeatStatus());
        

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Seat seat) {
        String sql = "UPDATE seats SET  seat_location=?, seat_type=?, seat_status=? WHERE seat_id=?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, seat.getSeatId());
                stmt.setString(2, seat.getSeatLocation());
                stmt.setString(3, seat.getSeatType());
                stmt.setString(4, seat.getSeatStatus());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM seats WHERE seat_id=?";

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
    public Seat findById(int id) {
        String sql = "SELECT * FROM seats WHERE seat_id=?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Seat(
                    rs.getInt("seat_id"),
                    rs.getString("seat_Location"),
                    rs.getString("seat_Type"),
                    rs.getString("seat_status")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Seat> findAll() {
        String sql = "SELECT * FROM seats";
        List<Seat> seats = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                seats.add(new Seat(
                    rs.getInt("seat_id"),
                    rs.getString("seat_Location"),
                    rs.getString("seat_Type"),
                    rs.getString("seat_status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return seats;
    }
}
