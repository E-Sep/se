package ap.projects.finalproject;

// LibrarySystem.java
public class LibrarySystem {
    private StudentManager studentManager;
    private BookManager bookManager;
    private EmployeeManager employeeManager;
    private LoanManager loanManager;
    private MenuHandler menuHandler;

    public LibrarySystem() {
        this.studentManager = new StudentManager();
        this.bookManager = new BookManager();
        this.employeeManager = new EmployeeManager();
        this.loanManager = new LoanManager();
        this.menuHandler = new MenuHandler(this);
    }

    // Student Manager getters
    public StudentManager getStudentManager() {
        return studentManager;
    }

    public int getStudentCount() {
        return this.studentManager.getStudentCount();
    }

    public void registerStudent(String name, String studentId, String username, String password) {
        studentManager.registerStudent(name, studentId, username, password);
    }

    public Student authenticateStudent(String username, String password) {
        return studentManager.authenticateStudent(username, password);
    }

    // Book Manager getters
    public BookManager getBookManager() {
        return bookManager;
    }

    // Employee Manager getters
    public EmployeeManager getEmployeeManager() {
        return employeeManager;
    }

    // Loan Manager getters
    public LoanManager getLoanManager() {
        return loanManager;
    }

    public void start() {
        menuHandler.displayMainMenu();
    }

    public static void main(String[] args) {
        LibrarySystem system = new LibrarySystem();
        system.start();
    }
}
