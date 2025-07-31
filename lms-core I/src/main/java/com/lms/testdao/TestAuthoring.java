package com.lms.testdao;

import com.lms.dao.AuthoringDAO;
import com.lms.model.Author;
import com.lms.model.Authoring;
import com.lms.model.Book;

import java.util.List;

public class TestAuthoring {
    public static void main(String[] args) {
        Book book = new Book();
        book.setBookId(1); // Set valid book ID

        Author author = new Author();
        author.setAuthorId(1); // Set valid author ID

        Authoring authoring = new Authoring(book, author, "Main Author");

        AuthoringDAO dao = new AuthoringDAO();

        if (dao.insert(authoring)) {
            System.out.println("Inserted: " + authoring);
        } else {
            System.out.println("Insert failed.");
        }

        List<Authoring> byBook = dao.findByBookId(1);
        System.out.println("Authorings for Book ID 1: ");
        byBook.forEach(System.out::println);

        // if (dao.delete(authoring)) {
        //     System.out.println("Deleted successfully.");
        // } else {
        //     System.out.println("Delete failed.");
        // }
    }
}