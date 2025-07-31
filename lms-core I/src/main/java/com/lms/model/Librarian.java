package com.lms.model;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

/**
 * Librarian: manages books and issues.
 */
public class Librarian extends User {

    // === Constructor ===
    public Librarian() {
        super();
    }

    public Librarian(int userId, String userFname, String userLname, String userSex, String userEmail,
                     String userPhone, String userPwd, String userRole, boolean userStatus, Timestamp createdAt) {
        super(userId, userFname, userLname, userSex, userEmail, userPhone, userPwd, userRole, userStatus, createdAt);
    }

    // === Librarian-specific methods ===

    public void addBook(Book bk) {
        // TODO: BookDAO.create(bk)
    }

    public void updateBook(int bookId) {
        // TODO: BookDAO.update(bookId)
    }

    public List<Book> searchBook(String query) {
        // TODO: BookDAO.search(query)
        return Collections.emptyList();
    }

    public Loan issueBook(Book bk, Member borrower) {
        // TODO: LoanDAO.issue(bk, borrower)
        return null;
    }

    public void receiveReturn(Book bk) {
        // TODO: LoanDAO.markAsReturned(book)
    }

    public void deleteBook(int bookId) {
        // TODO: BookDAO.delete(bookId)
    }

    public List<Loan> viewHistory() {
        // TODO: LoanDAO.getAll()
        return Collections.emptyList();
    }

    @Override
    public void showDashboard() {
        System.out.println("Librarian Dashboard");
    }
    @Override
    public String toString() {
        return "[ID=" + getUserId() + ", Name=" + getUserFname() + " " + getUserLname() + "]";
    }
}
