package com.lms.model;

import java.util.List;
import java.util.ArrayList;

public class Book {
    private int bookId;
    private int categoryId;
    private String title;
    private int pages;
    private String publicationYear;
    private String status;
    private Category category;
    private List<Author> authors;
    
    // Constructors
    public Book() {
        this.authors = new ArrayList<>();
    }
    
    public Book(int categoryId, String title, int pages, String publicationYear, String status) {
        this();
        this.categoryId = categoryId;
        this.title = title;
        this.pages = pages;
        this.publicationYear = publicationYear;
        this.status = status;
    }
    
    // Getters and Setters
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }
    
    public String getPublicationYear() { return publicationYear; }
    public void setPublicationYear(String publicationYear) { this.publicationYear = publicationYear; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { 
        this.category = category;
        if (category != null) {
            this.categoryId = category.getCatId();
        }
    }
    
    public List<Author> getAuthors() { return authors; }
    public void setAuthors(List<Author> authors) { 
        this.authors = authors != null ? authors : new ArrayList<>(); 
    }
    
    public void addAuthor(Author author) {
        if (this.authors == null) {
            this.authors = new ArrayList<>();
        }
        this.authors.add(author);
    }
    
    public String getAuthorsString() {
        if (authors == null || authors.isEmpty()) {
            return "Unknown Author";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < authors.size(); i++) {
            sb.append(authors.get(i).getAuthorName());
            if (i < authors.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
    
    public String getCategoryName() {
        return category != null ? category.getCatName() : "Unknown Category";
    }
    
    public boolean isAvailable() {
        return "AVAILABLE".equalsIgnoreCase(status);
    }
    
    public boolean isIssued() {
        return "ISSUED".equalsIgnoreCase(status);
    }
    
    public boolean isReserved() {
        return "RESERVED".equalsIgnoreCase(status);
    }
    
    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", authors='" + getAuthorsString() + '\'' +
                ", category='" + getCategoryName() + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
