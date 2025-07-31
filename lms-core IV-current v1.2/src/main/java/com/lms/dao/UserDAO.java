package com.lms.dao;

import com.lms.model.User;
import com.lms.util.DatabaseUtil;
import com.lms.util.SessionManager; // Add this import for the canDeleteUser method

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements GenericDAO<User> {
    
    private static final String INSERT_USER = 
        "INSERT INTO users (user_fname, user_lname, user_sex, user_phone, user_email, user_pwd, user_role, user_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE_USER = 
        "UPDATE users SET user_fname=?, user_lname=?, user_sex=?, user_phone=?, user_email=?, user_pwd=?, user_role=?, user_status=? WHERE user_id=?";
    
    private static final String DELETE_USER = 
        "DELETE FROM users WHERE user_id=?";
    
    private static final String SELECT_USER_BY_ID = 
        "SELECT * FROM users WHERE user_id=?";
    
    private static final String SELECT_ALL_USERS = 
        "SELECT * FROM users ORDER BY user_fname, user_lname";
    
    private static final String SELECT_USER_BY_EMAIL_PASSWORD = 
        "SELECT * FROM users WHERE user_email=? AND user_pwd=?";
    
    private static final String SELECT_USERS_BY_ROLE = 
        "SELECT * FROM users WHERE user_role=? ORDER BY user_fname, user_lname";
    
    private static final String SELECT_ACTIVE_USERS = 
        "SELECT * FROM users WHERE user_status='ACTIVE' ORDER BY user_fname, user_lname";

    @Override
    public void save(User user) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getSex());
            statement.setString(4, user.getPhone());
            statement.setString(5, user.getEmail());
            statement.setString(6, user.getPassword());
            statement.setString(7, user.getRole());
            statement.setString(8, user.getStatus());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving user: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(User user) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_USER)) {
            
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getSex());
            statement.setString(4, user.getPhone());
            statement.setString(5, user.getEmail());
            statement.setString(6, user.getPassword());
            statement.setString(7, user.getRole());
            statement.setString(8, user.getStatus());
            statement.setInt(9, user.getUserId());
            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int userId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_USER)) {
            
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user: " + e.getMessage(), e);
        }
    }

    @Override
    public User findById(int userId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_USER_BY_ID)) {
            
            statement.setInt(1, userId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_USERS);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all users: " + e.getMessage(), e);
        }
        
        return users;
    }
    
    public User findByEmailAndPassword(String email, String password) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_USER_BY_EMAIL_PASSWORD)) {
            
            statement.setString(1, email);
            statement.setString(2, password);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by email and password: " + e.getMessage(), e);
        }
        return null;
    }
    
    public List<User> findByRole(String role) {
        List<User> users = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_USERS_BY_ROLE)) {
            
            statement.setString(1, role);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(mapResultSetToUser(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding users by role: " + e.getMessage(), e);
        }
        
        return users;
    }
    
    public List<User> findActiveUsers() {
        List<User> users = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ACTIVE_USERS);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding active users: " + e.getMessage(), e);
        }
        
        return users;
    }
    
    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE user_email = ?";
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, email);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if email exists: " + e.getMessage(), e);
        }
        
        return false;
    }
    
    // NEW METHODS ADDED BELOW
    
    /**
     * Search for users based on full name, email, or phone number
     * @param searchTerm The term to search for
     * @return List of users matching the search criteria
     */
    public List<User> searchMembers(String searchTerm) {
        String query = "SELECT * FROM users WHERE " +
                "(CONCAT(user_fname, ' ', user_lname) LIKE ? OR " +
                "user_email LIKE ? OR " +
                "user_phone LIKE ?) " +
                "ORDER BY user_fname, user_lname";
        
        List<User> users = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            String searchPattern = "%" + searchTerm + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(mapResultSetToUser(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching members: " + e.getMessage(), e);
        }
        
        return users;
    }
    
    /**
     * Find users by role and/or status with flexible filtering
     * @param role The role to filter by (null or "All Roles" for no role filter)
     * @param status The status to filter by (null or "All Status" for no status filter)
     * @return List of users matching the criteria
     */
    public List<User> findByRoleAndStatus(String role, String status) {
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM users WHERE 1=1");
        List<String> parameters = new ArrayList<>();
        
        if (role != null && !"All Roles".equals(role)) {
            queryBuilder.append(" AND user_role = ?");
            parameters.add(role);
        }
        
        if (status != null && !"All Status".equals(status)) {
            queryBuilder.append(" AND user_status = ?");
            parameters.add(status);
        }
        
        queryBuilder.append(" ORDER BY user_fname, user_lname");
        
        List<User> users = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
            
            for (int i = 0; i < parameters.size(); i++) {
                statement.setString(i + 1, parameters.get(i));
            }
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(mapResultSetToUser(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding users by role and status: " + e.getMessage(), e);
        }
        
        return users;
    }
    
    /**
     * Check if a user can be deleted
     * Currently checks if the user is not the current logged-in user
     * @param userId The ID of the user to check
     * @return true if the user can be deleted, false otherwise
     */
    public boolean canDeleteUser(int userId) {
        // Check if user has any active loans, reservations, etc.
        // For now, just check if it's not the current user
        User currentUser = SessionManager.getInstance().getCurrentUser();
        return currentUser == null || currentUser.getUserId() != userId;
    }
    
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUserId(resultSet.getInt("user_id"));
        user.setFirstName(resultSet.getString("user_fname"));
        user.setLastName(resultSet.getString("user_lname"));
        user.setSex(resultSet.getString("user_sex"));
        user.setPhone(resultSet.getString("user_phone"));
        user.setEmail(resultSet.getString("user_email"));
        user.setPassword(resultSet.getString("user_pwd"));
        user.setRole(resultSet.getString("user_role"));
        user.setStatus(resultSet.getString("user_status"));
        
        Timestamp createdAt = resultSet.getTimestamp("createdat");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return user;
    }
}