package com.lms.util;

import com.lms.model.User;
import com.lms.service.AuthenticationService;

public class SessionManager {
    private static SessionManager instance;
    private AuthenticationService authService;
    
    private SessionManager() {
        this.authService = new AuthenticationService();
    }
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public User login(String email, String password) {
        return authService.login(email, password);
    }
    
    public void logout() {
        authService.logout();
    }
    
    public User getCurrentUser() {
        return authService.getCurrentUser();
    }
    
    public boolean isAuthenticated() {
        return authService.isAuthenticated();
    }
    
    public boolean hasRole(String role) {
        return authService.hasRole(role);
    }
    
    public boolean changePassword(String oldPassword, String newPassword) {
        return authService.changePassword(oldPassword, newPassword);
    }
}