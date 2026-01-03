package api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import system.LibrarySystem;
import model.Employee;
import spark.Spark;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminController {
    public static void setup(LibrarySystem system, Gson gson) {
        // POST /api/admin/employees (Manager only)
        Spark.post("/api/admin/employees", (req, res) -> {
            String token = req.headers("Authorization");
            if (!AuthController.isManager(token)) {
                res.status(403);
                return gson.toJson(Map.of("error", "Forbidden: Manager access required"));
            }

            JsonObject body = gson.fromJson(req.body(), JsonObject.class);
            String username = body.get("username").getAsString();
            String password = body.get("password").getAsString();

            if (system.getEmployeeManager().findEmployeeByUsername(username) != null) {
                res.status(400);
                return gson.toJson(Map.of("error", "Employee with this username already exists"));
            }

            system.getEmployeeManager().addEmployee(username, password);
            
            res.status(201);
            return gson.toJson(Map.of("message", "Employee created successfully", "username", username));
        });

        // GET /api/admin/employees (Manager only)
        Spark.get("/api/admin/employees", (req, res) -> {
            String token = req.headers("Authorization");
            if (!AuthController.isManager(token)) {
                res.status(403);
                return gson.toJson(Map.of("error", "Forbidden: Manager access required"));
            }

            List<Employee> employees = system.getEmployeeManager().getAllEmployees();
            
            List<Map<String, Object>> employeeList = employees.stream()
                .map(emp -> {
                    Map<String, Object> empMap = new HashMap<>();
                    empMap.put("username", emp.getUsername());
                    empMap.put("booksRegistered", emp.getBooksRegistered());
                    empMap.put("booksLent", emp.getBooksLent());
                    empMap.put("booksReturned", emp.getBooksReturned());
                    return empMap;
                })
                .collect(Collectors.toList());

            res.status(200);
            return gson.toJson(Map.of("employees", employeeList));
        });
    }
}

