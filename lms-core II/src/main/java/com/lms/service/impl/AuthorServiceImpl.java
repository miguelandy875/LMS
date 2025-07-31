package com.lms.service.impl;

import com.lms.dao.AuthorDAO;

import com.lms.model.Author;
import com.lms.service.AuthorService;

import java.util.List;

public class AuthorServiceImpl implements AuthorService {

    private final AuthorDAO authorDAO;

    public AuthorServiceImpl() {
        this.authorDAO = new AuthorDAO();
    }

    @Override
    public Author getAuthorById(int id) {
        return authorDAO.findById(id);
    }

    @Override
    public List<Author> getAllAuthors() {
        return authorDAO.findAll();
    }

    @Override
    public boolean addAuthor(Author author) {
        return authorDAO.insert(author);
    }

    @Override
    public boolean updateAuthor(Author author) {
        return authorDAO.update(author);
    }

    @Override
    public boolean deleteAuthor(int id) {
        return authorDAO.delete(id);
    }
}
