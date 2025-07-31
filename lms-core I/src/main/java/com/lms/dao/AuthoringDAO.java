package com.lms.dao;

import com.lms.model.Author;
import com.lms.model.Authoring;
import com.lms.model.Book;
import com.lms.utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthoringDAO {

    public boolean insert(Authoring authoring) {
        String sql = "INSERT INTO authoring (author_id, book_id, CONTRIBUTION_TYPE) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, authoring.getAuthor().getAuthorId());
            stmt.setInt(2, authoring.getBook().getBookId());
            stmt.setString(3, authoring.getContributionType());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Authoring authoring) {
        String sql = "DELETE FROM authoring WHERE author_id=? AND book_id=?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, authoring.getAuthor().getAuthorId());
            stmt.setInt(2, authoring.getBook().getBookId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Authoring> findByBookId(int bookId) {
        String sql = "SELECT * FROM authoring WHERE book_id=?";
        List<Authoring> list = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Author author = new Author();
                author.setAuthorId(rs.getInt("author_id"));

                Book book = new Book();
                book.setBookId(bookId);

                Authoring authoring = new Authoring(book, author, rs.getString("CONTRIBUTION_TYPE"));
                list.add(authoring);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Authoring> findByAuthorId(int authorId) {
        String sql = "SELECT * FROM authoring WHERE author_id=?";
        List<Authoring> list = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, authorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Author author = new Author();
                author.setAuthorId(authorId);

                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));

                Authoring authoring = new Authoring(book, author, rs.getString("CONTRIBUTION_TYPE"));
                list.add(authoring);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Authoring> findAll() {
        String sql = "SELECT * FROM authoring";
        List<Authoring> list = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Author author = new Author();
                author.setAuthorId(rs.getInt("author_id"));

                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));

                Authoring authoring = new Authoring(book, author, rs.getString("CONTRIBUTION_TYPE"));
                list.add(authoring);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}