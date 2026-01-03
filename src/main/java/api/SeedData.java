package api;

import system.LibrarySystem;
import model.Book;

public class SeedData {
    public static void seed(LibrarySystem system) {
        // Seed Employees
        system.getEmployeeManager().addEmployee("emp1", "1234");
        system.getEmployeeManager().addEmployee("emp2", "1234");
        system.getEmployeeManager().addEmployee("emp3", "1234");

        // Seed Students
        system.getStudentManager().registerStudent("st1", "1", "st1", "1");
        system.getStudentManager().registerStudent("Ali Ahmadi", "401001", "ali", "1111");
        system.getStudentManager().registerStudent("Sara Karimi", "401002", "sara", "2222");
        system.getStudentManager().registerStudent("Reza Hosseini", "401003", "reza", "3333");

        // Seed Books (registered by emp1)
        system.getBookManager().addBook(new Book("Discrete Mathematics", "Rosen", 2019, "ISBN-0001", "emp1"));
        system.getBookManager().addBook(new Book("Introduction to Algorithms", "CLRS", 2022, "ISBN-0002", "emp1"));
        system.getBookManager().addBook(new Book("Clean Code", "Robert C. Martin", 2008, "ISBN-0003", "emp2"));
        system.getBookManager().addBook(new Book("Design Patterns", "GoF", 1994, "ISBN-0004", "emp3"));
    }
}

