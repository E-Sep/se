package ap.projects.finalproject;

public class Employee {
    private String username;
    private String password;
    private int booksRegistered;
    private int booksLent;
    private int booksReturned;

    public Employee(String username, String password) {
        this.username = username;
        this.password = password;
        this.booksRegistered = 0;
        this.booksLent = 0;
        this.booksReturned = 0;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getBooksRegistered() {
        return booksRegistered;
    }

    public void incrementBooksRegistered() {
        this.booksRegistered++;
    }

    public int getBooksLent() {
        return booksLent;
    }

    public void incrementBooksLent() {
        this.booksLent++;
    }

    public int getBooksReturned() {
        return booksReturned;
    }

    public void incrementBooksReturned() {
        this.booksReturned++;
    }

    @Override
    public String toString() {
        return "Username: " + username +
                " | Books Registered: " + booksRegistered +
                " | Books Lent: " + booksLent +
                " | Books Returned: " + booksReturned;
    }
}

