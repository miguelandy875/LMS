package com.lms.dao;

import com.lms.model.SeatReservation;
import com.lms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SeatReservationDAO {
    
    private static final String INSERT_SEAT_RESERVATION = 
        "INSERT INTO reserve (seat_id, user_id, reserve_due, reserve_end, status) VALUES (?, ?, ?, ?, ?)";
    
    private static final String UPDATE_SEAT_RESERVATION = 
        "UPDATE reserve SET reserve_due=?, reserve_end=?, status=? WHERE seat_id=? AND user_id=?";
    
    private static final String DELETE_SEAT_RESERVATION = 
        "DELETE FROM reserve WHERE seat_id=? AND user_id=?";
    
    private static final String SELECT_SEAT_RESERVATION = 
        "SELECT r.*, s.seat_location, s.seat_type, CONCAT(u.user_fname, ' ', u.user_lname) as member_name, u.user_email as member_email " +
        "FROM reserve r " +
        "JOIN seats s ON r.seat_id = s.seat_id " +
        "JOIN users u ON r.user_id = u.user_id " +
        "WHERE r.seat_id = ? AND r.user_id = ?";
    
    private static final String SELECT_ALL_SEAT_RESERVATIONS = 
        "SELECT r.*, s.seat_location, s.seat_type, CONCAT(u.user_fname, ' ', u.user_lname) as member_name, u.user_email as member_email " +
        "FROM reserve r " +
        "JOIN seats s ON r.seat_id = s.seat_id " +
        "JOIN users u ON r.user_id = u.user_id " +
        "ORDER BY r.reserve_due DESC";
    
    private static final String SELECT_ACTIVE_SEAT_RESERVATIONS = 
        "SELECT r.*, s.seat_location, s.seat_type, CONCAT(u.user_fname, ' ', u.user_lname) as member_name, u.user_email as member_email " +
        "FROM reserve r " +
        "JOIN seats s ON r.seat_id = s.seat_id " +
        "JOIN users u ON r.user_id = u.user_id " +
        "WHERE r.status = 'ACTIVE' " +
        "ORDER BY r.reserve_due ASC";
    
    private static final String SELECT_SEAT_RESERVATIONS_BY_USER = 
        "SELECT r.*, s.seat_location, s.seat_type, CONCAT(u.user_fname, ' ', u.user_lname) as member_name, u.user_email as member_email " +
        "FROM reserve r " +
        "JOIN seats s ON r.seat_id = s.seat_id " +
        "JOIN users u ON r.user_id = u.user_id " +
        "WHERE r.user_id = ? " +
        "ORDER BY r.reserve_due DESC";
    
    private static final String SELECT_SEAT_RESERVATIONS_BY_SEAT = 
        "SELECT r.*, s.seat_location, s.seat_type, CONCAT(u.user_fname, ' ', u.user_lname) as member_name, u.user_email as member_email " +
        "FROM reserve r " +
        "JOIN seats s ON r.seat_id = s.seat_id " +
        "JOIN users u ON r.user_id = u.user_id " +
        "WHERE r.seat_id = ? " +
        "ORDER BY r.reserve_due DESC";

    public void save(SeatReservation reservation) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SEAT_RESERVATION)) {
            
            statement.setInt(1, reservation.getSeatId());
            statement.setInt(2, reservation.getUserId());
            statement.setTimestamp(3, Timestamp.valueOf(reservation.getReserveStart()));
            statement.setTimestamp(4, Timestamp.valueOf(reservation.getReserveEnd()));
            statement.setString(5, reservation.getStatus());
            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving seat reservation: " + e.getMessage(), e);
        }
    }

    public void update(SeatReservation reservation) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SEAT_RESERVATION)) {
            
            statement.setTimestamp(1, Timestamp.valueOf(reservation.getReserveStart()));
            statement.setTimestamp(2, Timestamp.valueOf(reservation.getReserveEnd()));
            statement.setString(3, reservation.getStatus());
            statement.setInt(4, reservation.getSeatId());
            statement.setInt(5, reservation.getUserId());
            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating seat reservation: " + e.getMessage(), e);
        }
    }

    public void delete(int seatId, int userId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SEAT_RESERVATION)) {
            
            statement.setInt(1, seatId);
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting seat reservation: " + e.getMessage(), e);
        }
    }

    public SeatReservation findBySeatAndUser(int seatId, int userId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_SEAT_RESERVATION)) {
            
            statement.setInt(1, seatId);
            statement.setInt(2, userId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToSeatReservation(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding seat reservation: " + e.getMessage(), e);
        }
        return null;
    }

    public List<SeatReservation> findAll() {
        List<SeatReservation> reservations = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SEAT_RESERVATIONS);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                reservations.add(mapResultSetToSeatReservation(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all seat reservations: " + e.getMessage(), e);
        }
        
        return reservations;
    }
    
    public List<SeatReservation> findActiveReservations() {
        List<SeatReservation> reservations = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ACTIVE_SEAT_RESERVATIONS);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                reservations.add(mapResultSetToSeatReservation(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding active seat reservations: " + e.getMessage(), e);
        }
        
        return reservations;
    }
    
    public List<SeatReservation> findReservationsByUser(int userId) {
        List<SeatReservation> reservations = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_SEAT_RESERVATIONS_BY_USER)) {
            
            statement.setInt(1, userId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reservations.add(mapResultSetToSeatReservation(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding seat reservations by user: " + e.getMessage(), e);
        }
        
        return reservations;
    }
    
    public List<SeatReservation> findReservationsBySeat(int seatId) {
        List<SeatReservation> reservations = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_SEAT_RESERVATIONS_BY_SEAT)) {
            
            statement.setInt(1, seatId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reservations.add(mapResultSetToSeatReservation(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding seat reservations by seat: " + e.getMessage(), e);
        }
        
        return reservations;
    }
    
    public boolean isSeatAvailable(int seatId, LocalDateTime startTime, LocalDateTime endTime) {
        String query = "SELECT COUNT(*) FROM reserve WHERE seat_id = ? AND status = 'ACTIVE' AND " +
                      "((reserve_due <= ? AND reserve_end > ?) OR (reserve_due < ? AND reserve_end >= ?) OR " +
                      "(reserve_due >= ? AND reserve_due < ?))";
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, seatId);
            statement.setTimestamp(2, Timestamp.valueOf(startTime));
            statement.setTimestamp(3, Timestamp.valueOf(startTime));
            statement.setTimestamp(4, Timestamp.valueOf(endTime));
            statement.setTimestamp(5, Timestamp.valueOf(endTime));
            statement.setTimestamp(6, Timestamp.valueOf(startTime));
            statement.setTimestamp(7, Timestamp.valueOf(endTime));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking seat availability: " + e.getMessage(), e);
        }
        
        return false;
    }
    
    public List<SeatReservation> searchSeatReservations(String searchTerm, String status, int limit, int offset) {
        List<SeatReservation> reservations = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();
        
        queryBuilder.append("SELECT r.*, s.seat_location, s.seat_type, CONCAT(u.user_fname, ' ', u.user_lname) as member_name, u.user_email as member_email ");
        queryBuilder.append("FROM reserve r ");
        queryBuilder.append("JOIN seats s ON r.seat_id = s.seat_id ");
        queryBuilder.append("JOIN users u ON r.user_id = u.user_id ");
        queryBuilder.append("WHERE 1=1 ");
        
        // Add search term condition
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            queryBuilder.append("AND (s.seat_location LIKE ? OR CONCAT(u.user_fname, ' ', u.user_lname) LIKE ? OR u.user_email LIKE ?) ");
        }
        
        // Add status condition
        if (status != null && !status.equals("ALL")) {
            queryBuilder.append("AND r.status = ? ");
        }
        
        queryBuilder.append("ORDER BY r.reserve_due DESC ");
        queryBuilder.append("LIMIT ? OFFSET ?");
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
            
            int paramIndex = 1;
            
            // Set search term parameters
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String searchPattern = "%" + searchTerm.trim() + "%";
                statement.setString(paramIndex++, searchPattern);
                statement.setString(paramIndex++, searchPattern);
                statement.setString(paramIndex++, searchPattern);
            }
            
            // Set status parameter
            if (status != null && !status.equals("ALL")) {
                statement.setString(paramIndex++, status);
            }
            
            // Set pagination parameters
            statement.setInt(paramIndex++, limit);
            statement.setInt(paramIndex, offset);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reservations.add(mapResultSetToSeatReservation(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching seat reservations: " + e.getMessage(), e);
        }
        
        return reservations;
    }
    
    public int countSearchResults(String searchTerm, String status) {
        StringBuilder queryBuilder = new StringBuilder();
        
        queryBuilder.append("SELECT COUNT(*) ");
        queryBuilder.append("FROM reserve r ");
        queryBuilder.append("JOIN seats s ON r.seat_id = s.seat_id ");
        queryBuilder.append("JOIN users u ON r.user_id = u.user_id ");
        queryBuilder.append("WHERE 1=1 ");
        
        // Add search term condition
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            queryBuilder.append("AND (s.seat_location LIKE ? OR CONCAT(u.user_fname, ' ', u.user_lname) LIKE ? OR u.user_email LIKE ?) ");
        }
        
        // Add status condition
        if (status != null && !status.equals("ALL")) {
            queryBuilder.append("AND r.status = ? ");
        }
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
            
            int paramIndex = 1;
            
            // Set search term parameters
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String searchPattern = "%" + searchTerm.trim() + "%";
                statement.setString(paramIndex++, searchPattern);
                statement.setString(paramIndex++, searchPattern);
                statement.setString(paramIndex++, searchPattern);
            }
            
            // Set status parameter
            if (status != null && !status.equals("ALL")) {
                statement.setString(paramIndex++, status);
            }
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting search results: " + e.getMessage(), e);
        }
        
        return 0;
    }
    
    private SeatReservation mapResultSetToSeatReservation(ResultSet resultSet) throws SQLException {
        SeatReservation reservation = new SeatReservation();
        reservation.setSeatId(resultSet.getInt("seat_id"));
        reservation.setUserId(resultSet.getInt("user_id"));
        
        Timestamp reserveStart = resultSet.getTimestamp("reserve_due");
        if (reserveStart != null) {
            reservation.setReserveStart(reserveStart.toLocalDateTime());
        }
        
        Timestamp reserveEnd = resultSet.getTimestamp("reserve_end");
        if (reserveEnd != null) {
            reservation.setReserveEnd(reserveEnd.toLocalDateTime());
        }
        
        reservation.setStatus(resultSet.getString("status"));
        
        // Set display fields
        reservation.setSeatLocation(resultSet.getString("seat_location"));
        reservation.setSeatType(resultSet.getString("seat_type"));
        reservation.setMemberName(resultSet.getString("member_name"));
        reservation.setMemberEmail(resultSet.getString("member_email"));
        
        return reservation;
    }
}
        