package com.lms.util;

public class Constants {
    
    // User Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_LIBRARIAN = "LIBRARIAN";
    public static final String ROLE_MEMBER = "MEMBER";
    
    // User Status
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    
    // Validation Constants
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MIN_PHONE_LENGTH = 10;
    
    // UI Constants
    public static final String DEFAULT_AVATAR = "👤";
    public static final String SUCCESS_COLOR = "#28a745";
    public static final String ERROR_COLOR = "#dc3545";
    public static final String WARNING_COLOR = "#ffc107";
    public static final String INFO_COLOR = "#17a2b8";
    
    // Module Names
    public static final String MODULE_DASHBOARD = "dashboard";
    public static final String MODULE_BOOKS = "books";
    public static final String MODULE_MEMBERS = "members";
    public static final String MODULE_LOANS = "loans";
    public static final String MODULE_RESERVATIONS = "reservations";
    public static final String MODULE_SEATS = "seats";
    public static final String MODULE_LOGS = "actionlogs";
    public static final String MODULE_SETTINGS = "settings";
    
    // Date Formats
    public static final String DATE_FORMAT_DISPLAY = "MMM dd, yyyy";
    public static final String DATETIME_FORMAT_DISPLAY = "MMM dd, yyyy - HH:mm:ss";
    public static final String DATETIME_FORMAT_FULL = "MMM dd, yyyy HH:mm";
    
    private Constants() {
        // Private constructor to prevent instantiation
    }
}
