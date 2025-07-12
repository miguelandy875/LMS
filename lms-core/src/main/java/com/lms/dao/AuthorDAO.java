package com.lms.dao;

import com.lms.model.Author;
import com.lms.utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthorDAO implements GenericDAO<Author> {

    @Override
    public boolean insert(Author author) {
        String sql = "INSERT INTO author (author_id, author_name) VALUES (?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, author.getAuthorId());
            stmt.setString(2, author.getName());
          

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Author author) {
        String sql = "UPDATE author SET author_name=?, WHERE author_id=?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, author.getName());
       
            stmt.setInt(3, author.getAuthorId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM author WHERE author_id=?";

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
    public Author findById(int id) {
        String sql = "SELECT * FROM author WHERE author_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Author author = new Author();
                author.setAuthorId(rs.getInt("author_id"));
                author.setName(rs.getString("author_name"));
                
                return author;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Author> findAll() {
        String sql = "SELECT * FROM author";
        List<Author> authors = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Author author = new Author();
                author.setAuthorId(rs.getInt("author_id"));
                author.setName(rs.getString("author_name"));
                
                authors.add(author);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return authors;
    }
}
