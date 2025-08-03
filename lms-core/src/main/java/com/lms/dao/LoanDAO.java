package com.lms.dao;

import com.lms.model.Loan;
import com.lms.model.LoanStatistics;
import com.lms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO implements GenericDAO<Loan> {
    
    private static final String INSERT_LOAN = 
        "INSERT INTO loans (user_id, book_id, user_id_issue, loan_issue_date, loan_return_date, returned) VALUES (?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE_LOAN = 
        "UPDATE loans SET user_id=?, book_id=?, user_id_issue=?, loan_issue_date=?, loan_return_date=?, returned=? WHERE loan_id=?";
    
    private static final String DELETE_LOAN = 
        "DELETE FROM loans WHERE loan_id=?";
    
    private static final String SELECT_LOAN_BY_ID = 
        "SELECT l.*, CONCAT(u1.user_fname, ' ', u1.user_lname) as member_name, u1.user_email as member_email, " +
        "b.book_title, GROUP_CONCAT(DISTINCT a.author_name SEPARATOR ', ') as book_authors, " +
        "CONCAT(u2.user_fname, ' ', u2.user_lname) as librarian_name " +
        "FROM loans l " +
        "JOIN users u1 ON l.user_id = u1.user_id " +
        "JOIN books b ON l.book_id = b.book_id " +
        "JOIN users u2 ON l.user_id_issue = u2.user_id " +
        "LEFT JOIN authoring au ON b.book_id = au.book_id " +
        "LEFT JOIN author a ON au.author_id = a.author_id " +
        "WHERE l.loan_id = ? " +
        "GROUP BY l.loan_id";
    
    private static final String SELECT_ALL_LOANS = 
        "SELECT l.*, CONCAT(u1.user_fname, ' ', u1.user_lname) as member_name, u1.user_email as member_email, " +
        "b.book_title, GROUP_CONCAT(DISTINCT a.author_name SEPARATOR ', ') as book_authors, " +
        "CONCAT(u2.user_fname, ' ', u2.user_lname) as librarian_name " +
        "FROM loans l " +
        "JOIN users u1 ON l.user_id = u1.user_id " +
        "JOIN books b ON l.book_id = b.book_id " +
        "JOIN users u2 ON l.user_id_issue = u2.user_id " +
        "LEFT JOIN authoring au ON b.book_id = au.book_id " +
        "LEFT JOIN author a ON au.author_id = a.author_id " +
        "GROUP BY l.loan_id " +
        "ORDER BY l.loan_issue_date DESC";
    
    private static final String SELECT_ACTIVE_LOANS = 
        "SELECT l.*, CONCAT(u1.user_fname, ' ', u1.user_lname) as member_name, u1.user_email as member_email, " +
        "b.book_title, GROUP_CONCAT(DISTINCT a.author_name SEPARATOR ', ') as book_authors, " +
        "CONCAT(u2.user_fname, ' ', u2.user_lname) as librarian_name " +
        "FROM loans l " +
        "JOIN users u1 ON l.user_id = u1.user_id " +
        "JOIN books b ON l.book_id = b.book_id " +
        "JOIN users u2 ON l.user_id_issue = u2.user_id " +
        "LEFT JOIN authoring au ON b.book_id = au.book_id " +
        "LEFT JOIN author a ON au.author_id = a.author_id " +
        "WHERE l.returned = false " +
        "GROUP BY l.loan_id " +
        "ORDER BY l.loan_return_date ASC";
    
    private static final String SELECT_OVERDUE_LOANS = 
        "SELECT l.*, CONCAT(u1.user_fname, ' ', u1.user_lname) as member_name, u1.user_email as member_email, " +
        "b.book_title, GROUP_CONCAT(DISTINCT a.author_name SEPARATOR ', ') as book_authors, " +
        "CONCAT(u2.user_fname, ' ', u2.user_lname) as librarian_name " +
        "FROM loans l " +
        "JOIN users u1 ON l.user_id = u1.user_id " +
        "JOIN books b ON l.book_id = b.book_id " +
        "JOIN users u2 ON l.user_id_issue = u2.user_id " +
        "LEFT JOIN authoring au ON b.book_id = au.book_id " +
        "LEFT JOIN author a ON au.author_id = a.author_id " +
        "WHERE l.returned = false AND l.loan_return_date < CURDATE() " +
        "GROUP BY l.loan_id " +
        "ORDER BY l.loan_return_date ASC";
    
    private static final String SELECT_LOANS_BY_USER = 
        "SELECT l.*, CONCAT(u1.user_fname, ' ', u1.user_lname) as member_name, u1.user_email as member_email, " +
        "b.book_title, GROUP_CONCAT(DISTINCT a.author_name SEPARATOR ', ') as book_authors, " +
        "CONCAT(u2.user_fname, ' ', u2.user_lname) as librarian_name " +
        "FROM loans l " +
        "JOIN users u1 ON l.user_id = u1.user_id " +
        "JOIN books b ON l.book_id = b.book_id " +
        "JOIN users u2 ON l.user_id_issue = u2.user_id " +
        "LEFT JOIN authoring au ON b.book_id = au.book_id " +
        "LEFT JOIN author a ON au.author_id = a.author_id " +
        "WHERE l.user_id = ? " +
        "GROUP BY l.loan_id " +
        "ORDER BY l.loan_issue_date DESC";
    
    private static final String SELECT_LOANS_BY_BOOK = 
        "SELECT l.*, CONCAT(u1.user_fname, ' ', u1.user_lname) as member_name, u1.user_email as member_email, " +
        "b.book_title, GROUP_CONCAT(DISTINCT a.author_name SEPARATOR ', ') as book_authors, " +
        "CONCAT(u2.user_fname, ' ', u2.user_lname) as librarian_name " +
        "FROM loans l " +
        "JOIN users u1 ON l.user_id = u1.user_id " +
        "JOIN books b ON l.book_id = b.book_id " +
        "JOIN users u2 ON l.user_id_issue = u2.user_id " +
        "LEFT JOIN authoring au ON b.book_id = au.book_id " +
        "LEFT JOIN author a ON au.author_id = a.author_id " +
        "WHERE l.book_id = ? " +
        "GROUP BY l.loan_id " +
        "ORDER BY l.loan_issue_date DESC";

    @Override
    public void save(Loan loan) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_LOAN, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setInt(1, loan.getUserId());
            statement.setInt(2, loan.getBookId());
            statement.setInt(3, loan.getUserIdIssue());
            statement.setDate(4, Date.valueOf(loan.getLoanIssueDate()));
            statement.setDate(5, Date.valueOf(loan.getLoanReturnDate()));
            statement.setBoolean(6, loan.isReturned());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        loan.setLoanId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving loan: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Loan loan) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_LOAN)) {
            
            statement.setInt(1, loan.getUserId());
            statement.setInt(2, loan.getBookId());
            statement.setInt(3, loan.getUserIdIssue());
            statement.setDate(4, Date.valueOf(loan.getLoanIssueDate()));
            statement.setDate(5, Date.valueOf(loan.getLoanReturnDate()));
            statement.setBoolean(6, loan.isReturned());
            statement.setInt(7, loan.getLoanId());
            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating loan: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int loanId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_LOAN)) {
            
            statement.setInt(1, loanId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting loan: " + e.getMessage(), e);
        }
    }

    @Override
    public Loan findById(int loanId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_LOAN_BY_ID)) {
            
            statement.setInt(1, loanId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToLoan(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding loan by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Loan> findAll() {
        List<Loan> loans = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_LOANS);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                loans.add(mapResultSetToLoan(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all loans: " + e.getMessage(), e);
        }
        
        return loans;
    }
    
    public List<Loan> findActiveLoans() {
        List<Loan> loans = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ACTIVE_LOANS);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                loans.add(mapResultSetToLoan(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding active loans: " + e.getMessage(), e);
        }
        
        return loans;
    }
    
    public List<Loan> findOverdueLoans() {
        List<Loan> loans = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_OVERDUE_LOANS);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                loans.add(mapResultSetToLoan(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding overdue loans: " + e.getMessage(), e);
        }
        
        return loans;
    }
    
    public List<Loan> findLoansByUser(int userId) {
        List<Loan> loans = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_LOANS_BY_USER)) {
            
            statement.setInt(1, userId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    loans.add(mapResultSetToLoan(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding loans by user: " + e.getMessage(), e);
        }
        
        return loans;
    }
    
    public List<Loan> findLoansByBook(int bookId) {
        List<Loan> loans = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_LOANS_BY_BOOK)) {
            
            statement.setInt(1, bookId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    loans.add(mapResultSetToLoan(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding loans by book: " + e.getMessage(), e);
        }
        
        return loans;
    }
    
    public boolean hasActiveLoan(int userId, int bookId) {
        String query = "SELECT COUNT(*) FROM loans WHERE user_id = ? AND book_id = ? AND returned = false";
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, userId);
            statement.setInt(2, bookId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking active loan: " + e.getMessage(), e);
        }
        
        return false;
    }
    
    public int getActiveLoanCount(int userId) {
        String query = "SELECT COUNT(*) FROM loans WHERE user_id = ? AND returned = false";
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, userId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting active loan count: " + e.getMessage(), e);
        }
        
        return 0;
    }
    
    
    public LoanStatistics getStatistics() {
        LoanStatistics stats = new LoanStatistics();
        
        String query = 
            "SELECT " +
            "(SELECT COUNT(*) FROM loans) as total_loans, " +
            "(SELECT COUNT(*) FROM loans WHERE returned = false) as active_loans, " +
            "(SELECT COUNT(*) FROM loans WHERE returned = false AND loan_return_date < CURDATE()) as overdue_loans, " +
            "(SELECT COUNT(*) FROM loans WHERE returned = false AND loan_return_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 2 DAY)) as due_soon_loans, " +
            "(SELECT COUNT(*) FROM loans WHERE returned = true) as returned_loans, " +
            "(SELECT COUNT(DISTINCT user_id) FROM loans) as total_members, " +
            "(SELECT COUNT(DISTINCT book_id) FROM loans WHERE returned = false) as books_issued";
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            
            if (resultSet.next()) {
                stats.setTotalLoans(resultSet.getInt("total_loans"));
                stats.setActiveLoans(resultSet.getInt("active_loans"));
                stats.setOverdueLoans(resultSet.getInt("overdue_loans"));
                stats.setDueSoonLoans(resultSet.getInt("due_soon_loans"));
                stats.setReturnedLoans(resultSet.getInt("returned_loans"));
                stats.setTotalMembers(resultSet.getInt("total_members"));
                stats.setBooksIssued(resultSet.getInt("books_issued"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting loan statistics: " + e.getMessage(), e);
        }
        
        return stats;
    }
    
    public List<Loan> searchLoans(String searchTerm, String status, int limit, int offset) {
        List<Loan> loans = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();
        
        queryBuilder.append("SELECT l.*, CONCAT(u1.user_fname, ' ', u1.user_lname) as member_name, u1.user_email as member_email, ");
        queryBuilder.append("b.book_title, GROUP_CONCAT(DISTINCT a.author_name SEPARATOR ', ') as book_authors, ");
        queryBuilder.append("CONCAT(u2.user_fname, ' ', u2.user_lname) as librarian_name ");
        queryBuilder.append("FROM loans l ");
        queryBuilder.append("JOIN users u1 ON l.user_id = u1.user_id ");
        queryBuilder.append("JOIN books b ON l.book_id = b.book_id ");
        queryBuilder.append("JOIN users u2 ON l.user_id_issue = u2.user_id ");
        queryBuilder.append("LEFT JOIN authoring au ON b.book_id = au.book_id ");
        queryBuilder.append("LEFT JOIN author a ON au.author_id = a.author_id ");
        queryBuilder.append("WHERE 1=1 ");
        
        // Add search term condition
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            queryBuilder.append("AND (b.book_title LIKE ? OR CONCAT(u1.user_fname, ' ', u1.user_lname) LIKE ? OR u1.user_email LIKE ?) ");
        }
        
        // Add status condition
        if (status != null && !status.equals("ALL")) {
            switch (status) {
                case "ACTIVE":
                    queryBuilder.append("AND l.returned = false AND l.loan_return_date >= CURDATE() ");
                    break;
                case "OVERDUE":
                    queryBuilder.append("AND l.returned = false AND l.loan_return_date < CURDATE() ");
                    break;
                case "DUE_SOON":
                    queryBuilder.append("AND l.returned = false AND l.loan_return_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 2 DAY) ");
                    break;
                case "RETURNED":
                    queryBuilder.append("AND l.returned = true ");
                    break;
            }
        }
        
        queryBuilder.append("GROUP BY l.loan_id ");
        queryBuilder.append("ORDER BY l.loan_issue_date DESC ");
        queryBuilder.append("LIMIT ? OFFSET ?");
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
            
            int paramIndex = 1;
            
            // Set search term parameters
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String searchPattern = "%" + searchTerm.trim() + "%";
                statement.setString(paramIndex++, searchPattern);
                statement.setString(paramIndex++, searchPattern);
                statement.setString(paramIndex++, searchPattern);
            }
            
            // Set pagination parameters
            statement.setInt(paramIndex++, limit);
            statement.setInt(paramIndex, offset);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    loans.add(mapResultSetToLoan(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching loans: " + e.getMessage(), e);
        }
        
        return loans;
    }
    
    public int countSearchResults(String searchTerm, String status) {
        StringBuilder queryBuilder = new StringBuilder();
        
        queryBuilder.append("SELECT COUNT(DISTINCT l.loan_id) ");
        queryBuilder.append("FROM loans l ");
        queryBuilder.append("JOIN users u1 ON l.user_id = u1.user_id ");
        queryBuilder.append("JOIN books b ON l.book_id = b.book_id ");
        queryBuilder.append("WHERE 1=1 ");
        
        // Add search term condition
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            queryBuilder.append("AND (b.book_title LIKE ? OR CONCAT(u1.user_fname, ' ', u1.user_lname) LIKE ? OR u1.user_email LIKE ?) ");
        }
        
        // Add status condition
        if (status != null && !status.equals("ALL")) {
            switch (status) {
                case "ACTIVE":
                    queryBuilder.append("AND l.returned = false AND l.loan_return_date >= CURDATE() ");
                    break;
                case "OVERDUE":
                    queryBuilder.append("AND l.returned = false AND l.loan_return_date < CURDATE() ");
                    break;
                case "DUE_SOON":
                    queryBuilder.append("AND l.returned = false AND l.loan_return_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 2 DAY) ");
                    break;
                case "RETURNED":
                    queryBuilder.append("AND l.returned = true ");
                    break;
            }
        }
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
            
            int paramIndex = 1;
            
            // Set search term parameters
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String searchPattern = "%" + searchTerm.trim() + "%";
                statement.setString(paramIndex++, searchPattern);
                statement.setString(paramIndex++, searchPattern);
                statement.setString(paramIndex++, searchPattern);
            }
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting search results: " + e.getMessage(), e);
        }
        
        return 0;
    }
    
    // ========== MISSING METHOD - This is what you were missing! ==========
    private Loan mapResultSetToLoan(ResultSet resultSet) throws SQLException {
        Loan loan = new Loan();
        loan.setLoanId(resultSet.getInt("loan_id"));
        loan.setUserId(resultSet.getInt("user_id"));
        loan.setBookId(resultSet.getInt("book_id"));
        loan.setUserIdIssue(resultSet.getInt("user_id_issue"));
        
        Date issueDate = resultSet.getDate("loan_issue_date");
        if (issueDate != null) {
            loan.setLoanIssueDate(issueDate.toLocalDate());
        }
        
        Date returnDate = resultSet.getDate("loan_return_date");
        if (returnDate != null) {
            loan.setLoanReturnDate(returnDate.toLocalDate());
        }
        
        loan.setReturned(resultSet.getBoolean("returned"));
        
        // Set display fields
        loan.setMemberName(resultSet.getString("member_name"));
        loan.setMemberEmail(resultSet.getString("member_email"));
        loan.setBookTitle(resultSet.getString("book_title"));
        loan.setBookAuthors(resultSet.getString("book_authors"));
        loan.setLibrarianName(resultSet.getString("librarian_name"));
        
        // Calculate overdue days and fine
        long overdueCount = loan.getDaysOverdueCount();
        loan.setDaysOverdue((int) overdueCount);
        loan.setFineAmount(loan.calculateFine(1.0)); // $1 per day fine
        
        return loan;
    }
}