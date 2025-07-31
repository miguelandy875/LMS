package com.lms.service.impl;

import java.util.List;
import com.lms.dao.UserDAO;
import com.lms.model.User;
import com.lms.service.UserService;

public class UserServiceImpl implements UserService {
    
    private final UserDAO userDAO;
    
    public UserServiceImpl() {
        this.userDAO = new UserDAO(); // Assuming you have this implemented
    }
    
    @Override
    public void addUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        // Validation logic
        if (user.getUserEmail() == null || user.getUserEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("User email is required");
        }
        
        if (user.getUserPwd() == null || user.getUserPwd().trim().isEmpty()) {
            throw new IllegalArgumentException("User password is required");
        }
        
        // Check if user already exists
        User existingUser = userDAO.findByEmail(user.getUserEmail());
        if (existingUser != null) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        
        userDAO.save(user);
    }
    
    @Override
    public void updateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (user.getUserId() <= 0) {
            throw new IllegalArgumentException("Valid user ID is required for update");
        }
        
        // Check if user exists
        User existingUser = userDAO.findById(user.getUserId());
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found with ID: " + user.getUserId());
        }
        
        userDAO.update(user);
    }
    
    @Override
    public void deleteUser(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Valid user ID is required");
        }
        
        // Check if user exists
        User existingUser = userDAO.findById(userId);
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        
        // Check if user has active loans before deletion
        // You might want to add this check based on your business rules
        
        userDAO.delete(userId);
    }
    
    @Override
    public User findUserById(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Valid user ID is required");
        }
        
        return userDAO.findById(userId);
    }
    
    @Override
    public List<User> findAllUsers() {
        return userDAO.findAll();
    }
    
    @Override
    public User login(String email, String password) {
        System.out.println("=== LOGIN DEBUG START ===");
        System.out.println("Input email: '" + email + "'");
        System.out.println("Input password: '" + password + "'");
        System.out.println("Email length: " + (email != null ? email.length() : "null"));
        System.out.println("Password length: " + (password != null ? password.length() : "null"));
        
        if (email == null || email.trim().isEmpty()) {
            System.out.println("❌ FAILED: Email is null or empty");
            return null;
        }
        
        if (password == null || password.trim().isEmpty()) {
            System.out.println("❌ FAILED: Password is null or empty");
            return null;
        }
        
        String trimmedEmail = email.trim();
        System.out.println("Trimmed email: '" + trimmedEmail + "'");
        
        // Find user by email
        System.out.println("🔍 Searching for user with email: '" + trimmedEmail + "'");
        User user = userDAO.findByEmail(trimmedEmail);
        
        System.out.println("User found: " + (user != null ? "YES" : "NO"));
        
        if (user == null) {
            System.out.println("❌ FAILED: No user found with email: '" + trimmedEmail + "'");
            System.out.println("=== LOGIN DEBUG END ===\n");
            return null;
        }
        
        // Debug user details
        System.out.println("--- USER DETAILS ---");
        System.out.println("User ID: " + user.getUserId());
        System.out.println("Stored email: '" + user.getUserEmail() + "'");
        System.out.println("Stored password: '" + user.getUserPwd() + "'");
        System.out.println("User role: '" + user.getUserRole() + "'");
        System.out.println("User status (boolean): " + user.getUserStatus());
        System.out.println("User fname: '" + user.getUserFname() + "'");
        System.out.println("User lname: '" + user.getUserLname() + "'");
        
        // Check if user account is active
        if (!user.getUserStatus()) {
            System.out.println("❌ FAILED: User account is not active (status = false)");
            System.out.println("=== LOGIN DEBUG END ===\n");
            return null;
        }
        
        System.out.println("✅ User account is active");
        
        // Password verification debug
        System.out.println("--- PASSWORD VERIFICATION ---");
        System.out.println("Input password: '" + password + "'");
        System.out.println("Stored password: '" + user.getUserPwd() + "'");
        System.out.println("Passwords equal? " + password.equals(user.getUserPwd()));
        
        // Verify password
        boolean passwordMatch = verifyPassword(password, user.getUserPwd());
        System.out.println("verifyPassword() result: " + passwordMatch);
        
        if (passwordMatch) {
            System.out.println("✅ LOGIN SUCCESSFUL!");
            System.out.println("=== LOGIN DEBUG END ===\n");
            return user;
        }
        
        System.out.println("❌ FAILED: Password mismatch");
        System.out.println("=== LOGIN DEBUG END ===\n");
        return null;
    }
    
    /**
     * Verify password against stored plain text password
     * NOTE: In production, you should use password hashing (BCrypt, etc.)
     */
    private boolean verifyPassword(String inputPassword, String storedPassword) {
        // Simple plain text comparison
        if (inputPassword == null || storedPassword == null) {
            return false;
        }
        return inputPassword.equals(storedPassword);
    }
    
    /**
     * Find users by role
     */
    public List<User> findUsersByRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        
        return userDAO.findByRole(role);
    }
    
    /**
     * Check if email already exists
     */
    public boolean emailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        return userDAO.findByEmail(email.trim()) != null;
    }
    
    /**
     * Change user password
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        User user = findUserById(userId);
        if (user == null) {
            return false;
        }
        
        // Verify old password
        if (!verifyPassword(oldPassword, user.getUserPwd())) {
            return false;
        }
        
        // Update with new password (should be hashed)
        user.setUserPwd(newPassword); // Hash this in production
        userDAO.update(user);
        
        return true;
    }
    
    /**
     * Activate/Deactivate user account - FIXED: Convert String to boolean
     */
    public void updateUserStatus(int userId, String status) {
        User user = findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        // Convert string to boolean - FIXED: Proper type conversion
        boolean statusBoolean = "active".equalsIgnoreCase(status) || "true".equalsIgnoreCase(status);
        user.setUserStatus(statusBoolean);
        userDAO.update(user);
    }
}