// LibrarySystem.java
import manager.BookManager;
import manager.EmployeeManager;
import manager.LoanManager;
import manager.StudentManager;
import model.Book;
import model.Student;

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

    // ==== Methods used by MenuHandler ====
    public void editStudentInformation(Student student) {
        System.out.println("Edit student information is not implemented yet.");
    }

    public void borrowBook(Student student) {
        System.out.println("Borrow book is not implemented yet.");
    }

    public void returnBook(Student student) {
        System.out.println("Return book is not implemented yet.");
    }

    public void displayAvailableBooks() {
        System.out.println("--- Available Books ---");
        for (Book book : bookManager.getAllBooks()) {
            System.out.println(book);
        }
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
