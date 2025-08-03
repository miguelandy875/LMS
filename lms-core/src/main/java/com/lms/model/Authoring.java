package com.lms.model;

public class Authoring {
    private int authorId;
    private int bookId;
    private String contributionType;
    
    // Constructors
    public Authoring() {}
    
    public Authoring(int authorId, int bookId, String contributionType) {
        this.authorId = authorId;
        this.bookId = bookId;
        this.contributionType = contributionType;
    }
    
    // Getters and Setters
    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }
    
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    
    public String getContributionType() { return contributionType; }
    public void setContributionType(String contributionType) { this.contributionType = contributionType; }
    
    @Override
    public String toString() {
        return "Authoring{" +
                "authorId=" + authorId +
                ", bookId=" + bookId +
                ", contributionType='" + contributionType + '\'' +
                '}';
    }
}
