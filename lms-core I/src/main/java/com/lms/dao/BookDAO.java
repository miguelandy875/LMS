package com.lms.dao;

import com.lms.model.Book;
import com.lms.model.Category;
import com.lms.utils.DatabaseUtil;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO implements GenericDAO<Book> {

    @Override
    public boolean insert(Book book) {
        String sql = "INSERT INTO books (book_id, cat_id, book_title, book_pages, book_pub_year, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, book.getBookId());
            stmt.setInt(2, book.getCategory().getCategoryId());
            stmt.setString(3, book.getBookTitle());
            stmt.setInt(4, book.getBookPages());
            stmt.setDate(5, Date.valueOf(book.getBookPubYear()));
            stmt.setBoolean(6, book.isStatus());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace(); // or use Logger
            return false;
        } finally {
            // Shutdown the MySQL cleanup thread
            try {
                AbandonedConnectionCleanupThread.checkedShutdown();
            } catch (Exception e) {
                System.err.println("Failed to shut down MySQL cleanup thread:\n "+ e.getMessage());
            }
        }

    }

    @Override
    public boolean update(Book book) {
        String sql = "UPDATE books SET cat_id=?, book_title=?, book_pages=?, book_pub_year=?, status=? WHERE book_id=?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, book.getCategory().getCategoryId());
            stmt.setString(2, book.getBookTitle());
            stmt.setInt(3, book.getBookPages());
            stmt.setDate(4, Date.valueOf(book.getBookPubYear()));
            stmt.setBoolean(5, book.isStatus());
            stmt.setInt(6, book.getBookId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM books WHERE book_id=?";

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
    public Book findById(int id) {
        String sql = "SELECT * FROM books WHERE book_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setBookTitle(rs.getString("book_title"));
                book.setBookPages(rs.getInt("book_pages"));
                book.setBookPubYear(rs.getDate("book_pub_year").toLocalDate());
                book.setStatus(rs.getBoolean("status"));
                                
                Category cat = new CategoryDAO().findById(rs.getInt("cat_id"));
                book.setCategory(cat);
                
                return book;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Book> findAll() {
        String sql = "SELECT * FROM books";
        List<Book> books = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setBookTitle(rs.getString("book_title"));
                book.setBookPages(rs.getInt("book_pages"));
                book.setBookPubYear(rs.getDate("book_pub_year").toLocalDate());
                book.setStatus(rs.getBoolean("status"));

                Category cat = new CategoryDAO().findById(rs.getInt("cat_id"));
                book.setCategory(cat);
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }
}
