package com.lms.dao;

import com.lms.model.Category;
import com.lms.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM category ORDER BY cat_name";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                categories.add(new Category(rs.getInt("cat_id"), rs.getString("cat_name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading categories: " + e.getMessage(), e);
        }
        return categories;
    }
    // Add more CRUD methods as needed
}
