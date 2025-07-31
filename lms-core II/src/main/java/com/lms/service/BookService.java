package com.lms.service;

import com.lms.model.Book;
import java.util.List;

public interface BookService {
    Book getBookById(int id);
    List<Book> getAllBooks();
    boolean addBook(Book book);
    boolean updateBook(Book book);
    boolean deleteBook(int id);
}
