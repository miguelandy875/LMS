package com.lms.service.impl;

import com.lms.dao.LoanDAO;
import com.lms.model.Loan;
import com.lms.service.LoanService;

import java.util.List;

public class LoanServiceImpl implements LoanService {

    private final LoanDAO loanDAO;

    public LoanServiceImpl() {
        this.loanDAO = new LoanDAO();
    }

    @Override
    public void addLoan(Loan loan) {
        loanDAO.insert(loan);
    }

    @Override
    public void updateLoan(Loan loan) {
        loanDAO.update(loan);
    }

    @Override
    public void deleteLoan(int loanId) {
        loanDAO.delete(loanId);
    }

    @Override
    public Loan findLoanById(int loanId) {
        return loanDAO.findById(loanId);
    }

    @Override
    public List<Loan> findAllLoans() {
        return loanDAO.findAll();
    }
}
