package com.lms.testdao;

import com.lms.dao.AuthorDAO;
import com.lms.model.Author;

import java.util.List;

public class TestAuthor {
    public static void main(String[] args) {
        AuthorDAO dao = new AuthorDAO();

        // Insert
        Author a1 = new Author(1, "George Orwell");
        dao.insert(a1);

        // Update
        //a1.setAuthorBio("Author of '1984' and 'Animal Farm'");
        //dao.update(a1);

        // Find by ID
        Author found = dao.findById(1);
        System.out.println("Found: " + found.getName());

        // Find all
        List<Author> authors = dao.findAll();
        authors.forEach(a -> System.out.println(a.getAuthorId() + " - " + a.getName()));

        // Delete
        // dao.delete(1);
    }
}