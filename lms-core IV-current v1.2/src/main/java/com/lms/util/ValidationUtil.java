package com.lms.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    
    private ValidationUtil() {
        // Private constructor to prevent instantiation
    }
    
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.replaceAll("\\D", "").length() >= Constants.MIN_PHONE_LENGTH;
    }
    
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= Constants.MIN_PASSWORD_LENGTH;
    }
    
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.trim().length() >= 2;
    }
    
    public static String sanitizeInput(String input) {
        return input != null ? input.trim() : "";
    }
    
    public static String formatPhoneNumber(String phone) {
        if (phone == null) return "";
        
        String digitsOnly = phone.replaceAll("\\D", "");
        
        if (digitsOnly.length() == 10) {
            return String.format("(%s) %s-%s", 
                digitsOnly.substring(0, 3),
                digitsOnly.substring(3, 6),
                digitsOnly.substring(6));
        }
        
        return phone;
    }
}