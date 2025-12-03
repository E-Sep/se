package manager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import model.Book;

public class BookManager {
    private List<Book> books;

    public BookManager() {
        this.books = new ArrayList<>();
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public List<Book> searchBooks(String title, String author, Integer year) {
        return books.stream()
                .filter(book -> (title == null || title.isEmpty() || book.getTitle().toLowerCase().contains(title.toLowerCase())))
                .filter(book -> (author == null || author.isEmpty() || book.getAuthor().toLowerCase().contains(author.toLowerCase())))
                .filter(book -> (year == null || book.getPublishYear() == year))
                .collect(Collectors.toList());
    }

    public List<Book> searchBooksByTitle(String title) {
        return books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Book findBookByIsbn(String isbn) {
        return books.stream()
                .filter(book -> book.getIsbn().equals(isbn))
                .findFirst()
                .orElse(null);
    }

    public List<Book> getAllBooks() {
        return books;
    }

    public int getTotalBooksCount() {
        return books.size();
    }

    public int getAvailableBooksCount() {
        return (int) books.stream().filter(Book::isAvailable).count();
    }

    public int getBooksOnLoanCount() {
        return (int) books.stream().filter(book -> !book.isAvailable()).count();
    }

    public void editBook(String isbn, String newTitle, String newAuthor, Integer newYear) {
        Book book = findBookByIsbn(isbn);
        if (book != null) {
            if (newTitle != null && !newTitle.isEmpty()) {
                book.setTitle(newTitle);
            }
            if (newAuthor != null && !newAuthor.isEmpty()) {
                book.setAuthor(newAuthor);
            }
            if (newYear != null) {
                book.setPublishYear(newYear);
            }
        }
    }
}

