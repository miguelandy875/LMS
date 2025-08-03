package com.lms.util;

import com.lms.dao.UserDAO;
import com.lms.dao.LoanDAO;
import com.lms.dao.BookDAO;
import com.lms.model.Loan;
import com.lms.model.Book;
import com.lms.model.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;






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
            admin.setLastName("Admin");
            admin.setSex("M");
            admin.setPhone("0276451937");
            admin.setEmail("admin@lms.lib");
            admin.setPassword(hashPassword("0909"));
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
            System.out.println("Admin: admin@lms.lib / 0909");
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

    public static void createSampleLoans() {
    LoanDAO loanDAO = new LoanDAO();
    UserDAO userDAO = new UserDAO();
    BookDAO bookDAO = new BookDAO();
    
    try {
        // Check if loans already exist
        if (!loanDAO.findAll().isEmpty()) {
            System.out.println("Loans already exist. Skipping sample loan creation.");
            return;
        }
        
        // Get sample users and books
        List<User> members = userDAO.findByRole("MEMBER");
        List<User> librarians = userDAO.findByRole("LIBRARIAN");
        List<Book> books = bookDAO.findAll();
        
        if (members.isEmpty() || librarians.isEmpty() || books.isEmpty()) {
            System.out.println("Not enough sample data (users/books) to create loans.");
            return;
        }
        
        User librarian = librarians.get(0);
        
        // Create sample loans with different statuses
        if (members.size() >= 1 && books.size() >= 4) {
            User member = members.get(0);
            
            // Active loan
            Loan activeLoan = new Loan();
            activeLoan.setUserId(member.getUserId());
            activeLoan.setBookId(books.get(0).getBookId());
            activeLoan.setUserIdIssue(librarian.getUserId());
            activeLoan.setLoanIssueDate(LocalDate.now().minusDays(5));
            activeLoan.setLoanReturnDate(LocalDate.now().plusDays(9));
            activeLoan.setReturned(false);
            loanDAO.save(activeLoan);
            
            // Update book status
            books.get(0).setStatus("ISSUED");
            bookDAO.update(books.get(0));
            
            // Overdue loan
            Loan overdueLoan = new Loan();
            overdueLoan.setUserId(member.getUserId());
            overdueLoan.setBookId(books.get(1).getBookId());
            overdueLoan.setUserIdIssue(librarian.getUserId());
            overdueLoan.setLoanIssueDate(LocalDate.now().minusDays(20));
            overdueLoan.setLoanReturnDate(LocalDate.now().minusDays(6));
            overdueLoan.setReturned(false);
            loanDAO.save(overdueLoan);
            
            // Update book status
            books.get(1).setStatus("ISSUED");
            bookDAO.update(books.get(1));
            
            // Due soon loan
            Loan dueSoonLoan = new Loan();
            dueSoonLoan.setUserId(member.getUserId());
            dueSoonLoan.setBookId(books.get(2).getBookId());
            dueSoonLoan.setUserIdIssue(librarian.getUserId());
            dueSoonLoan.setLoanIssueDate(LocalDate.now().minusDays(12));
            dueSoonLoan.setLoanReturnDate(LocalDate.now().plusDays(2));
            dueSoonLoan.setReturned(false);
            loanDAO.save(dueSoonLoan);
            
            // Update book status
            books.get(2).setStatus("ISSUED");
            bookDAO.update(books.get(2));
            
            // Returned loan
            Loan returnedLoan = new Loan();
            returnedLoan.setUserId(member.getUserId());
            returnedLoan.setBookId(books.get(3).getBookId());
            returnedLoan.setUserIdIssue(librarian.getUserId());
            returnedLoan.setLoanIssueDate(LocalDate.now().minusDays(30));
            returnedLoan.setLoanReturnDate(LocalDate.now().minusDays(16));
            returnedLoan.setReturned(true);
            loanDAO.save(returnedLoan);
        }
        
        System.out.println("Sample loans created successfully!");
        
    } catch (Exception e) {
        System.err.println("Error creating sample loans: " + e.getMessage());
        e.printStackTrace();
    }
}
    
    public static void main(String[] args) {
        // Test database connection and create default users
        if (DatabaseUtil.testConnection()) {
            System.out.println("Database connection successful!");
            createDefaultUsers();
            createSampleMembers(); // Also create sample members
            createSampleLoans();
        } else {
            System.err.println("Database connection failed!");
        }
    }



}