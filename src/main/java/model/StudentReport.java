package model;

public class StudentReport {
    private String studentUsername;
    private int totalLoans;
    private int undeliveredBooks;
    private int lateLoans;

    public StudentReport(String studentUsername, int totalLoans, 
                        int undeliveredBooks, int lateLoans) {
        this.studentUsername = studentUsername;
        this.totalLoans = totalLoans;
        this.undeliveredBooks = undeliveredBooks;
        this.lateLoans = lateLoans;
    }

    public String getStudentUsername() {
        return studentUsername;
    }

    public int getTotalLoans() {
        return totalLoans;
    }

    public int getUndeliveredBooks() {
        return undeliveredBooks;
    }

    public int getLateLoans() {
        return lateLoans;
    }

    @Override
    public String toString() {
        return "StudentReport{" +
                "studentUsername='" + studentUsername + '\'' +
                ", totalLoans=" + totalLoans +
                ", undeliveredBooks=" + undeliveredBooks +
                ", lateLoans=" + lateLoans +
                '}';
    }
}

