package com.lms.dao;

import com.lms.model.Author;
import com.lms.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthorDAO implements GenericDAO<Author> {
    
    private static final String INSERT_AUTHOR = 
        "INSERT INTO author (author_name) VALUES (?)";
    
    private static final String UPDATE_AUTHOR = 
        "UPDATE author SET author_name=? WHERE author_id=?";
    
    private static final String DELETE_AUTHOR = 
        "DELETE FROM author WHERE author_id=?";
    
    private static final String SELECT_AUTHOR_BY_ID = 
        "SELECT * FROM author WHERE author_id=?";
    
    private static final String SELECT_ALL_AUTHORS = 
        "SELECT * FROM author ORDER BY author_name";
    
    private static final String SELECT_AUTHOR_BY_NAME = 
        "SELECT * FROM author WHERE LOWER(author_name) = LOWER(?)";
    
    private static final String SEARCH_AUTHORS = 
        "SELECT * FROM author WHERE LOWER(author_name) LIKE LOWER(?) ORDER BY author_name";
    
    private static final String COUNT_AUTHORS = 
        "SELECT COUNT(*) FROM author";
    
    private static final String COUNT_BOOKS_BY_AUTHOR = 
        "SELECT COUNT(*) FROM authoring WHERE author_id=?";

    @Override
    public void save(Author author) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_AUTHOR, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, author.getAuthorName());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        author.setAuthorId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving author: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Author author) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_AUTHOR)) {
            
            statement.setString(1, author.getAuthorName());
            statement.setInt(2, author.getAuthorId());
            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating author: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int authorId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_AUTHOR)) {
            
            statement.setInt(1, authorId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting author: " + e.getMessage(), e);
        }
    }

    @Override
    public Author findById(int authorId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_AUTHOR_BY_ID)) {
            
            statement.setInt(1, authorId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToAuthor(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding author by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Author> findAll() {
        List<Author> authors = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_AUTHORS);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                authors.add(mapResultSetToAuthor(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all authors: " + e.getMessage(), e);
        }
        
        return authors;
    }
    
    public Author findByName(String name) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_AUTHOR_BY_NAME)) {
            
            statement.setString(1, name);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToAuthor(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding author by name: " + e.getMessage(), e);
        }
        return null;
    }
    
    public List<Author> searchAuthors(String searchTerm) {
        List<Author> authors = new ArrayList<>();
        String searchPattern = "%" + searchTerm + "%";
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SEARCH_AUTHORS)) {
            
            statement.setString(1, searchPattern);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    authors.add(mapResultSetToAuthor(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching authors: " + e.getMessage(), e);
        }
        
        return authors;
    }
    
    public int getTotalAuthors() {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_AUTHORS);
             ResultSet resultSet = statement.executeQuery()) {
            
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting authors: " + e.getMessage(), e);
        }
        return 0;
    }
    
    public int getBookCountByAuthor(int authorId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_BOOKS_BY_AUTHOR)) {
            
            statement.setInt(1, authorId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting books by author: " + e.getMessage(), e);
        }
        return 0;
    }
    
    public Author findOrCreate(String authorName) {
        // First try to find existing author
        Author existingAuthor = findByName(authorName);
        if (existingAuthor != null) {
            return existingAuthor;
        }
        
        // Create new author
        Author newAuthor = new Author(authorName);
        save(newAuthor);
        return newAuthor;
    }
    
    private Author mapResultSetToAuthor(ResultSet resultSet) throws SQLException {
        Author author = new Author();
        author.setAuthorId(resultSet.getInt("author_id"));
        author.setAuthorName(resultSet.getString("author_name"));
        return author;
    }
}