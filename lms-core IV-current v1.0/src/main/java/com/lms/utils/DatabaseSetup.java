package com.lms.util;

import com.lms.dao.UserDAO;
import com.lms.model.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class DatabaseSetup {
    
    public static void createDefaultUsers() {
        UserDAO userDAO = new UserDAO();
        
        try {
            // Check if users already exist
            if (!userDAO.findAll().isEmpty()) {
                System.out.println("Users already exist. Skipping default user creation.");
                return;
            }
            
            // Create default admin user
            User admin = new User();
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setSex("M");
            admin.setPhone("1234567890");
            admin.setEmail("admin@library.com");
            admin.setPassword(hashPassword("admin123"));
            admin.setRole("ADMIN");
            admin.setStatus("ACTIVE");
            userDAO.save(admin);
            
            // Create default librarian user
            User librarian = new User();
            librarian.setFirstName("John");
            librarian.setLastName("Librarian");
            librarian.setSex("M");
            librarian.setPhone("0987654321");
            librarian.setEmail("librarian@library.com");
            librarian.setPassword(hashPassword("lib123"));
            librarian.setRole("LIBRARIAN");
            librarian.setStatus("ACTIVE");
            userDAO.save(librarian);
            
            // Create default member user
            User member = new User();
            member.setFirstName("Jane");
            member.setLastName("Member");
            member.setSex("F");
            member.setPhone("1122334455");
            member.setEmail("member@library.com");
            member.setPassword(hashPassword("mem123"));
            member.setRole("MEMBER");
            member.setStatus("ACTIVE");
            userDAO.save(member);
            
            System.out.println("Default users created successfully!");
            System.out.println("Admin: admin@library.com / admin123");
            System.out.println("Librarian: librarian@library.com / lib123");
            System.out.println("Member: member@library.com / mem123");
            
        } catch (Exception e) {
            System.err.println("Error creating default users: " + e.getMessage());
        }
    }
    
    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    public static void main(String[] args) {
        // Test database connection and create default users
        if (DatabaseUtil.testConnection()) {
            System.out.println("Database connection successful!");
            createDefaultUsers();
        } else {
            System.err.println("Database connection failed!");
        }
    }
}