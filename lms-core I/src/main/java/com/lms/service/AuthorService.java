package com.lms.service;

import com.lms.model.Author;
import java.util.List;

public interface AuthorService {
    Author getAuthorById(int id);
    List<Author> getAllAuthors();
    boolean addAuthor(Author author);
    boolean updateAuthor(Author author);
    boolean deleteAuthor(int id);
}