package com.lms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseUtil {
    private static final String CONFIG_FILE = "/database.properties";
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;
    
    static {
        loadDatabaseConfig();
    }
    
    private static void loadDatabaseConfig() {
        try (InputStream input = DatabaseUtil.class.getResourceAsStream(CONFIG_FILE)) {
            Properties props = new Properties();
            if (input != null) {
                props.load(input);
                URL = props.getProperty("db.url", "jdbc:mysql://localhost:3306/lmsdb");
                USERNAME = props.getProperty("db.username", "root");
                PASSWORD = props.getProperty("db.password", "");
            } else {
                // Default values if config file not found
                URL = "jdbc:mysql://localhost:3306/lmsdb";
                USERNAME = "root";
                PASSWORD = "";
            }
        } catch (IOException e) {
            System.err.println("Error loading database configuration: " + e.getMessage());
            // Use default values
            URL = "jdbc:mysql://localhost:3306/lmsdb";
            USERNAME = "root";
            PASSWORD = "";
        }
    }
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
    
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
    
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}
