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
            admin.setFirstName("Andy Miguel");
            admin.setLastName("Habyarimana");
            admin.setSex("M");
            admin.setPhone("1234567890");
            admin.setEmail("miguel@library.com");
            admin.setPassword(hashPassword("mig123"));
            admin.setRole("ADMIN");
            admin.setStatus("ACTIVE");
            userDAO.save(admin);
            
            // Create default librarian user
            User librarian = new User();
            librarian.setFirstName("John");
            librarian.setLastName("Ray");
            librarian.setSex("M");
            librarian.setPhone("0987654321");
            librarian.setEmail("lib@library.com");
            librarian.setPassword(hashPassword("lib123"));
            librarian.setRole("LIBRARIAN");
            librarian.setStatus("ACTIVE");
            userDAO.save(librarian);
            
            // Create default member user
            User member = new User();
            member.setFirstName("Jane");
            member.setLastName("Foster");
            member.setSex("F");
            member.setPhone("1122334455");
            member.setEmail("member@library.com");
            member.setPassword(hashPassword("mem123"));
            member.setRole("MEMBER");
            member.setStatus("ACTIVE");
            userDAO.save(member);
            
            System.out.println("Default users created successfully!");
            System.out.println("Admin: miguel@library.com / mig123");
            System.out.println("Librarian: lib@library.com / lib123");
            System.out.println("Member: member@library.com / mem123");
            
        } catch (Exception e) {
            System.err.println("Error creating default users: " + e.getMessage());
        }
    }
    
    /**
     * Creates additional sample members for testing purposes
     * This method generates 8 sample members with varied data
     */
    public static void createSampleMembers() {
        UserDAO userDAO = new UserDAO();
        
        try {
            // Create additional sample members for testing
            String[] firstNames = {"Alice", "Bob", "Charlie", "Diana", "Edward", "Fiona", "George", "Helen"};
            String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis"};
            String[] domains = {"gmail.com", "yahoo.com", "hotmail.com", "outlook.com"};
            
            for (int i = 0; i < 8; i++) {
                String firstName = firstNames[i];
                String lastName = lastNames[i];
                String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@" + domains[i % domains.length];
                
                // Check if user already exists
                if (!userDAO.emailExists(email)) {
                    User member = new User();
                    member.setFirstName(firstName);
                    member.setLastName(lastName);
                    member.setSex(i % 2 == 0 ? "Female" : "Male");
                    member.setPhone("555" + String.format("%07d", 1000000 + i));
                    member.setEmail(email);
                    member.setPassword(hashPassword("password123"));
                    member.setRole("MEMBER");
                    member.setStatus(i % 5 == 0 ? "INACTIVE" : "ACTIVE");
                    
                    userDAO.save(member);
                }
            }
            
            System.out.println("Sample members created successfully!");
            
        } catch (Exception e) {
            System.err.println("Error creating sample members: " + e.getMessage());
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
            createSampleMembers(); // Also create sample members
        } else {
            System.err.println("Database connection failed!");
        }
    }
}