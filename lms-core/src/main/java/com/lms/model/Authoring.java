package com.lms.model;

import java.io.Serializable;

/**
 * Association class representing the many-to-many relationship 
 * between Book and Author.
 */
public class Authoring implements Serializable {
    private static final long serialVersionUID = 1L;

    private Book book;
    private Author author;
    private String contributionType;

    public Authoring() {
        // Default constructor
    }

    public Authoring(Book book, Author author, String contributionType) {
        this.book = book;
        this.author = author;
        this.contributionType = contributionType;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getContributionType() {
        return contributionType;
    }

    public void setContributionType(String contributionType) {
        this.contributionType = contributionType;
    }

    @Override
    public String toString() {
        return "Authoring[BookID=" + book.getBookId() +
               ", AuthorID=" + author.getAuthorId() +
               ", ContributionType=" + contributionType + "]";
    }
}