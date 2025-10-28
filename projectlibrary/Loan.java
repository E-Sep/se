package ap.projects.finalproject;

import java.time.LocalDate;
import java.time.temporal.ChronUnit;

public class Loan {
    private String studentUsername;
    private String bookIsbn;
    private LocalDate requestedStartDate;
    private LocalDate requestedEndDate;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;
    private String approvedByEmployee;
    private String receivedByEmployee;
    private boolean isApproved;
    private boolean isReturned;
    private boolean wasLateReturn;

    public Loan(String studentUsername, String bookIsbn, LocalDate requestedStartDate, LocalDate requestedEndDate) {
        this.studentUsername = studentUsername;
        this.bookIsbn = bookIsbn;
        this.requestedStartDate = requestedStartDate;
        this.requestedEndDate = requestedEndDate;
        this.isApproved = false;
        this.isReturned = false;
        this.wasLateReturn = false;
    }

    // Getters and Setters
    public String getStudentUsername() {
        return studentUsername;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public LocalDate getRequestedStartDate() {
        return requestedStartDate;
    }

    public LocalDate getRequestedEndDate() {
        return requestedEndDate;
    }

    public LocalDate getActualStartDate() {
        return actualStartDate;
    }

    public void setActualStartDate(LocalDate actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public LocalDate getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(LocalDate actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public String getApprovedByEmployee() {
        return approvedByEmployee;
    }

    public void setApprovedByEmployee(String approvedByEmployee) {
        this.approvedByEmployee = approvedByEmployee;
    }

    public String getReceivedByEmployee() {
        return receivedByEmployee;
    }

    public void setReceivedByEmployee(String receivedByEmployee) {
        this.receivedByEmployee = receivedByEmployee;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }

    public boolean wasLateReturn() {
        return wasLateReturn;
    }

    public void setWasLateReturn(boolean wasLateReturn) {
        this.wasLateReturn = wasLateReturn;
    }

    public long getDaysBorrowed() {
        if (actualStartDate != null && actualEndDate != null) {
            return ChronUnit.DAYS.between(actualStartDate, actualEndDate);
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Loan: " + bookIsbn + " by " + studentUsername +
                " | Requested: " + requestedStartDate + " to " + requestedEndDate +
                " | Approved: " + isApproved +
                " | Returned: " + isReturned;
    }
}

