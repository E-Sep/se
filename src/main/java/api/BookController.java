package api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import system.LibrarySystem;
import model.Book;
import spark.Spark;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookController {
    public static void setup(LibrarySystem system, Gson gson) {
        // GET /api/books
        Spark.get("/api/books", (req, res) -> {
            String title = req.queryParams("title");
            String author = req.queryParams("author");
            String yearStr = req.queryParams("year");
            
            Integer year = yearStr != null && !yearStr.isEmpty() ? Integer.parseInt(yearStr) : null;
            
            List<Book> books = system.getBookManager().searchBooks(title, author, year);
            
            res.status(200);
            return gson.toJson(Map.of("books", books));
        });

        // GET /api/books/:id (using ISBN as id)
        Spark.get("/api/books/:id", (req, res) -> {
            String isbn = req.params(":id");
            Book book = system.getBookManager().findBookByIsbn(isbn);
            
            if (book == null) {
                res.status(404);
                return gson.toJson(Map.of("error", "Book not found"));
            }
            
            res.status(200);
            return gson.toJson(book);
        });

        // POST /api/books (Employee only)
        Spark.post("/api/books", (req, res) -> {
            String token = req.headers("Authorization");
            if (!AuthController.isEmployee(token)) {
                res.status(403);
                return gson.toJson(Map.of("error", "Forbidden: Employee access required"));
            }

            JsonObject body = gson.fromJson(req.body(), JsonObject.class);
            String title = body.get("title").getAsString();
            String author = body.get("author").getAsString();
            int year = body.get("year").getAsInt();
            String isbn = body.get("isbn").getAsString();

            if (system.getBookManager().findBookByIsbn(isbn) != null) {
                res.status(400);
                return gson.toJson(Map.of("error", "Book with this ISBN already exists"));
            }

            AuthController.AuthSession session = AuthController.getSession(token);
            Book book = new Book(title, author, year, isbn, session.username);
            system.getBookManager().addBook(book);
            
            // Increment employee stats
            system.getEmployeeManager().findEmployeeByUsername(session.username).incrementBooksRegistered();
            
            res.status(201);
            return gson.toJson(Map.of("message", "Book created successfully", "book", book));
        });

        // PUT /api/books/:id (Employee only)
        Spark.put("/api/books/:id", (req, res) -> {
            String token = req.headers("Authorization");
            if (!AuthController.isEmployee(token)) {
                res.status(403);
                return gson.toJson(Map.of("error", "Forbidden: Employee access required"));
            }

            String isbn = req.params(":id");
            Book book = system.getBookManager().findBookByIsbn(isbn);
            
            if (book == null) {
                res.status(404);
                return gson.toJson(Map.of("error", "Book not found"));
            }

            JsonObject body = gson.fromJson(req.body(), JsonObject.class);
            String newTitle = body.has("title") ? body.get("title").getAsString() : null;
            String newAuthor = body.has("author") ? body.get("author").getAsString() : null;
            Integer newYear = body.has("year") ? body.get("year").getAsInt() : null;

            system.getBookManager().editBook(isbn, newTitle, newAuthor, newYear);
            
            res.status(200);
            Book updatedBook = system.getBookManager().findBookByIsbn(isbn);
            return gson.toJson(Map.of("message", "Book updated successfully", "book", updatedBook));
        });

        // GET /api/books/search
        Spark.get("/api/books/search", (req, res) -> {
            String title = req.queryParams("title");
            String author = req.queryParams("author");
            String yearStr = req.queryParams("year");
            
            Integer year = yearStr != null && !yearStr.isEmpty() ? Integer.parseInt(yearStr) : null;
            
            List<Book> books = system.getBookManager().searchBooks(title, author, year);
            
            res.status(200);
            return gson.toJson(Map.of("books", books, "count", books.size()));
        });
    }
}

