package api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import system.LibrarySystem;
import exception.BookNotAvailableException;
import exception.InvalidRequestStatusException;
import exception.InvalidStudentStatusException;
import model.Book;
import model.BorrowRequest;
import model.Employee;
import model.Loan;
import model.Student;
import spark.Spark;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BorrowController {
    private static Map<String, BorrowRequest> borrowRequests = new HashMap<>();
    private static int requestIdCounter = 1;

    public static void setup(LibrarySystem system, Gson gson) {
        // POST /api/borrow/request (Student only)
        Spark.post("/api/borrow/request", (req, res) -> {
            String token = req.headers("Authorization");
            AuthController.AuthSession session = AuthController.getSession(token);
            if (session == null || !"student".equals(session.userType)) {
                res.status(403);
                return gson.toJson(Map.of("error", "Forbidden: Student access required"));
            }

            JsonObject body = gson.fromJson(req.body(), JsonObject.class);
            String bookIsbn = body.get("bookIsbn").getAsString();
            String startDateStr = body.get("startDate").getAsString();
            String endDateStr = body.get("endDate").getAsString();

            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            LocalDate startDate = LocalDate.parse(startDateStr, formatter);
            LocalDate endDate = LocalDate.parse(endDateStr, formatter);

            Student student = system.getStudentManager().findStudentByUsername(session.username);
            Book book = system.getBookManager().findBookByIsbn(bookIsbn);

            if (book == null) {
                res.status(404);
                return gson.toJson(Map.of("error", "Book not found"));
            }

            try {
                BorrowRequest request = system.getLoanManager().requestLoan(
                    student, book, startDate, endDate,
                    system.getStudentManager(), system.getBookManager()
                );
                String requestId = session.username + "_" + bookIsbn;
                borrowRequests.put(requestId, request);
                
                res.status(201);
                return gson.toJson(Map.of(
                    "message", "Borrow request created successfully",
                    "requestId", requestId,
                    "status", request.getStatus().toString()
                ));
            } catch (InvalidStudentStatusException e) {
                res.status(400);
                return gson.toJson(Map.of("error", e.getMessage()));
            } catch (BookNotAvailableException e) {
                res.status(400);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });

        // GET /api/borrow/requests/pending (Employee only)
        Spark.get("/api/borrow/requests/pending", (request, response) -> {
            String token = request.headers("Authorization");
            if (!AuthController.isEmployee(token)) {
                response.status(403);
                return gson.toJson(Map.of("error", "Forbidden: Employee access required"));
            }

            List<Loan> pendingLoans = system.getLoanManager().getPendingLoanRequests();
            
            List<Map<String, Object>> requests = pendingLoans.stream()
                .map(loan -> {
                    Map<String, Object> reqMap = new HashMap<>();
                    reqMap.put("id", loan.getStudentUsername() + "_" + loan.getBookIsbn());
                    reqMap.put("studentUsername", loan.getStudentUsername());
                    reqMap.put("bookIsbn", loan.getBookIsbn());
                    reqMap.put("requestedStartDate", loan.getRequestedStartDate().toString());
                    reqMap.put("requestedEndDate", loan.getRequestedEndDate().toString());
                    reqMap.put("approved", loan.isApproved());
                    return reqMap;
                })
                .collect(Collectors.toList());
            
            response.status(200);
            return gson.toJson(Map.of("requests", requests));
        });

        // PUT /api/borrow/requests/:id/approve (Employee only)
        Spark.put("/api/borrow/requests/:id/approve", (req, res) -> {
            String token = req.headers("Authorization");
            if (!AuthController.isEmployee(token)) {
                res.status(403);
                return gson.toJson(Map.of("error", "Forbidden: Employee access required"));
            }

            String requestId = req.params(":id");
            String[] parts = requestId.split("_");
            if (parts.length < 2) {
                res.status(400);
                return gson.toJson(Map.of("error", "Invalid request ID format"));
            }

            String studentUsername = parts[0];
            String bookIsbn = parts.length == 2 ? parts[1] : parts[parts.length - 1];

            // Find the BorrowRequest from our map
            BorrowRequest request = borrowRequests.get(requestId);
            if (request == null) {
                // Try to find from loans and create BorrowRequest
                Loan loan = system.getLoanManager().getAllLoans().stream()
                    .filter(l -> l.getStudentUsername().equals(studentUsername) && 
                                l.getBookIsbn().equals(bookIsbn) && 
                                !l.isApproved())
                    .findFirst()
                    .orElse(null);
                
                if (loan == null) {
                    res.status(404);
                    return gson.toJson(Map.of("error", "Request not found or already processed"));
                }
                
                request = new BorrowRequest(studentUsername, bookIsbn, 
                    loan.getRequestedStartDate(), loan.getRequestedEndDate());
                borrowRequests.put(requestId, request);
            }

            if (request.getStatus() != BorrowRequest.RequestStatus.PENDING) {
                res.status(400);
                return gson.toJson(Map.of("error", "Request is not in PENDING status"));
            }

            try {
                AuthController.AuthSession session = AuthController.getSession(token);
                system.getLoanManager().approveRequest(request, session.username, system.getBookManager());
                
                // Increment employee stats
                Employee employee = system.getEmployeeManager().findEmployeeByUsername(session.username);
                if (employee != null) {
                    employee.incrementBooksLent();
                }

                res.status(200);
                return gson.toJson(Map.of("message", "Request approved successfully"));
            } catch (InvalidRequestStatusException e) {
                res.status(400);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });

        // PUT /api/borrow/requests/:id/reject (Employee only)
        Spark.put("/api/borrow/requests/:id/reject", (req, res) -> {
            String token = req.headers("Authorization");
            if (!AuthController.isEmployee(token)) {
                res.status(403);
                return gson.toJson(Map.of("error", "Forbidden: Employee access required"));
            }

            String requestId = req.params(":id");
            String[] parts = requestId.split("_");
            if (parts.length < 2) {
                res.status(400);
                return gson.toJson(Map.of("error", "Invalid request ID format"));
            }

            String studentUsername = parts[0];
            String bookIsbn = parts.length == 2 ? parts[1] : parts[parts.length - 1];

            BorrowRequest request = borrowRequests.get(requestId);
            if (request == null) {
                // Try to find it from loans
                Loan loan = system.getLoanManager().getAllLoans().stream()
                    .filter(l -> l.getStudentUsername().equals(studentUsername) && 
                                l.getBookIsbn().equals(bookIsbn) && 
                                !l.isApproved())
                    .findFirst()
                    .orElse(null);

                if (loan != null) {
                    request = new BorrowRequest(studentUsername, bookIsbn, 
                        loan.getRequestedStartDate(), loan.getRequestedEndDate());
                    borrowRequests.put(requestId, request);
                }
            }

            if (request == null || request.getStatus() != BorrowRequest.RequestStatus.PENDING) {
                res.status(404);
                return gson.toJson(Map.of("error", "Request not found or already processed"));
            }

            request.setStatus(BorrowRequest.RequestStatus.REJECTED);
            
            res.status(200);
            return gson.toJson(Map.of("message", "Request rejected successfully"));
        });

        // PUT /api/borrow/:id/return (Employee only)
        Spark.put("/api/borrow/:id/return", (req, res) -> {
            String token = req.headers("Authorization");
            if (!AuthController.isEmployee(token)) {
                res.status(403);
                return gson.toJson(Map.of("error", "Forbidden: Employee access required"));
            }

            String requestId = req.params(":id");
            String[] parts = requestId.split("_");
            if (parts.length < 2) {
                res.status(400);
                return gson.toJson(Map.of("error", "Invalid request ID format"));
            }

            String studentUsername = parts[0];
            String bookIsbn = parts.length == 2 ? parts[1] : parts[parts.length - 1];

            JsonObject body = gson.fromJson(req.body(), JsonObject.class);
            boolean wasLate = body.has("wasLate") && body.get("wasLate").getAsBoolean();

            Loan loan = system.getLoanManager().getAllLoans().stream()
                .filter(l -> l.getStudentUsername().equals(studentUsername) && 
                            l.getBookIsbn().equals(bookIsbn) && 
                            l.isApproved() && 
                            !l.isReturned())
                .findFirst()
                .orElse(null);

            if (loan == null) {
                res.status(404);
                return gson.toJson(Map.of("error", "Loan not found or already returned"));
            }

            AuthController.AuthSession session = AuthController.getSession(token);
            system.getLoanManager().returnBook(studentUsername, bookIsbn, session.username, wasLate);
            
            // Mark book as available
            Book book = system.getBookManager().findBookByIsbn(bookIsbn);
            if (book != null) {
                book.setAvailable(true);
            }
            
            // Increment employee stats
            Employee employee = system.getEmployeeManager().findEmployeeByUsername(session.username);
            if (employee != null) {
                employee.incrementBooksReturned();
            }

            res.status(200);
            return gson.toJson(Map.of("message", "Book returned successfully"));
        });
    }
}

