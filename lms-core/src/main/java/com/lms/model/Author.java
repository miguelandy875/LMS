package com.lms.model;

public class Author {
    private int authorId;
    private String authorName;
    
    // Constructors
    public Author() {}
    
    public Author(String authorName) {
        this.authorName = authorName;
    }
    
    public Author(int authorId, String authorName) {
        this.authorId = authorId;
        this.authorName = authorName;
    }
    
    // Getters and Setters
    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }
    
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    
    @Override
    public String toString() {
        return authorName;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Author author = (Author) obj;
        return authorId == author.authorId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(authorId);
    }
}