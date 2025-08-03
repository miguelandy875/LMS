package com.lms.service;

import com.lms.dao.BookDAO;
import com.lms.dao.LoanDAO;
import com.lms.dao.UserDAO;
import com.lms.model.Book;
import com.lms.model.Loan;
import com.lms.model.LoanStatistics;
import com.lms.model.User;
import com.lms.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class LoanService {
    private LoanDAO loanDAO;
    private BookDAO bookDAO;
    private UserDAO userDAO;
    
    // Configuration constants
    private static final int DEFAULT_LOAN_PERIOD_DAYS = 14;
    private static final int MAX_LOANS_PER_USER = 5;
    private static final double FINE_PER_DAY = 1.0;
    
    public LoanService() {
        this.loanDAO = new LoanDAO();
        this.bookDAO = new BookDAO();
        this.userDAO = new UserDAO();
    }
    
    public Loan issueLoan(int userId, int bookId, int librarianId) throws ServiceException {
        return issueLoan(userId, bookId, librarianId, DEFAULT_LOAN_PERIOD_DAYS);
    }
    
    public Loan issueLoan(int userId, int bookId, int librarianId, int loanPeriodDays) throws ServiceException {
        Connection connection = null;
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            
            // Validate user exists and is active member
            User member = userDAO.findById(userId);
            if (member == null) {
                throw new ServiceException("Member not found with ID: " + userId);
            }
            if (!"MEMBER".equals(member.getRole())) {
                throw new ServiceException("User is not a member");
            }
            if (!"ACTIVE".equals(member.getStatus())) {
                throw new ServiceException("Member account is not active");
            }
            
            // Validate book exists and is available
            Book book = bookDAO.findById(bookId);
            if (book == null) {
                throw new ServiceException("Book not found with ID: " + bookId);
            }
            if (!"AVAILABLE".equals(book.getStatus())) {
                throw new ServiceException("Book is not available for loan. Current status: " + book.getStatus());
            }
            
            // Check if member already has this book
            if (loanDAO.hasActiveLoan(userId, bookId)) {
                throw new ServiceException("Member already has an active loan for this book");
            }
            
            // Check loan limit
            int activeLoanCount = loanDAO.getActiveLoanCount(userId);
            if (activeLoanCount >= MAX_LOANS_PER_USER) {
                throw new ServiceException("Member has reached maximum loan limit (" + MAX_LOANS_PER_USER + " books)");
            }
            
            // Check for overdue books
            List<Loan> memberLoans = loanDAO.findLoansByUser(userId);
            for (Loan loan : memberLoans) {
                if (!loan.isReturned() && loan.isOverdue()) {
                    throw new ServiceException("Member has overdue books. Please return overdue books before issuing new loans.");
                }
            }
            
            // Create new loan
            Loan loan = new Loan();
            loan.setUserId(userId);
            loan.setBookId(bookId);
            loan.setUserIdIssue(librarianId);
            loan.setLoanIssueDate(LocalDate.now());
            loan.setLoanReturnDate(LocalDate.now().plusDays(loanPeriodDays));
            loan.setReturned(false);
            
            // Save loan
            loanDAO.save(loan);
            
            // Update book status to ISSUED
            book.setStatus("ISSUED");
            bookDAO.update(book);
            
            connection.commit();
            
            // Return the complete loan with display information
            return loanDAO.findById(loan.getLoanId());
            
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    throw new ServiceException("Transaction rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw new ServiceException("Database error during loan issue: " + e.getMessage());
        } catch (ServiceException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    throw new ServiceException("Transaction rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    
    public Loan returnBook(int loanId, int librarianId) throws ServiceException {
        Connection connection = null;
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            
            // Find the loan
            Loan loan = loanDAO.findById(loanId);
            if (loan == null) {
                throw new ServiceException("Loan not found with ID: " + loanId);
            }
            
            if (loan.isReturned()) {
                throw new ServiceException("Book has already been returned");
            }
            
            // Update loan as returned
            loan.setReturned(true);
            loanDAO.update(loan);
            
            // Update book status back to AVAILABLE
            Book book = bookDAO.findById(loan.getBookId());
            if (book != null) {
                book.setStatus("AVAILABLE");
                bookDAO.update(book);
            }
            
            connection.commit();
            
            // Return updated loan with display information
            return loanDAO.findById(loanId);
            
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    throw new ServiceException("Transaction rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw new ServiceException("Database error during book return: " + e.getMessage());
        } catch (ServiceException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    throw new ServiceException("Transaction rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    
    public Loan renewLoan(int loanId, int additionalDays) throws ServiceException {
        Connection connection = null;
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            
            // Find the loan
            Loan loan = loanDAO.findById(loanId);
            if (loan == null) {
                throw new ServiceException("Loan not found with ID: " + loanId);
            }
            
            if (loan.isReturned()) {
                throw new ServiceException("Cannot renew a returned book");
            }
            
            if (loan.isOverdue()) {
                throw new ServiceException("Cannot renew an overdue book. Please return the book and pay any fines.");
            }
            
            // Check if book is reserved by another user
            // (This would require a reservation system - placeholder for now)
            
            // Extend the return date
            LocalDate newReturnDate = loan.getLoanReturnDate().plusDays(additionalDays);
            loan.setLoanReturnDate(newReturnDate);
            
            loanDAO.update(loan);
            
            connection.commit();
            
            return loanDAO.findById(loanId);
            
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    throw new ServiceException("Transaction rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw new ServiceException("Database error during loan renewal: " + e.getMessage());
        } catch (ServiceException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    throw new ServiceException("Transaction rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    
    public List<Loan> getAllLoans() {
        return loanDAO.findAll();
    }
    
    public List<Loan> getActiveLoans() {
        return loanDAO.findActiveLoans();
    }
    
    public List<Loan> getOverdueLoans() {
        return loanDAO.findOverdueLoans();
    }
    
    public List<Loan> getLoansByUser(int userId) {
        return loanDAO.findLoansByUser(userId);
    }
    
    public List<Loan> getLoansByBook(int bookId) {
        return loanDAO.findLoansByBook(bookId);
    }
    
    public LoanStatistics getStatistics() {
        return loanDAO.getStatistics();
    }
    
    public List<Loan> searchLoans(String searchTerm, String status, int limit, int offset) {
        return loanDAO.searchLoans(searchTerm, status, limit, offset);
    }
    
    public int countSearchResults(String searchTerm, String status) {
        return loanDAO.countSearchResults(searchTerm, status);
    }
    
    public boolean canIssueLoan(int userId, int bookId) {
        try {
            // Check user exists and is active member
            User member = userDAO.findById(userId);
            if (member == null || !"MEMBER".equals(member.getRole()) || !"ACTIVE".equals(member.getStatus())) {
                return false;
            }
            
            // Check book exists and is available
            Book book = bookDAO.findById(bookId);
            if (book == null || !"AVAILABLE".equals(book.getStatus())) {
                return false;
            }
            
            // Check if member already has this book
            if (loanDAO.hasActiveLoan(userId, bookId)) {
                return false;
            }
            
            // Check loan limit
            int activeLoanCount = loanDAO.getActiveLoanCount(userId);
            if (activeLoanCount >= MAX_LOANS_PER_USER) {
                return false;
            }
            
            // Check for overdue books
            List<Loan> memberLoans = loanDAO.findLoansByUser(userId);
            for (Loan loan : memberLoans) {
                if (!loan.isReturned() && loan.isOverdue()) {
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public double calculateFine(int loanId) {
        Loan loan = loanDAO.findById(loanId);
        if (loan != null) {
            return loan.calculateFine(FINE_PER_DAY);
        }
        return 0.0;
    }
    public int getActiveLoanCount(int userId) {
    return loanDAO.getActiveLoanCount(userId);
}
    public void deleteLoan(int loanId) throws ServiceException {
    Connection connection = null;
    try {
        connection = DatabaseUtil.getConnection();
        connection.setAutoCommit(false);
        
        // Find the loan first
        Loan loan = loanDAO.findById(loanId);
        if (loan == null) {
            throw new ServiceException("Loan not found with ID: " + loanId);
        }
        
        // Only allow deletion of returned loans
        if (!loan.isReturned()) {
            throw new ServiceException("Cannot delete an active loan. Please return the book first.");
        }
        
        // Delete the loan record
        loanDAO.delete(loanId);
        
        connection.commit();
        
    } catch (SQLException e) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                throw new ServiceException("Transaction rollback failed: " + rollbackEx.getMessage());
            }
        }
        throw new ServiceException("Database error during loan deletion: " + e.getMessage());
    } catch (ServiceException e) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                throw new ServiceException("Transaction rollback failed: " + rollbackEx.getMessage());
            }
        }
        throw e;
    } finally {
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
    
    // Configuration getters
    public static int getDefaultLoanPeriodDays() {
        return DEFAULT_LOAN_PERIOD_DAYS;
    }
    
    public static int getMaxLoansPerUser() {
        return MAX_LOANS_PER_USER;
    }
    
    public static double getFinePerDay() {
        return FINE_PER_DAY;
    }
}
