package manager;

import model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * سناریو ۲: سرویس جستجوی کتاب
 */
public class BookSearchServiceTest {
    private BookManager bookManager;

    @BeforeEach
    void setUp() {
        bookManager = new BookManager();
        // Add sample books
        bookManager.addBook(new Book("Discrete Mathematics", "Rosen", 2019, "ISBN-0001", "emp1"));
        bookManager.addBook(new Book("Introduction to Algorithms", "CLRS", 2022, "ISBN-0002", "emp1"));
        bookManager.addBook(new Book("Clean Code", "Robert C. Martin", 2008, "ISBN-0003", "emp2"));
        bookManager.addBook(new Book("Design Patterns", "GoF", 1994, "ISBN-0004", "emp3"));
        bookManager.addBook(new Book("Algorithms and Data Structures", "CLRS", 2022, "ISBN-0005", "emp1"));
    }

    /**
     * 2-1: جستجو فقط با عنوان.
     * رفتار مورد انتظار: لیستی از کتاب‌هایی که عنوانشان شامل رشته ورودی می‌شود، برگردانده می‌شود.
     */
    @Test
    void testSearchByTitleOnly() {
        // Act
        List<Book> results = bookManager.searchBooks("Algorithms", null, null);

        // Assert
        assertNotNull(results, "Search results should not be null");
        assertEquals(2, results.size(), "Should find 2 books with 'Algorithms' in title");
        assertTrue(results.stream().anyMatch(b -> b.getTitle().contains("Algorithms")), 
                  "Results should contain books with 'Algorithms' in title");
    }

    /**
     * 2-2: جستجو با ترکیب نویسنده و سال انتشار.
     * رفتار مورد انتظار: لیستی از کتاب‌های آن نویسنده در آن سال خاص برگردانده می‌شود.
     */
    @Test
    void testSearchByAuthorAndYear() {
        // Act
        List<Book> results = bookManager.searchBooks(null, "CLRS", 2022);

        // Assert
        assertNotNull(results, "Search results should not be null");
        assertEquals(2, results.size(), "Should find 2 books by CLRS in 2022");
        assertTrue(results.stream().allMatch(b -> b.getAuthor().equals("CLRS") && b.getPublishYear() == 2022),
                  "All results should be by CLRS and published in 2022");
    }

    /**
     * 2-3: جستجو بدون هیچ معیاری (همه پارامترها null هستند).
     * رفتار مورد انتظار: تمام کتاب‌های موجود برگردانده می‌شوند.
     */
    @Test
    void testSearchWithoutAnyCriteria() {
        // Act
        List<Book> results = bookManager.searchBooks(null, null, null);

        // Assert
        assertNotNull(results, "Search results should not be null");
        assertEquals(5, results.size(), "Should return all 5 books when no criteria specified");
        assertEquals(bookManager.getAllBooks().size(), results.size(), 
                    "Results should match total books count");
    }

    /**
     * 2-4: جستجویی که هیچ کتابی مطابقت ندارد.
     * رفتار مورد انتظار: یک لیست خالی برگردانده می‌شود.
     */
    @Test
    void testSearchWithNoMatches() {
        // Act
        List<Book> results = bookManager.searchBooks("NonExistentBook", null, null);

        // Assert
        assertNotNull(results, "Search results should not be null");
        assertTrue(results.isEmpty(), "Search should return empty list when no matches found");
        assertEquals(0, results.size(), "Empty list should have size 0");
    }
}

