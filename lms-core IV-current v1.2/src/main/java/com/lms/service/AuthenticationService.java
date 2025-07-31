package com.lms.service;

import com.lms.model.User;
import com.lms.dao.UserDAO;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class AuthenticationService {
    private UserDAO userDAO;
    private User currentUser;
    
    public AuthenticationService() {
        this.userDAO = new UserDAO();
    }
    
    public User login(String email, String password) {
        try {
            String hashedPassword = hashPassword(password);
            User user = userDAO.findByEmailAndPassword(email, hashedPassword);
            
            if (user != null && "ACTIVE".equals(user.getStatus())) {
                this.currentUser = user;
                logAction(user.getUserId(), "LOGIN", "User logged in successfully");
                return user;
            }
            return null;
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            return null;
        }
    }
    
    public void logout() {
        if (currentUser != null) {
            logAction(currentUser.getUserId(), "LOGOUT", "User logged out");
            currentUser = null;
        }
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isAuthenticated() {
        return currentUser != null;
    }
    
    public boolean hasRole(String role) {
        return currentUser != null && role.equals(currentUser.getRole());
    }
    
    private String hashPassword(String password) {
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
    
    private void logAction(int userId, String actionType, String details) {
        // This will be implemented when we have ActionDAO ready
        System.out.println("Action logged: " + actionType + " - " + details);
    }
    
    public boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser == null) return false;
        
        try {
            String hashedOldPassword = hashPassword(oldPassword);
            if (!hashedOldPassword.equals(currentUser.getPassword())) {
                return false;
            }
            
            String hashedNewPassword = hashPassword(newPassword);
            currentUser.setPassword(hashedNewPassword);
            userDAO.update(currentUser);
            
            logAction(currentUser.getUserId(), "PASSWORD_CHANGE", "Password changed successfully");
            return true;
        } catch (Exception e) {
            System.err.println("Password change error: " + e.getMessage());
            return false;
        }
    }
}