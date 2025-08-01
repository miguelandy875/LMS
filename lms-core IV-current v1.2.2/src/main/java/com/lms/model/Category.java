package com.lms.model;

public class Category {
    private int catId;
    private String catName;
    
    // Constructors
    public Category() {}
    
    public Category(String catName) {
        this.catName = catName;
    }
    
    public Category(int catId, String catName) {
        this.catId = catId;
        this.catName = catName;
    }
    
    // Getters and Setters
    public int getCatId() { return catId; }
    public void setCatId(int catId) { this.catId = catId; }
    
    public String getCatName() { return catName; }
    public void setCatName(String catName) { this.catName = catName; }
    
    @Override
    public String toString() {
        return catName;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Category category = (Category) obj;
        return catId == category.catId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(catId);
    }
}