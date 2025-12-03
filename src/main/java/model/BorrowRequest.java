package model;

import java.time.LocalDate;

public class BorrowRequest {
    private String studentUsername;
    private String bookIsbn;
    private LocalDate requestedStartDate;
    private LocalDate requestedEndDate;
    private RequestStatus status;

    public enum RequestStatus {
        PENDING, APPROVED, REJECTED
    }

    public BorrowRequest(String studentUsername, String bookIsbn, 
                        LocalDate requestedStartDate, LocalDate requestedEndDate) {
        this.studentUsername = studentUsername;
        this.bookIsbn = bookIsbn;
        this.requestedStartDate = requestedStartDate;
        this.requestedEndDate = requestedEndDate;
        this.status = RequestStatus.PENDING;
    }

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

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}

