package com.lms.service;

import com.lms.model.Loan;
import java.util.List;

public interface LoanService {
    void addLoan(Loan loan);
    void updateLoan(Loan loan);
    void deleteLoan(int loanId);
    Loan findLoanById(int loanId);
    List<Loan> findAllLoans();
}
