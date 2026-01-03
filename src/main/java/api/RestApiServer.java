package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import system.LibrarySystem;
import spark.Spark;

public class RestApiServer {
    private static final int PORT = 8081;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private LibrarySystem librarySystem;

    public RestApiServer() {
        this.librarySystem = new LibrarySystem();
        SeedData.seed(librarySystem);
    }

    public void start() {
        Spark.port(PORT);
        Spark.staticFiles.location("/public");
        
        // Enable CORS
        Spark.options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
            response.type("application/json; charset=UTF-8");
        });

        // Initialize routes
        AuthController.setup(librarySystem, gson);
        BookController.setup(librarySystem, gson);
        BorrowController.setup(librarySystem, gson);
        StudentController.setup(librarySystem, gson);
        StatsController.setup(librarySystem, gson);
        AdminController.setup(librarySystem, gson);

        Spark.init();
        System.out.println("REST API Server started on http://localhost:" + PORT);
        System.out.println("API Base URL: http://localhost:" + PORT + "/api");
    }

    public static void main(String[] args) {
        new RestApiServer().start();
    }
}

