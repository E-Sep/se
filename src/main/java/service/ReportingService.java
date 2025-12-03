package service;

import manager.BookManager;
import manager.LoanManager;
import manager.StudentManager;
import model.LibraryStats;
import model.Loan;
import model.StudentReport;

import java.util.List;

public class ReportingService {
    private StudentManager studentManager;
    private BookManager bookManager;
    private LoanManager loanManager;

    public ReportingService(StudentManager studentManager, BookManager bookManager, LoanManager loanManager) {
        this.studentManager = studentManager;
        this.bookManager = bookManager;
        this.loanManager = loanManager;
    }

    public StudentReport generateStudentReport(String studentUsername) {
        List<Loan> loans = loanManager.getStudentLoanHistory(studentUsername);
        
        int totalLoans = loans.size();
        int undeliveredBooks = (int) loans.stream()
                .filter(l -> l.isApproved() && !l.isReturned())
                .count();
        int lateLoans = (int) loans.stream()
                .filter(Loan::wasLateReturn)
                .count();
        
        return new StudentReport(studentUsername, totalLoans, undeliveredBooks, lateLoans);
    }

    public LibraryStats generateLibraryStats() {
        double averageLoanDays = loanManager.getAverageLoanDays();
        int totalBooks = bookManager.getTotalBooksCount();
        int totalLoans = loanManager.getTotalLoanRequests();
        int activeLoans = loanManager.getCurrentLoansCount();
        
        return new LibraryStats(averageLoanDays, totalBooks, totalLoans, activeLoans);
    }
}

