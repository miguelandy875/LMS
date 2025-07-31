package com.lms.dao;

import com.lms.model.Book;
import com.lms.model.User;
import com.lms.model.Loan;
import com.lms.utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO implements GenericDAO<Loan> {

    private final BookDAO bookDAO = new BookDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    public boolean insert(Loan loan) {
        String sql = "INSERT INTO loans (loan_id, user_id, book_id, user_id_issue, loan_issue_date, loan_return_date, returned) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, loan.getLoanId());
            stmt.setInt(2, loan.getBorrower().getUserId());
            stmt.setInt(3, loan.getBook().getBookId());
            stmt.setInt(4, loan.getIssuer().getUserId());
            stmt.setDate(5, Date.valueOf(loan.getIssueDate()));
            stmt.setDate(6, Date.valueOf(loan.getReturnDate()));
            stmt.setBoolean(7, loan.isReturned());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Loan loan) {
        String sql = "UPDATE loans SET user_id=?, book_id=?, user_id_issue=?, loan_issue_date=?, loan_return_date=?, returned=? WHERE loan_id=?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, loan.getBorrower().getUserId());
            stmt.setInt(2, loan.getBook().getBookId());
            stmt.setInt(3, loan.getIssuer().getUserId());
            stmt.setDate(4, Date.valueOf(loan.getIssueDate()));
            stmt.setDate(5, Date.valueOf(loan.getReturnDate()));
            stmt.setBoolean(6, loan.isReturned());
            stmt.setInt(7, loan.getLoanId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM loans WHERE loan_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Loan findById(int id) {
        String sql = "SELECT * FROM loans WHERE loan_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Integer borrowerId = rs.getObject("user_id")!= null ? rs.getInt("user_id") : null;
                Integer bookId = rs.getObject("book_id")!= null ? rs.getInt("book_id") : null;
                Integer issuerId = rs.getObject("user_id_issue")!= null ? rs.getInt("user_id_issue") : null;

                User borrower = borrowerId  != null ? userDAO.findById(borrowerId): null;
                Book book =  bookId != null ? bookDAO.findById(bookId): null;
                User issuer = issuerId != null ? userDAO.findById(issuerId): null;

                return new Loan(
                    rs.getInt("loan_id"),
                    book,
                    borrower,
                    issuer,
                    rs.getDate("loan_issue_date").toLocalDate(),
                    rs.getDate("loan_return_date").toLocalDate(),
                    rs.getBoolean("returned")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Loan> findAll() {
        String sql = "SELECT * FROM loans";
        List<Loan> loans = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Integer borrowerId = rs.getObject("user_id")!= null ? rs.getInt("user_id") : null;
                Integer bookId = rs.getObject("book_id")!= null ? rs.getInt("book_id") : null;
                Integer issuerId = rs.getObject("user_id_issue")!= null ? rs.getInt("user_id_issue") : null;

                User borrower = borrowerId  != null ? userDAO.findById(borrowerId): null;
                Book book =  bookId != null ? bookDAO.findById(bookId): null;
                User issuer = issuerId != null ? userDAO.findById(issuerId): null;

                loans.add(new Loan(
                    rs.getInt("loan_id"),
                    book,
                    borrower,
                    issuer,
                    rs.getDate("loan_issue_date").toLocalDate(),
                    rs.getDate("loan_return_date").toLocalDate(),
                    rs.getBoolean("returned")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }
}