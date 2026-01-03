package api;

import com.google.gson.Gson;
import system.LibrarySystem;
import model.Employee;
import model.Loan;
import service.ReportingService;
import spark.Spark;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsController {
    public static void setup(LibrarySystem system, Gson gson) {
        ReportingService reportingService = new ReportingService(
            system.getStudentManager(),
            system.getBookManager(),
            system.getLoanManager()
        );

        // GET /api/stats/summary (Public)
        Spark.get("/api/stats/summary", (req, res) -> {
            int totalStudents = system.getStudentManager().getStudentCount();
            int totalBooks = system.getBookManager().getTotalBooksCount();
            int totalLoans = system.getLoanManager().getTotalLoanRequests();
            int currentLoans = system.getLoanManager().getCurrentLoansCount();
            int availableBooks = system.getBookManager().getAvailableBooksCount();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalStudents", totalStudents);
            stats.put("totalBooks", totalBooks);
            stats.put("totalLoans", totalLoans);
            stats.put("currentLoans", currentLoans);
            stats.put("availableBooks", availableBooks);

            res.status(200);
            return gson.toJson(stats);
        });

        // GET /api/stats/borrows (Manager only)
        Spark.get("/api/stats/borrows", (req, res) -> {
            String token = req.headers("Authorization");
            if (!AuthController.isManager(token)) {
                res.status(403);
                return gson.toJson(Map.of("error", "Forbidden: Manager access required"));
            }

            int totalLoanRequests = system.getLoanManager().getTotalLoanRequests();
            int totalApprovedLoans = system.getLoanManager().getTotalApprovedLoans();
            double averageLoanDays = system.getLoanManager().getAverageLoanDays();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalLoanRequests", totalLoanRequests);
            stats.put("totalApprovedLoans", totalApprovedLoans);
            stats.put("averageLoanDays", averageLoanDays);

            res.status(200);
            return gson.toJson(stats);
        });

        // GET /api/stats/employees/:id/performance (Manager only)
        Spark.get("/api/stats/employees/:id/performance", (req, res) -> {
            String token = req.headers("Authorization");
            if (!AuthController.isManager(token)) {
                res.status(403);
                return gson.toJson(Map.of("error", "Forbidden: Manager access required"));
            }

            String employeeUsername = req.params(":id");
            Employee employee = system.getEmployeeManager().findEmployeeByUsername(employeeUsername);

            if (employee == null) {
                res.status(404);
                return gson.toJson(Map.of("error", "Employee not found"));
            }

            Map<String, Object> performance = new HashMap<>();
            performance.put("username", employee.getUsername());
            performance.put("booksRegistered", employee.getBooksRegistered());
            performance.put("booksLent", employee.getBooksLent());
            performance.put("booksReturned", employee.getBooksReturned());

            res.status(200);
            return gson.toJson(performance);
        });

        // GET /api/stats/top-delayed (Manager only)
        Spark.get("/api/stats/top-delayed", (req, res) -> {
            String token = req.headers("Authorization");
            if (!AuthController.isManager(token)) {
                res.status(403);
                return gson.toJson(Map.of("error", "Forbidden: Manager access required"));
            }

            List<String> topDelayed = system.getLoanManager().getStudentsWithMostDelays(10);
            
            List<Map<String, Object>> delayedList = topDelayed.stream()
                .map(entry -> {
                    String[] parts = entry.split(" - ");
                    Map<String, Object> item = new HashMap<>();
                    item.put("studentUsername", parts[0]);
                    item.put("lateReturnsCount", Integer.parseInt(parts[1].split(" ")[0]));
                    return item;
                })
                .collect(Collectors.toList());

            res.status(200);
            return gson.toJson(Map.of("students", delayedList));
        });
    }
}

