package com.lms.test.service;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.lms.model.Book;
import com.lms.model.Category;
import com.lms.service.BookService;
import com.lms.service.impl.BookServiceImpl;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookServiceTest {

    private static BookService bookService;

    @BeforeAll
    public static void setup() {
        bookService = new BookServiceImpl();
    }

    @Test
    @Order(1)
    public void testAddBook() {
        Category category = new Category();
        category.setCategoryId(1); // Existing or dummy category
        category.setCategoryName("cqt");
    
        Book book = new Book(1, "rgtrg", 90, LocalDate.of(1999, 10, 30), true, category);
        Assertions.assertTrue(bookService.addBook(book));
    }

    @Test
    @Order(2)
    public void testGetBookById() {
        Book book = bookService.getBookById(1);
        Assertions.assertNotNull(book);
    }

    @Test
    @Order(3)
    public void testUpdateBook() {
        Book book = bookService.getBookById(1);
        book.setBookTitle("Updated Title");
        Assertions.assertTrue(bookService.updateBook(book));
    }

    @Test
    @Order(4)
    public void testGetAllBooks() {
        List<Book> books = bookService.getAllBooks();
        Assertions.assertFalse(books.isEmpty());
    }

    @Test
    @Order(5)
    public void testDeleteBook() {
        Assertions.assertTrue(bookService.deleteBook(1));
    }
}
