package api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import system.LibrarySystem;
import model.Student;
import model.Employee;
import spark.Spark;

import java.util.HashMap;
import java.util.Map;

public class AuthController {
    private static Map<String, AuthSession> sessions = new HashMap<>();
    
    public static class AuthSession {
        public String username;
        public String userType; // "student", "employee", "manager"
        
        public AuthSession(String username, String userType) {
            this.username = username;
            this.userType = userType;
        }
    }

    public static void setup(LibrarySystem system, Gson gson) {
        // POST /api/auth/register
        Spark.post("/api/auth/register", (req, res) -> {
            JsonObject body = gson.fromJson(req.body(), JsonObject.class);
            String name = body.get("name").getAsString();
            String studentId = body.get("studentId").getAsString();
            String username = body.get("username").getAsString();
            String password = body.get("password").getAsString();

            if (system.getStudentManager().findStudentByUsername(username) != null) {
                res.status(400);
                return gson.toJson(Map.of("error", "Username already exists"));
            }

            system.getStudentManager().registerStudent(name, studentId, username, password);
            res.status(201);
            return gson.toJson(Map.of("message", "Student registered successfully", "username", username));
        });

        // POST /api/auth/login
        Spark.post("/api/auth/login", (req, res) -> {
            JsonObject body = gson.fromJson(req.body(), JsonObject.class);
            String username = body.get("username").getAsString();
            String password = body.get("password").getAsString();
            String userType = body.has("userType") ? body.get("userType").getAsString() : null;

            // Try student login
            Student student = system.getStudentManager().authenticateStudent(username, password);
            if (student != null) {
                String token = generateToken();
                sessions.put(token, new AuthSession(username, "student"));
                res.status(200);
                return gson.toJson(Map.of(
                    "message", "Login successful",
                    "token", token,
                    "userType", "student",
                    "username", username
                ));
            }

            // Try employee login
            Employee employee = system.getEmployeeManager().authenticateEmployee(username, password);
            if (employee != null) {
                String token = generateToken();
                sessions.put(token, new AuthSession(username, "employee"));
                res.status(200);
                return gson.toJson(Map.of(
                    "message", "Login successful",
                    "token", token,
                    "userType", "employee",
                    "username", username
                ));
            }

            // Try manager login
            if ("manager".equals(userType) && "admin".equals(password)) {
                String token = generateToken();
                sessions.put(token, new AuthSession("admin", "manager"));
                res.status(200);
                return gson.toJson(Map.of(
                    "message", "Login successful",
                    "token", token,
                    "userType", "manager",
                    "username", "admin"
                ));
            }

            res.status(401);
            return gson.toJson(Map.of("error", "Invalid username or password"));
        });

        // POST /api/auth/change-password
        Spark.post("/api/auth/change-password", (req, res) -> {
            String token = req.headers("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                res.status(401);
                return gson.toJson(Map.of("error", "Unauthorized"));
            }
            
            token = token.substring(7);
            AuthSession session = sessions.get(token);
            if (session == null || !"employee".equals(session.userType) && !"manager".equals(session.userType)) {
                res.status(403);
                return gson.toJson(Map.of("error", "Forbidden: Only employees and managers can change password"));
            }

            JsonObject body = gson.fromJson(req.body(), JsonObject.class);
            String oldPassword = body.get("oldPassword").getAsString();
            String newPassword = body.get("newPassword").getAsString();

            Employee employee = system.getEmployeeManager().findEmployeeByUsername(session.username);
            if (employee == null || !employee.getPassword().equals(oldPassword)) {
                res.status(400);
                return gson.toJson(Map.of("error", "Invalid old password"));
            }

            employee.setPassword(newPassword);
            res.status(200);
            return gson.toJson(Map.of("message", "Password changed successfully"));
        });
    }

    private static String generateToken() {
        return "token_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }

    public static AuthSession getSession(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return sessions.get(token);
    }

    public static boolean isEmployee(String token) {
        AuthSession session = getSession(token);
        return session != null && ("employee".equals(session.userType) || "manager".equals(session.userType));
    }

    public static boolean isManager(String token) {
        AuthSession session = getSession(token);
        return session != null && "manager".equals(session.userType);
    }
}

