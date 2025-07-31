package com.lms.model;

public class Category {

    // === Fields ===
    private int categoryId;
    private String categoryName;
  

    // === Constructors ===
    public Category() {
        // Default constructor
    }

    public Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        
    }

    // === Getters and Setters ===

    public int getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

}