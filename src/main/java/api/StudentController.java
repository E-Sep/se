package api;

import com.google.gson.Gson;
import system.LibrarySystem;
import model.Loan;
import model.Student;
import service.ReportingService;
import spark.Spark;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentController {
    public static void setup(LibrarySystem system, Gson gson) {
        // GET /api/students/:id
        Spark.get("/api/students/:id", (req, res) -> {
            String id = req.params(":id");
            Student student = system.getStudentManager().findStudentByUsername(id);
            
            if (student == null) {
                // Try finding by studentId
                student = system.getStudentManager().getAllStudents().stream()
                    .filter(s -> s.getStudentId().equals(id))
                    .findFirst()
                    .orElse(null);
            }
            
            if (student == null) {
                res.status(404);
                return gson.toJson(Map.of("error", "Student not found"));
            }
            
            Map<String, Object> studentData = new HashMap<>();
            studentData.put("name", student.getName());
            studentData.put("studentId", student.getStudentId());
            studentData.put("username", student.getUsername());
            studentData.put("isActive", student.isActive());
            
            res.status(200);
            return gson.toJson(studentData);
        });

        // PUT /api/students/:id/status (Employee only)
        Spark.put("/api/students/:id/status", (req, res) -> {
            String token = req.headers("Authorization");
            if (!AuthController.isEmployee(token)) {
                res.status(403);
                return gson.toJson(Map.of("error", "Forbidden: Employee access required"));
            }

            String id = req.params(":id");
            Student student = system.getStudentManager().findStudentByUsername(id);
            
            if (student == null) {
                // Try finding by studentId
                student = system.getStudentManager().getAllStudents().stream()
                    .filter(s -> s.getStudentId().equals(id))
                    .findFirst()
                    .orElse(null);
            }
            
            if (student == null) {
                res.status(404);
                return gson.toJson(Map.of("error", "Student not found"));
            }

            com.google.gson.JsonObject body = gson.fromJson(req.body(), com.google.gson.JsonObject.class);
            Boolean isActive = body.has("isActive") ? body.get("isActive").getAsBoolean() : null;

            if (isActive != null) {
                student.setActive(isActive);
            }

            res.status(200);
            return gson.toJson(Map.of(
                "message", "Student status updated successfully",
                "isActive", student.isActive()
            ));
        });

        // GET /api/students/:id/borrow-history (Employee only)
        Spark.get("/api/students/:id/borrow-history", (req, res) -> {
            String token = req.headers("Authorization");
            if (!AuthController.isEmployee(token)) {
                res.status(403);
                return gson.toJson(Map.of("error", "Forbidden: Employee access required"));
            }

            String id = req.params(":id");
            Student student = system.getStudentManager().findStudentByUsername(id);
            
            if (student == null) {
                // Try finding by studentId
                student = system.getStudentManager().getAllStudents().stream()
                    .filter(s -> s.getStudentId().equals(id))
                    .findFirst()
                    .orElse(null);
            }
            
            if (student == null) {
                res.status(404);
                return gson.toJson(Map.of("error", "Student not found"));
            }

            List<Loan> history = system.getLoanManager().getStudentLoanHistory(student.getUsername());
            
            List<Map<String, Object>> historyList = history.stream()
                .map(loan -> {
                    Map<String, Object> loanMap = new HashMap<>();
                    loanMap.put("bookIsbn", loan.getBookIsbn());
                    loanMap.put("requestedStartDate", loan.getRequestedStartDate().toString());
                    loanMap.put("requestedEndDate", loan.getRequestedEndDate().toString());
                    loanMap.put("actualStartDate", loan.getActualStartDate() != null ? loan.getActualStartDate().toString() : null);
                    loanMap.put("actualEndDate", loan.getActualEndDate() != null ? loan.getActualEndDate().toString() : null);
                    loanMap.put("isApproved", loan.isApproved());
                    loanMap.put("isReturned", loan.isReturned());
                    loanMap.put("wasLateReturn", loan.wasLateReturn());
                    loanMap.put("approvedByEmployee", loan.getApprovedByEmployee());
                    return loanMap;
                })
                .collect(Collectors.toList());
            
            res.status(200);
            return gson.toJson(Map.of("history", historyList, "count", historyList.size()));
        });
    }
}

