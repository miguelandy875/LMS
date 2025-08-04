package com.lms.dao;

import com.lms.model.Seat;
import com.lms.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO implements GenericDAO<Seat> {
    
    private static final String INSERT_SEAT = 
        "INSERT INTO seats (seat_location, seat_type, seat_status) VALUES (?, ?, ?)";
    
    private static final String UPDATE_SEAT = 
        "UPDATE seats SET seat_location=?, seat_type=?, seat_status=? WHERE seat_id=?";
    
    private static final String DELETE_SEAT = 
        "DELETE FROM seats WHERE seat_id=?";
    
    private static final String SELECT_SEAT_BY_ID = 
        "SELECT * FROM seats WHERE seat_id=?";
    
    private static final String SELECT_ALL_SEATS = 
        "SELECT * FROM seats ORDER BY seat_location";
    
    private static final String SELECT_SEATS_BY_STATUS = 
        "SELECT * FROM seats WHERE seat_status=? ORDER BY seat_location";
    
    private static final String SELECT_SEATS_BY_TYPE = 
        "SELECT * FROM seats WHERE seat_type=? ORDER BY seat_location";
    
    private static final String SELECT_AVAILABLE_SEATS = 
        "SELECT * FROM seats WHERE seat_status='AVAILABLE' ORDER BY seat_location";

    @Override
    public void save(Seat seat) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SEAT, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, seat.getSeatLocation());
            statement.setString(2, seat.getSeatType());
            statement.setString(3, seat.getSeatStatus());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        seat.setSeatId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving seat: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Seat seat) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SEAT)) {
            
            statement.setString(1, seat.getSeatLocation());
            statement.setString(2, seat.getSeatType());
            statement.setString(3, seat.getSeatStatus());
            statement.setInt(4, seat.getSeatId());
            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating seat: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int seatId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SEAT)) {
            
            statement.setInt(1, seatId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting seat: " + e.getMessage(), e);
        }
    }

    @Override
    public Seat findById(int seatId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_SEAT_BY_ID)) {
            
            statement.setInt(1, seatId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToSeat(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding seat by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Seat> findAll() {
        List<Seat> seats = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SEATS);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                seats.add(mapResultSetToSeat(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all seats: " + e.getMessage(), e);
        }
        
        return seats;
    }
    
    public List<Seat> findByStatus(String status) {
        List<Seat> seats = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_SEATS_BY_STATUS)) {
            
            statement.setString(1, status);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    seats.add(mapResultSetToSeat(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding seats by status: " + e.getMessage(), e);
        }
        
        return seats;
    }
    
    public List<Seat> findByType(String type) {
        List<Seat> seats = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_SEATS_BY_TYPE)) {
            
            statement.setString(1, type);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    seats.add(mapResultSetToSeat(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding seats by type: " + e.getMessage(), e);
        }
        
        return seats;
    }
    
    public List<Seat> findAvailableSeats() {
        List<Seat> seats = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_AVAILABLE_SEATS);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                seats.add(mapResultSetToSeat(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding available seats: " + e.getMessage(), e);
        }
        
        return seats;
    }
    
    public int countSeatsByStatus(String status) {
        String query = "SELECT COUNT(*) FROM seats WHERE seat_status = ?";
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, status);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting seats by status: " + e.getMessage(), e);
        }
        
        return 0;
    }
    
    private Seat mapResultSetToSeat(ResultSet resultSet) throws SQLException {
        Seat seat = new Seat();
        seat.setSeatId(resultSet.getInt("seat_id"));
        seat.setSeatLocation(resultSet.getString("seat_location"));
        seat.setSeatType(resultSet.getString("seat_type"));
        seat.setSeatStatus(resultSet.getString("seat_status"));
        return seat;
    }
}
