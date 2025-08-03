package com.lms.dao;

import com.lms.model.Category;
import com.lms.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO implements GenericDAO<Category> {
    
    private static final String INSERT_CATEGORY = 
        "INSERT INTO categories (cat_name) VALUES (?)";
    
    private static final String UPDATE_CATEGORY = 
        "UPDATE categories SET cat_name=? WHERE cat_id=?";
    
    private static final String DELETE_CATEGORY = 
        "DELETE FROM categories WHERE cat_id=?";
    
    private static final String SELECT_CATEGORY_BY_ID = 
        "SELECT * FROM categories WHERE cat_id=?";
    
    private static final String SELECT_ALL_CATEGORIES = 
        "SELECT * FROM categories ORDER BY cat_name";
    
    private static final String SELECT_CATEGORY_BY_NAME = 
        "SELECT * FROM categories WHERE LOWER(cat_name) = LOWER(?)";
    
    private static final String SEARCH_CATEGORIES = 
        "SELECT * FROM categories WHERE LOWER(cat_name) LIKE LOWER(?) ORDER BY cat_name";
    
    private static final String COUNT_CATEGORIES = 
        "SELECT COUNT(*) FROM categories";
    
    private static final String COUNT_BOOKS_BY_CATEGORY = 
        "SELECT COUNT(*) FROM books WHERE cat_id=?";

    @Override
    public void save(Category category) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_CATEGORY, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, category.getCatName());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        category.setCatId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving category: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Category category) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_CATEGORY)) {
            
            statement.setString(1, category.getCatName());
            statement.setInt(2, category.getCatId());
            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating category: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int categoryId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_CATEGORY)) {
            
            statement.setInt(1, categoryId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting category: " + e.getMessage(), e);
        }
    }

    @Override
    public Category findById(int categoryId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_CATEGORY_BY_ID)) {
            
            statement.setInt(1, categoryId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToCategory(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding category by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_CATEGORIES);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                categories.add(mapResultSetToCategory(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all categories: " + e.getMessage(), e);
        }
        
        return categories;
    }
    
    public Category findByName(String name) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_CATEGORY_BY_NAME)) {
            
            statement.setString(1, name);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToCategory(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding category by name: " + e.getMessage(), e);
        }
        return null;
    }
    
    public List<Category> searchCategories(String searchTerm) {
        List<Category> categories = new ArrayList<>();
        String searchPattern = "%" + searchTerm + "%";
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SEARCH_CATEGORIES)) {
            
            statement.setString(1, searchPattern);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    categories.add(mapResultSetToCategory(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching categories: " + e.getMessage(), e);
        }
        
        return categories;
    }
    
    public int getTotalCategories() {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_CATEGORIES);
             ResultSet resultSet = statement.executeQuery()) {
            
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting categories: " + e.getMessage(), e);
        }
        return 0;
    }
    
    public int getBookCountByCategory(int categoryId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_BOOKS_BY_CATEGORY)) {
            
            statement.setInt(1, categoryId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting books by category: " + e.getMessage(), e);
        }
        return 0;
    }
    
    public Category findOrCreate(String categoryName) {
        // First try to find existing category
        Category existingCategory = findByName(categoryName);
        if (existingCategory != null) {
            return existingCategory;
        }
        
        // Create new category
        Category newCategory = new Category(categoryName);
        save(newCategory);
        return newCategory;
    }
    
    private Category mapResultSetToCategory(ResultSet resultSet) throws SQLException {
        Category category = new Category();
        category.setCatId(resultSet.getInt("cat_id"));
        category.setCatName(resultSet.getString("cat_name"));
        return category;
    }
}