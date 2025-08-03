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

    /**
     * Get paginated users with search and filter capabilities
     */
 public PaginationResult<User> findPaginated(int page, int pageSize, String searchTerm, 
                                               String roleFilter, String statusFilter) {
        
        System.out.println("=== DEBUG: findPaginated called ===");
        System.out.println("Page: " + page + ", PageSize: " + pageSize);
        System.out.println("SearchTerm: '" + searchTerm + "'");
        System.out.println("RoleFilter: '" + roleFilter + "'");
        System.out.println("StatusFilter: '" + statusFilter + "'");
        
        List<User> users = new ArrayList<>();
        int totalRecords = 0;
        
        // Build the WHERE clause dynamically
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1 ");
        List<Object> parameters = new ArrayList<>();
        
        try {
            // Add search filter
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                whereClause.append(" AND (LOWER(user_fname) LIKE ? OR LOWER(user_lname) LIKE ? OR LOWER(user_email) LIKE ? OR user_phone LIKE ?) ");
                String searchPattern = "%" + searchTerm.toLowerCase() + "%";
                parameters.add(searchPattern);
                parameters.add(searchPattern);
                parameters.add(searchPattern);
                parameters.add("%" + searchTerm + "%");
                System.out.println("Added search filter: " + searchPattern);
            }

                  
            // Add role filter
            if (roleFilter != null && !roleFilter.trim().isEmpty() && !roleFilter.equals("All Roles")) {
                whereClause.append(" AND role = ? ");
                parameters.add(roleFilter);
                System.out.println("Added role filter: " + roleFilter);
            }
            
            // Add status filter
            if (statusFilter != null && !statusFilter.trim().isEmpty() && !statusFilter.equals("All Status")) {
                whereClause.append(" AND user_status = ? ");
                parameters.add(statusFilter);
                System.out.println("Added status filter: " + statusFilter);
            }
            
            System.out.println("Final WHERE clause: " + whereClause.toString());
            System.out.println("Parameters count: " + parameters.size());
            
            // Test database connection first
            try (Connection conn = getConnection()) {
                if (conn == null) {
                    throw new SQLException("Could not establish database connection");
                }
                System.out.println("Database connection established successfully");
                
                // First, get total count
                String countQuery = "SELECT COUNT(*) FROM users" + whereClause.toString();
                System.out.println("Count Query: " + countQuery);
                
                try (PreparedStatement countStmt = conn.prepareStatement(countQuery)) {
                    setParameters(countStmt, parameters);
                    
                    System.out.println("Executing count query...");
                    ResultSet countRs = countStmt.executeQuery();
                    if (countRs.next()) {
                        totalRecords = countRs.getInt(1);
                        System.out.println("Total records found: " + totalRecords);
                    }
                } catch (SQLException e) {
                    System.err.println("Error in count query: " + e.getMessage());
                    throw e;
                }
                
                // Then get paginated results
                String dataQuery = "SELECT user_id, user_fname, user_lname, user_email, user_phone, user_sex, user_role, user_status, createdat " +
                                 "FROM users" + whereClause.toString() + 
                                 " ORDER BY createdat DESC LIMIT ? OFFSET ?";
                
                System.out.println("Data Query: " + dataQuery);
                System.out.println("LIMIT: " + pageSize + ", OFFSET: " + ((page - 1) * pageSize));
                
                try (PreparedStatement dataStmt = conn.prepareStatement(dataQuery)) {
                    setParameters(dataStmt, parameters);
                    dataStmt.setInt(parameters.size() + 1, pageSize);
                    dataStmt.setInt(parameters.size() + 2, (page - 1) * pageSize);
                    
                    System.out.println("Executing data query...");
                    ResultSet rs = dataStmt.executeQuery();
                    
                    int recordCount = 0;
                    while (rs.next()) {
                        User user = new User();
                        user.setUserId(rs.getInt("user_id"));
                        user.setFirstName(rs.getString("user_fname"));
                        user.setLastName(rs.getString("user_lname"));
                        user.setEmail(rs.getString("user_email"));
                        user.setPhone(rs.getString("user_phone"));
                        user.setSex(rs.getString("user_sex"));
                        user.setRole(rs.getString("user_role"));
                        user.setStatus(rs.getString("user_status"));
                        
                        // Handle createdat safely
                        Timestamp createdAt = rs.getTimestamp("createdat");
                        if (createdAt != null) {
                            user.setCreatedAt(createdAt.toLocalDateTime());
                        }
                        
                        users.add(user);
                        recordCount++;
                    }
                    
                    System.out.println("Retrieved " + recordCount + " records for current page");
                    
                } catch (SQLException e) {
                    System.err.println("Error in data query: " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
                
            } catch (SQLException e) {
                System.err.println("Database connection error: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Error connecting to database: " + e.getMessage(), e);
            }
            
        } catch (Exception e) {
            System.err.println("Unexpected error in findPaginated: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error fetching paginated users: " + e.getMessage(), e);
        }
        
        System.out.println("=== DEBUG: Creating PaginationResult ===");
        System.out.println("Users: " + users.size() + ", Total: " + totalRecords + ", Page: " + page + ", PageSize: " + pageSize);
        
        return new PaginationResult<>(users, totalRecords, page, pageSize);
    }
    
    private void setParameters(PreparedStatement stmt, List<Object> parameters) throws SQLException {
        System.out.println("Setting " + parameters.size() + " parameters:");
        for (int i = 0; i < parameters.size(); i++) {
            Object param = parameters.get(i);
            System.out.println("  Parameter " + (i + 1) + ": " + param + " (" + param.getClass().getSimpleName() + ")");
            stmt.setObject(i + 1, param);
        }
    }
    
    // Add this method to get connection (adjust based on your existing DatabaseUtil class)
    private Connection getConnection() throws SQLException {
        // Replace with your actual database connection method
        // This is just a placeholder - use your existing DatabaseUtil.getConnection()
        try {
            return DatabaseUtil.getConnection();
        } catch (Exception e) {
            System.err.println("Failed to get database connection: " + e.getMessage());
            throw new SQLException("Database connection failed", e);
        }
    }
    
    // Test method to verify basic database connectivity
    public void testDatabaseUtil() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Database connection test successful");
                
                // Test basic query
                try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM users")) {
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        System.out.println("✅ Basic query successful. Total users: " + count);
                    }
                } catch (SQLException e) {
                    System.err.println("❌ Basic query failed: " + e.getMessage());
                    throw e;
                }
            } else {
                System.err.println("❌ Database connection is null or closed");
            }
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Fallback method that returns your existing findAll() with fake pagination
    public PaginationResult<User> findPaginatedFallback(int page, int pageSize, String searchTerm, 
                                                        String roleFilter, String statusFilter) {
        System.out.println("Using fallback pagination method...");
        
        try {
            // Get all users using your existing method
            List<User> allUsers = this.findAll(); // Your existing method
            
            // Apply filters manually
            List<User> filteredUsers = new ArrayList<>();
            for (User user : allUsers) {
                boolean matches = true;
                
                // Search filter
                if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                    String search = searchTerm.toLowerCase();
                    matches = user.getFirstName().toLowerCase().contains(search) ||
                             user.getLastName().toLowerCase().contains(search) ||
                             user.getEmail().toLowerCase().contains(search) ||
                             user.getPhone().contains(searchTerm);
                }
                
                // Role filter
                if (matches && roleFilter != null && !roleFilter.equals("All Roles")) {
                    matches = roleFilter.equals(user.getRole());
                }
                
                // Status filter
                if (matches && statusFilter != null && !statusFilter.equals("All Status")) {
                    matches = statusFilter.equals(user.getStatus());
                }
                
                if (matches) {
                    filteredUsers.add(user);
                }
            }
            
            // Manual pagination
            int totalRecords = filteredUsers.size();
            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalRecords);
            
            List<User> pageUsers = new ArrayList<>();
            if (startIndex < totalRecords) {
                pageUsers = filteredUsers.subList(startIndex, endIndex);
            }
            
            System.out.println("Fallback method: Total=" + totalRecords + ", Page=" + page + ", PageSize=" + pageSize + ", Results=" + pageUsers.size());
            
            return new PaginationResult<>(pageUsers, totalRecords, page, pageSize);
            
        } catch (Exception e) {
            System.err.println("Fallback method also failed: " + e.getMessage());
            e.printStackTrace();
            return new PaginationResult<>(new ArrayList<>(), 0, page, pageSize);
        }
    }
}