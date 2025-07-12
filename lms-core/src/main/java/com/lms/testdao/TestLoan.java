package com.lms.testdao;

import java.time.LocalDate;
import java.util.List;

import com.lms.dao.LoanDAO;
import com.lms.model.Book;
import com.lms.model.Librarian;
import com.lms.model.Loan;
import com.lms.model.Member;
import com.lms.model.User;

public class TestLoan {
    public static void main(String[] args) {

            Book book = new Book();
            book.setBookId(1); // Assuming book with ID 2 exists in DB

            User borrower = new Member(); // Assuming Member is a subclass of User
            borrower.setUserId(1); // Borrower ID

            User librarian = new Librarian();
            librarian.setUserId(1); // Librarian ID
        LoanDAO dao = new LoanDAO();

        Loan loan = new Loan(2, book, borrower, librarian, LocalDate.now(), LocalDate.now().plusDays(14), false);
        dao.insert(loan);

        loan.setReturned(true);
        dao.update(loan);

        Loan found = dao.findById(2);
        System.out.println("Loan ID 2 - Returned: " + found.isReturned());

        List<Loan> all = dao.findAll();
        System.out.println("Total Loans: " + all.size());

        // dao.delete(1);
    }
}