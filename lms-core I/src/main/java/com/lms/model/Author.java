package com.lms.model;

import java.io.Serializable;

/**
 * Represents an author who writes books in the system.
 */
public class Author implements Serializable {

    private int authorId;
    private String name;
  

    // === Constructors ===

    /**
     * Default constructor for creating an empty Author object.
     */
    public Author() {
        // Default constructor
    }
    
    // Parameterized constructor
    public Author(int authorId, String name) {
        this.authorId = authorId;
        this.name = name;
       
    }

    // === Getters and Setters ===

   

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


   
}