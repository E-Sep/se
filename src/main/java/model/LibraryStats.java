package model;

public class LibraryStats {
    private double averageLoanDays;
    private int totalBooks;
    private int totalLoans;
    private int activeLoans;

    public LibraryStats(double averageLoanDays, int totalBooks, 
                       int totalLoans, int activeLoans) {
        this.averageLoanDays = averageLoanDays;
        this.totalBooks = totalBooks;
        this.totalLoans = totalLoans;
        this.activeLoans = activeLoans;
    }

    public double getAverageLoanDays() {
        return averageLoanDays;
    }

    public int getTotalBooks() {
        return totalBooks;
    }

    public int getTotalLoans() {
        return totalLoans;
    }

    public int getActiveLoans() {
        return activeLoans;
    }

    @Override
    public String toString() {
        return "LibraryStats{" +
                "averageLoanDays=" + averageLoanDays +
                ", totalBooks=" + totalBooks +
                ", totalLoans=" + totalLoans +
                ", activeLoans=" + activeLoans +
                '}';
    }
}

