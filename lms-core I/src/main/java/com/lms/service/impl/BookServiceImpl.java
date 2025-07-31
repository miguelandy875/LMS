package com.lms.service.impl;

import com.lms.dao.BookDAO;

import com.lms.model.Book;
import com.lms.service.BookService;

import java.util.List;

public class BookServiceImpl implements BookService {

    private final BookDAO bookDAO;

    public BookServiceImpl() {
        this.bookDAO = new BookDAO(); // Or inject via constructor
    }

    @Override
    public Book getBookById(int id) {
        return bookDAO.findById(id);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookDAO.findAll();
    }

    @Override
    public boolean addBook(Book book) {
        return bookDAO.insert(book);
    }

    @Override
    public boolean updateBook(Book book) {
        return bookDAO.update(book);
    }

    @Override
    public boolean deleteBook(int id) {
        return bookDAO.delete(id);
    }
}