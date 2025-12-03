package manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import model.Loan;

public class LoanManager {
    private List<Loan> loans;

    public LoanManager() {
        this.loans = new ArrayList<>();
    }

    public void createLoanRequest(String studentUsername, String bookIsbn, LocalDate startDate, LocalDate endDate) {
        Loan loan = new Loan(studentUsername, bookIsbn, startDate, endDate);
        loans.add(loan);
    }

    public List<Loan> getPendingLoanRequests() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        
        return loans.stream()
                .filter(loan -> !loan.isApproved())
                .filter(loan -> loan.getRequestedStartDate().isEqual(today) || 
                                loan.getRequestedStartDate().isEqual(yesterday))
                .collect(Collectors.toList());
    }

    public void approveLoanRequest(String loanId, String employeeUsername) {
        // loanId is in format: student_username_book_isbn
        String[] parts = loanId.split("_");
        String studentUsername = parts[0];
        String bookIsbn = parts[2];
        
        Loan loan = loans.stream()
                .filter(l -> l.getStudentUsername().equals(studentUsername) && 
                            l.getBookIsbn().equals(bookIsbn) && 
                            !l.isApproved())
                .findFirst()
                .orElse(null);
        
        if (loan != null) {
            loan.setApproved(true);
            loan.setApprovedByEmployee(employeeUsername);
            loan.setActualStartDate(LocalDate.now());
        }
    }

    public void returnBook(String studentUsername, String bookIsbn, String employeeUsername, boolean wasLate) {
        Loan loan = loans.stream()
                .filter(l -> l.getStudentUsername().equals(studentUsername) && 
                            l.getBookIsbn().equals(bookIsbn) && 
                            l.isApproved() && 
                            !l.isReturned())
                .findFirst()
                .orElse(null);
        
        if (loan != null) {
            loan.setReturned(true);
            loan.setActualEndDate(LocalDate.now());
            loan.setReceivedByEmployee(employeeUsername);
            loan.setWasLateReturn(wasLate);
        }
    }

    public List<Loan> getStudentLoanHistory(String studentUsername) {
        return loans.stream()
                .filter(l -> l.getStudentUsername().equals(studentUsername))
                .collect(Collectors.toList());
    }

    public int getTotalLoanRequests() {
        return loans.size();
    }

    public int getTotalApprovedLoans() {
        return (int) loans.stream().filter(Loan::isApproved).count();
    }

    public int getCurrentLoansCount() {
        return (int) loans.stream()
                .filter(l -> l.isApproved() && !l.isReturned())
                .count();
    }

    public double getAverageLoanDays() {
        List<Loan> returnedLoans = loans.stream()
                .filter(l -> l.isReturned() && l.getDaysBorrowed() > 0)
                .collect(Collectors.toList());
        
        if (returnedLoans.isEmpty()) {
            return 0;
        }
        
        return returnedLoans.stream()
                .mapToLong(Loan::getDaysBorrowed)
                .average()
                .orElse(0);
    }

    public List<String> getStudentsWithMostDelays(int limit) {
        return loans.stream()
                .filter(Loan::wasLateReturn)
                .collect(Collectors.groupingBy(Loan::getStudentUsername, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(limit)
                .map(e -> e.getKey() + " - " + e.getValue() + " late returns")
                .collect(Collectors.toList());
    }

    public List<Loan> getAllLoans() {
        return loans;
    }
}

