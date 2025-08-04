package com.lms.dao;

import com.lms.model.Reservation;
import com.lms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO implements GenericDAO<Reservation> {
    
    // Note: We'll need to create a reservations table since it's not in your original schema
    // For now, I'll use a placeholder table structure
    
    private static final String INSERT_RESERVATION = 
        "INSERT INTO book_reservations (user_id, book_id, reservation_date, expiry_date, status) VALUES (?, ?, ?, ?, ?)";
    
    private static final String UPDATE_RESERVATION = 
        "UPDATE book_reservations SET user_id=?, book_id=?, reservation_date=?, expiry_date=?, status=? WHERE reservation_id=?";
    
    private static final String DELETE_RESERVATION = 
        "DELETE FROM book_reservations WHERE reservation_id=?";
    
    private static final String SELECT_RESERVATION_BY_ID = 
        "SELECT r.*, CONCAT(u.user_fname, ' ', u.user_lname) as member_name, u.user_email as member_email, " +
        "b.book_title, GROUP_CONCAT(DISTINCT a.author_name SEPARATOR ', ') as book_authors " +
        "FROM book_reservations r " +
        "JOIN users u ON r.user_id = u.user_id " +
        "JOIN books b ON r.book_id = b.book_id " +
        "LEFT JOIN authoring au ON b.book_id = au.book_id " +
        "LEFT JOIN author a ON au.author_id = a.author_id " +
        "WHERE r.reservation_id = ? " +
        "GROUP BY r.reservation_id";
    
    private static final String SELECT_ALL_RESERVATIONS = 
        "SELECT r.*, CONCAT(u.user_fname, ' ', u.user_lname) as member_name, u.user_email as member_email, " +
        "b.book_title, GROUP_CONCAT(DISTINCT a.author_name SEPARATOR ', ') as book_authors " +
        "FROM book_reservations r " +
        "JOIN users u ON r.user_id = u.user_id " +
        "JOIN books b ON r.book_id = b.book_id " +
        "LEFT JOIN authoring au ON b.book_id = au.book_id " +
        "LEFT JOIN author a ON au.author_id = a.author_id " +
        "GROUP BY r.reservation_id " +
        "ORDER BY r.reservation_date DESC";
    
    private static final String SELECT_ACTIVE_RESERVATIONS = 
        "SELECT r.*, CONCAT(u.user_fname, ' ', u.user_lname) as member_name, u.user_email as member_email, " +
        "b.book_title, GROUP_CONCAT(DISTINCT a.author_name SEPARATOR ', ') as book_authors " +
        "FROM book_reservations r " +
        "JOIN users u ON r.user_id = u.user_id " +
        "JOIN books b ON r.book_id = b.book_id " +
        "LEFT JOIN authoring au ON b.book_id = au.book_id " +
        "LEFT JOIN author a ON au.author_id = a.author_id " +
        "WHERE r.status = 'ACTIVE' AND r.expiry_date > NOW() " +
        "GROUP BY r.reservation_id " +
        "ORDER BY r.reservation_date ASC";
    
    private static final String SELECT_RESERVATIONS_BY_USER = 
        "SELECT r.*, CONCAT(u.user_fname, ' ', u.user_lname) as member_name, u.user_email as member_email, " +
        "b.book_title, GROUP_CONCAT(DISTINCT a.author_name SEPARATOR ', ') as book_authors " +
        "FROM book_reservations r " +
        "JOIN users u ON r.user_id = u.user_id " +
        "JOIN books b ON r.book_id = b.book_id " +
        "LEFT JOIN authoring au ON b.book_id = au.book_id " +
        "LEFT JOIN author a ON au.author_id = a.author_id " +
        "WHERE r.user_id = ? " +
        "GROUP BY r.reservation_id " +
        "ORDER BY r.reservation_date DESC";
    
    private static final String SELECT_RESERVATIONS_BY_BOOK = 
        "SELECT r.*, CONCAT(u.user_fname, ' ', u.user_lname) as member_name, u.user_email as member_email, " +
        "b.book_title, GROUP_CONCAT(DISTINCT a.author_name SEPARATOR ', ') as book_authors " +
        "FROM book_reservations r " +
        "JOIN users u ON r.user_id = u.user_id " +
        "JOIN books b ON r.book_id = b.book_id " +
        "LEFT JOIN authoring au ON b.book_id = au.book_id " +
        "LEFT JOIN author a ON au.author_id = a.author_id " +
        "WHERE r.book_id = ? " +
        "GROUP BY r.reservation_id " +
        "ORDER BY r.reservation_date ASC";

    @Override
    public void save(Reservation reservation) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_RESERVATION, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setInt(1, reservation.getUserId());
            statement.setInt(2, reservation.getBookId());
            statement.setTimestamp(3, Timestamp.valueOf(reservation.getReservationDate()));
            statement.setTimestamp(4, Timestamp.valueOf(reservation.getExpiryDate()));
            statement.setString(5, reservation.getStatus());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reservation.setReservationId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving reservation: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Reservation reservation) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_RESERVATION)) {
            
            statement.setInt(1, reservation.getUserId());
            statement.setInt(2, reservation.getBookId());
            statement.setTimestamp(3, Timestamp.valueOf(reservation.getReservationDate()));
            statement.setTimestamp(4, Timestamp.valueOf(reservation.getExpiryDate()));
            statement.setString(5, reservation.getStatus());
            statement.setInt(6, reservation.getReservationId());
            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating reservation: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int reservationId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_RESERVATION)) {
            
            statement.setInt(1, reservationId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting reservation: " + e.getMessage(), e);
        }
    }

    @Override
    public Reservation findById(int reservationId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_RESERVATION_BY_ID)) {
            
            statement.setInt(1, reservationId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToReservation(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding reservation by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Reservation> findAll() {
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_RESERVATIONS);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                reservations.add(mapResultSetToReservation(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all reservations: " + e.getMessage(), e);
        }
        
        return reservations;
    }
    
    public List<Reservation> findActiveReservations() {
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ACTIVE_RESERVATIONS);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                reservations.add(mapResultSetToReservation(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding active reservations: " + e.getMessage(), e);
        }
        
        return reservations;
    }
    
    public List<Reservation> findReservationsByUser(int userId) {
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_RESERVATIONS_BY_USER)) {
            
            statement.setInt(1, userId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reservations.add(mapResultSetToReservation(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding reservations by user: " + e.getMessage(), e);
        }
        
        return reservations;
    }
    
    public List<Reservation> findReservationsByBook(int bookId) {
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_RESERVATIONS_BY_BOOK)) {
            
            statement.setInt(1, bookId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reservations.add(mapResultSetToReservation(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding reservations by book: " + e.getMessage(), e);
        }
        
        return reservations;
    }
    
    public boolean hasActiveReservation(int userId, int bookId) {
        String query = "SELECT COUNT(*) FROM book_reservations WHERE user_id = ? AND book_id = ? AND status = 'ACTIVE' AND expiry_date > NOW()";
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, userId);
            statement.setInt(2, bookId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking active reservation: " + e.getMessage(), e);
        }
        
        return false;
    }
    
    public int getQueuePosition(int userId, int bookId) {
        String query = "SELECT COUNT(*) + 1 FROM book_reservations WHERE book_id = ? AND status = 'ACTIVE' AND expiry_date > NOW() AND reservation_date < (SELECT reservation_date FROM book_reservations WHERE user_id = ? AND book_id = ? AND status = 'ACTIVE' AND expiry_date > NOW())";
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, bookId);
            statement.setInt(2, userId);
            statement.setInt(3, bookId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting queue position: " + e.getMessage(), e);
        }
        
        return 0;
    }
    
    public List<Reservation> searchReservations(String searchTerm, String status, int limit, int offset) {
        List<Reservation> reservations = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();
        
        queryBuilder.append("SELECT r.*, CONCAT(u.user_fname, ' ', u.user_lname) as member_name, u.user_email as member_email, ");
        queryBuilder.append("b.book_title, GROUP_CONCAT(DISTINCT a.author_name SEPARATOR ', ') as book_authors ");
        queryBuilder.append("FROM book_reservations r ");
        queryBuilder.append("JOIN users u ON r.user_id = u.user_id ");
        queryBuilder.append("JOIN books b ON r.book_id = b.book_id ");
        queryBuilder.append("LEFT JOIN authoring au ON b.book_id = au.book_id ");
        queryBuilder.append("LEFT JOIN author a ON au.author_id = a.author_id ");
        queryBuilder.append("WHERE 1=1 ");
        
        // Add search term condition
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            queryBuilder.append("AND (b.book_title LIKE ? OR CONCAT(u.user_fname, ' ', u.user_lname) LIKE ? OR u.user_email LIKE ?) ");
        }
        
        // Add status condition
        if (status != null && !status.equals("ALL")) {
            switch (status) {
                case "ACTIVE":
                    queryBuilder.append("AND r.status = 'ACTIVE' AND r.expiry_date > NOW() ");
                    break;
                case "EXPIRED":
                    queryBuilder.append("AND (r.status = 'EXPIRED' OR (r.status = 'ACTIVE' AND r.expiry_date <= NOW())) ");
                    break;
                default:
                    queryBuilder.append("AND r.status = ? ");
                    break;
            }
        }
        
        queryBuilder.append("GROUP BY r.reservation_id ");
        queryBuilder.append("ORDER BY r.reservation_date DESC ");
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
            if (status != null && !status.equals("ALL") && !status.equals("ACTIVE") && !status.equals("EXPIRED")) {
                statement.setString(paramIndex++, status);
            }
            
            // Set pagination parameters
            statement.setInt(paramIndex++, limit);
            statement.setInt(paramIndex, offset);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reservations.add(mapResultSetToReservation(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching reservations: " + e.getMessage(), e);
        }
        
        return reservations;
    }
    
    public int countSearchResults(String searchTerm, String status) {
        StringBuilder queryBuilder = new StringBuilder();
        
        queryBuilder.append("SELECT COUNT(DISTINCT r.reservation_id) ");
        queryBuilder.append("FROM book_reservations r ");
        queryBuilder.append("JOIN users u ON r.user_id = u.user_id ");
        queryBuilder.append("JOIN books b ON r.book_id = b.book_id ");
        queryBuilder.append("WHERE 1=1 ");
        
        // Add search term condition
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            queryBuilder.append("AND (b.book_title LIKE ? OR CONCAT(u.user_fname, ' ', u.user_lname) LIKE ? OR u.user_email LIKE ?) ");
        }
        
        // Add status condition
        if (status != null && !status.equals("ALL")) {
            switch (status) {
                case "ACTIVE":
                    queryBuilder.append("AND r.status = 'ACTIVE' AND r.expiry_date > NOW() ");
                    break;
                case "EXPIRED":
                    queryBuilder.append("AND (r.status = 'EXPIRED' OR (r.status = 'ACTIVE' AND r.expiry_date <= NOW())) ");
                    break;
                default:
                    queryBuilder.append("AND r.status = ? ");
                    break;
            }
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
            if (status != null && !status.equals("ALL") && !status.equals("ACTIVE") && !status.equals("EXPIRED")) {
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
    
    private Reservation mapResultSetToReservation(ResultSet resultSet) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setReservationId(resultSet.getInt("reservation_id"));
        reservation.setUserId(resultSet.getInt("user_id"));
        reservation.setBookId(resultSet.getInt("book_id"));
        
        Timestamp reservationDate = resultSet.getTimestamp("reservation_date");
        if (reservationDate != null) {
            reservation.setReservationDate(reservationDate.toLocalDateTime());
        }
        
        Timestamp expiryDate = resultSet.getTimestamp("expiry_date");
        if (expiryDate != null) {
            reservation.setExpiryDate(expiryDate.toLocalDateTime());
        }
        
        reservation.setStatus(resultSet.getString("status"));
        
        // Set display fields
        reservation.setMemberName(resultSet.getString("member_name"));
        reservation.setMemberEmail(resultSet.getString("member_email"));
        reservation.setBookTitle(resultSet.getString("book_title"));
        reservation.setBookAuthors(resultSet.getString("book_authors"));
        
        return reservation;
    }
}