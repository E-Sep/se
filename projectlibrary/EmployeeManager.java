package ap.projects.finalproject;

import java.util.ArrayList;
import java.util.List;

public class EmployeeManager {
    private List<Employee> employees;

    public EmployeeManager() {
        this.employees = new ArrayList<>();
    }

    public void addEmployee(String username, String password) {
        if (isUsernameTaken(username)) {
            System.out.println("This username already exists. Please choose a different username.");
            return;
        }
        Employee newEmployee = new Employee(username, password);
        employees.add(newEmployee);
        System.out.println("Employee registration completed successfully.");
    }

    public Employee authenticateEmployee(String username, String password) {
        return employees.stream()
                .filter(e -> e.getUsername().equals(username) && e.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public Employee findEmployeeByUsername(String username) {
        return employees.stream()
                .filter(e -> e.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    private boolean isUsernameTaken(String username) {
        return employees.stream().anyMatch(e -> e.getUsername().equals(username));
    }

    public List<Employee> getAllEmployees() {
        return employees;
    }
}

