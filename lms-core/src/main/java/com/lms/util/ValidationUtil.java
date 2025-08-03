package com.lms.util;

import java.time.Year;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class ValidationUtil {
   
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
   
    private ValidationUtil() {
        // Private constructor to prevent instantiation
    }
   
    // Existing validation methods
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
    
    // New book validation methods
    
    /**
     * Validates if the given year is a valid publication year
     */
    public static boolean isValidYear(String year) {
        if (year == null || year.trim().isEmpty()) {
            return false;
        }
        
        try {
            int yearInt = Integer.parseInt(year.trim());
            int currentYear = Year.now().getValue();
            // Year should be between 1000 and current year + 1 (for upcoming publications)
            return yearInt >= 1000 && yearInt <= currentYear + 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validates if the given pages number is valid
     */
    public static boolean isValidPages(String pages) {
        if (pages == null || pages.trim().isEmpty()) {
            return false;
        }
        
        try {
            int pagesInt = Integer.parseInt(pages.trim());
            return pagesInt > 0 && pagesInt <= 10000; // Reasonable upper limit
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validates book title
     */
    public static boolean isValidBookTitle(String title) {
        return title != null && !title.trim().isEmpty() && title.trim().length() >= 2;
    }
    
    /**
     * Validates author name
     */
    public static boolean isValidAuthorName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = name.trim();
        return trimmed.length() >= 2 && trimmed.matches("^[a-zA-Z\\s\\.\\-']+$");
    }
    
    /**
     * Validates category name
     */
    public static boolean isValidCategoryName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = name.trim();
        return trimmed.length() >= 2 && trimmed.length() <= 50;
    }
    
    /**
     * Sanitizes book title
     */
    public static String sanitizeBookTitle(String title) {
        if (title == null) return "";
        return title.trim().replaceAll("\\s+", " ");
    }
    
    /**
     * Sanitizes author name
     */
    public static String sanitizeAuthorName(String name) {
        if (name == null) return "";
        
        // Remove extra spaces and capitalize properly
        String sanitized = name.trim().replaceAll("\\s+", " ");
        
        // Simple title case conversion
        String[] words = sanitized.split(" ");
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            if (i > 0) result.append(" ");
            String word = words[i].toLowerCase();
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1));
                }
            }
        }
        
        return result.toString();
    }

    public static class LoanValidation {
    
    public static boolean isValidLoanPeriod(int days) {
        return days >= 1 && days <= 90;
    }
    
    public static boolean isValidIssueDate(LocalDate issueDate) {
        if (issueDate == null) return false;
        LocalDate today = LocalDate.now();
        // Allow dates from yesterday to 30 days in the future
        return !issueDate.isBefore(today.minusDays(1)) && 
               !issueDate.isAfter(today.plusDays(30));
    }
    
    public static boolean isValidReturnDate(LocalDate issueDate, LocalDate returnDate) {
        if (issueDate == null || returnDate == null) return false;
        return returnDate.isAfter(issueDate) && 
               returnDate.isBefore(issueDate.plusDays(91)); // Max 90 days
    }
    
    public static String validateLoanData(LocalDate issueDate, LocalDate returnDate, int loanPeriod) {
        if (!isValidIssueDate(issueDate)) {
            return "Issue date must be between yesterday and 30 days from today.";
        }
        
        if (!isValidReturnDate(issueDate, returnDate)) {
            return "Return date must be after issue date and within 90 days.";
        }
        
        if (!isValidLoanPeriod(loanPeriod)) {
            return "Loan period must be between 1 and 90 days.";
        }
        
        return null; // No validation errors
    }
    }

}