package com.lms.test.service;

import com.lms.model.Category;
import com.lms.service.CategoryService;
import com.lms.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.*;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryServiceTest {

    private static CategoryService categoryService;

    @BeforeAll
    public static void setup() {
        categoryService = new CategoryServiceImpl();
    }

    @Test
    @Order(1)
    public void testAddCategory() {
        Category category = new Category(1, "Science");
        Assertions.assertTrue(categoryService.addCategory(category));
    }

    @Test
    @Order(2)
    public void testGetCategoryById() {
        Category category = categoryService.getCategoryById(1);
        Assertions.assertNotNull(category);
    }

    @Test
    @Order(3)
    public void testUpdateCategory() {
        Category category = categoryService.getCategoryById(1);
        category.setCategoryName("Updated Science");
        Assertions.assertTrue(categoryService.updateCategory(category));
    }

    @Test
    @Order(4)
    public void testGetAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        Assertions.assertFalse(categories.isEmpty());
    }

    @Test
    @Order(5)
    public void testDeleteCategory() {
        Assertions.assertTrue(categoryService.deleteCategory(1));
    }
}
