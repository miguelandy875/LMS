package com.lms.test.service;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.lms.model.Author;
import com.lms.service.AuthorService;
import com.lms.service.impl.AuthorServiceImpl;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthorServiceTest {

    private static AuthorService authorService;

    @BeforeAll
    public static void setup() {
        authorService = new AuthorServiceImpl();
    }

    @Test
    @Order(1)
    public void testAddAuthor() {
        Author author = new Author(1, "Stephen King");
        Assertions.assertTrue(authorService.addAuthor(author));
    }

    @Test
    @Order(2)
    public void testGetAuthorById() {
        Author author = authorService.getAuthorById(1);
        Assertions.assertNotNull(author);
    }

    @Test
    @Order(3)
    public void testUpdateAuthor() {
        Author author = authorService.getAuthorById(1);
        author.setName("stephen keen");
        Assertions.assertTrue(authorService.updateAuthor(author));
    }

    @Test
    @Order(4)
    public void testGetAllAuthors() {
        List<Author> authors = authorService.getAllAuthors();
        Assertions.assertFalse(authors.isEmpty());
    }

    @Test
    @Order(5)
    public void testDeleteAuthor() {
        Assertions.assertTrue(authorService.deleteAuthor(1));
    }
}