package com.lms.service;

import com.lms.model.Category;
import java.util.List;

public interface CategoryService {
    Category getCategoryById(int id);
    List<Category> getAllCategories();
    boolean addCategory(Category category);
    boolean updateCategory(Category category);
    boolean deleteCategory(int id);
}