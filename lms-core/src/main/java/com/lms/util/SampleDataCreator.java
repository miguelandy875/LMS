package com.lms.util;

import com.lms.dao.BookDAO;
import com.lms.dao.LoanDAO;
import com.lms.dao.UserDAO;
import com.lms.model.Book;
import com.lms.model.Loan;
import com.lms.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class SampleDataCreator {
    
    public static void main(String[] args) {
        System.out.println("Starting sample data creation...");
        createRealisticSampleLoans();
        System.out.println("Sample data creation completed.");
    }

    public static void createRealisticSampleLoans() {
        LoanDAO loanDAO = new LoanDAO();
        UserDAO userDAO = new UserDAO();
        BookDAO bookDAO = new BookDAO();
        Random random = new Random();

        try {
            List<User> members = userDAO.findByRole("MEMBER");
            List<User> librarians = userDAO.findByRole("LIBRARIAN");
            List<Book> availableBooks = bookDAO.findByStatus("AVAILABLE");

            if (members.isEmpty() || librarians.isEmpty() || availableBooks.isEmpty()) {
                System.out.println("Insufficient data to create realistic loans.");
                return;
            }

            User librarian = librarians.get(0);

            // Create various loan scenarios
            for (int i = 0; i < Math.min(10, availableBooks.size()); i++) {
                User randomMember = members.get(random.nextInt(members.size()));
                Book randomBook = availableBooks.get(i);

                Loan loan = new Loan();
                loan.setUserId(randomMember.getUserId());
                loan.setBookId(randomBook.getBookId());
                loan.setUserIdIssue(librarian.getUserId());

                // Create different loan statuses
                int scenario = random.nextInt(4);
                switch (scenario) {
                    case 0: // Active loan
                        loan.setLoanIssueDate(LocalDate.now().minusDays(random.nextInt(10) + 1));
                        loan.setLoanReturnDate(LocalDate.now().plusDays(random.nextInt(10) + 5));
                        loan.setReturned(false);
                        randomBook.setStatus("ISSUED");
                        break;
                    case 1: // Overdue loan
                        loan.setLoanIssueDate(LocalDate.now().minusDays(random.nextInt(20) + 15));
                        loan.setLoanReturnDate(LocalDate.now().minusDays(random.nextInt(5) + 1));
                        loan.setReturned(false);
                        randomBook.setStatus("ISSUED");
                        break;
                    case 2: // Due soon loan
                        loan.setLoanIssueDate(LocalDate.now().minusDays(random.nextInt(10) + 10));
                        loan.setLoanReturnDate(LocalDate.now().plusDays(random.nextInt(2) + 1));
                        loan.setReturned(false);
                        randomBook.setStatus("ISSUED");
                        break;
                    case 3: // Returned loan
                        loan.setLoanIssueDate(LocalDate.now().minusDays(random.nextInt(30) + 15));
                        loan.setLoanReturnDate(LocalDate.now().minusDays(random.nextInt(10) + 5));
                        loan.setReturned(true);
                        // Book remains available
                        break;
                }

                loanDAO.save(loan);
                if (!loan.isReturned()) {
                    bookDAO.update(randomBook);
                }
            }

            System.out.println("Realistic sample loans created successfully!");

        } catch (Exception e) {
            System.err.println("Error creating realistic sample loans: " + e.getMessage());
            e.printStackTrace();
        }
    }
}