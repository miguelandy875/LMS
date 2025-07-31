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
    
    // NEW AUTHORIZATION METHODS ADDED BELOW
    
    /**
     * Check if the current user can access a specific module
     * @param moduleName The name of the module to check access for
     * @return true if the user can access the module, false otherwise
     */
    public boolean canAccessModule(String moduleName) {
        if (!isAuthenticated()) {
            return false;
        }
        
        String userRole = getCurrentUser().getRole();
        
        switch (moduleName.toLowerCase()) {
            case "dashboard":
                return true; // All authenticated users can access dashboard
            case "books":
            case "members":
            case "loans":
            case "reservations":
            case "seats":
            case "actionlogs":
                return "ADMIN".equals(userRole) || "LIBRARIAN".equals(userRole);
            case "settings":
                return true; // All users can access their settings
            default:
                return false;
        }
    }
    
    /**
     * Check if the current user can perform a specific action
     * @param action The action to check permission for
     * @return true if the user can perform the action, false otherwise
     */
    public boolean canPerformAction(String action) {
        if (!isAuthenticated()) {
            return false;
        }
        
        String userRole = getCurrentUser().getRole();
        
        switch (action.toLowerCase()) {
            case "create_member":
            case "edit_member":
            case "delete_member":
            case "view_member":
                return "ADMIN".equals(userRole) || "LIBRARIAN".equals(userRole);
            case "change_own_password":
            case "view_own_profile":
                return true;
            default:
                return "ADMIN".equals(userRole);
        }
    }
}