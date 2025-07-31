package com.lms.testdao;

import com.lms.dao.BookDAO;
import com.lms.model.Book;
import com.lms.model.Category;

import java.sql.SQLException;
import java.time.LocalDate;

public class TestBook {

    public static void main(String[] args) throws SQLException {

        BookDAO bookDAO = new BookDAO();

        // ==== INSERT Test ====
        Book book = new Book();
        book.setBookId(1);  // Make sure it's unique or use auto_increment in DB
        book.setBookTitle("The Pragmatic Programmer");
        book.setBookPages(352);
        book.setBookPubYear(LocalDate.of(1999, 10, 30));
        book.setStatus(true);

        // Dummy Category (make sure it exists in DB)
        Category category = new Category();
        category.setCategoryId(1); // Assume category with ID 1 exists
        book.setCategory(category);

        boolean inserted = bookDAO.insert(book);
        System.out.println("Inserted: " + inserted);

        // ==== FIND Test ====
        Book found = bookDAO.findById(1);
        if (found != null) {
            System.out.println("Found Book: " + found.getBookTitle());
        } else {
            System.out.println("Book not found!");
        }

        // ==== UPDATE Test ====
        found.setBookTitle("The Pragmatic Programmer - Updated");
        boolean updated = bookDAO.update(found);
        System.out.println("Updated: " + updated);

        // ==== DELETE Test ====
        // boolean deleted = bookDAO.delete(1);
        // System.out.println("Deleted: " + deleted);
    }
}
