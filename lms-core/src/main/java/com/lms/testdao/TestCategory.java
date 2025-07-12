package com.lms.testdao;

import com.lms.dao.CategoryDAO;
import com.lms.model.Category;

import java.util.List;

public class TestCategory {
    public static void main(String[] args) {
        CategoryDAO dao = new CategoryDAO();

        Category cat = new Category(1, "Sci-Fi");
        dao.insert(cat);

        cat.setCategoryName("Science Fiction");
        dao.update(cat);

        Category found = dao.findById(1);
        System.out.println("Found: " + found.getCategoryName());

        List<Category> all = dao.findAll();
        System.out.println("All categories: " + all.size());

        dao.delete(1);
    }
}