package com.lms.model;

import java.sql.Timestamp;

import java.util.Collections;
import java.util.List;

/**
 * Member (Borrower): can search, borrow, and reserve.
 */
public class Member extends User {

    // === Constructor ===
    public Member() {
        super();
    }

    public Member(int userId, String userFname, String userLname, String userSex, String userEmail,
            String userPhone, String userPwd, String userRole, boolean userStatus, Timestamp createdAt) {
        super(userId, userFname, userLname, userSex, userEmail, userPhone, userPwd, userRole, userStatus, createdAt);
    }

    // === Member-specific methods ===

    public void register(User usr) {
        // TODO: UserDAO.create(usr)
    }

    public Loan borrowBook(Book book) {
        // TODO: LoanDAO.create()
        return null;
    }

    public Reservation reserveSeat(Seat st) {
        // TODO: ReservationDAO.create()
        return null;
    }

    public List<Loan> viewHistory() {
        // TODO: LoanDAO.getLoansByUser(this.getUserId())
        return Collections.emptyList();
    }

    public List<Book> searchBook(String query) {
        // TODO: BookDAO.search(query)
        return Collections.emptyList();
    }

    public void returnBook(Book bk) {
        // TODO: LoanDAO.markAsReturned()
    }

    @Override
    public void showDashboard() {
        System.out.println("Member Dashboard");
    }

    @Override
    public String toString() {
        return "[ID=" + getUserId() + ", Name=" + getUserFname() + " " + getUserLname() + "]";
    }
}
