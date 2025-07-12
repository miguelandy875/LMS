package com.lms.service.impl;

import com.lms.dao.CategoryDAO;

import com.lms.model.Category;
import com.lms.service.CategoryService;

import java.util.List;

public class CategoryServiceImpl implements CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryServiceImpl() {
        this.categoryDAO = new CategoryDAO(); // or pass via constructor if DI
    }

    @Override
    public Category getCategoryById(int id) {
        return categoryDAO.findById(id);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    @Override
    public boolean addCategory(Category category) {
        return categoryDAO.insert(category);
    }

    @Override
    public boolean updateCategory(Category category) {
        return categoryDAO.update(category);
    }

    @Override
    public boolean deleteCategory(int id) {
        return categoryDAO.delete(id);
    }
}

