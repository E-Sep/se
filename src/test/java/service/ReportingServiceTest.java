package service;

import manager.BookManager;
import manager.LoanManager;
import manager.StudentManager;
import model.Book;
import model.LibraryStats;
import model.Loan;
import model.StudentReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * سناریو ۴: سرویس گزارش‌گیری
 */
public class ReportingServiceTest {
    private ReportingService reportingService;
    private StudentManager studentManager;
    private BookManager bookManager;
    private LoanManager loanManager;

    @BeforeEach
    void setUp() {
        studentManager = new StudentManager();
        bookManager = new BookManager();
        loanManager = new LoanManager();
        reportingService = new ReportingService(studentManager, bookManager, loanManager);
    }

    /**
     * 4-1: تولید گزارش برای یک دانشجو.
     * رفتار مورد انتظار: StudentReport شامل تعداد کل امانت‌ها، تعداد کتاب‌های تحویل‌داده‌نشده 
     * و تعداد امانت‌های با تاخیر به درستی محاسبه شده باشد.
     */
    @Test
    void testGenerateStudentReport() {
        // Arrange
        studentManager.register("Ali Ahmadi", "401001", "ali", "1111");
        
        Book book1 = new Book("Book 1", "Author 1", 2020, "ISBN-001", "emp1");
        Book book2 = new Book("Book 2", "Author 2", 2021, "ISBN-002", "emp1");
        Book book3 = new Book("Book 3", "Author 3", 2022, "ISBN-003", "emp1");
        bookManager.addBook(book1);
        bookManager.addBook(book2);
        bookManager.addBook(book3);

        // Create loans with different statuses
        LocalDate today = LocalDate.now();
        
        // Loan 1: returned, not late
        loanManager.createLoanRequest("ali", "ISBN-001", today.minusDays(30), today.minusDays(20));
        Loan loan1 = loanManager.getAllLoans().get(0);
        loan1.setApproved(true);
        loan1.setReturned(true);
        loan1.setActualStartDate(today.minusDays(30));
        loan1.setActualEndDate(today.minusDays(20));
        loan1.setWasLateReturn(false);

        // Loan 2: returned, late
        loanManager.createLoanRequest("ali", "ISBN-002", today.minusDays(15), today.minusDays(5));
        Loan loan2 = loanManager.getAllLoans().get(1);
        loan2.setApproved(true);
        loan2.setReturned(true);
        loan2.setActualStartDate(today.minusDays(15));
        loan2.setActualEndDate(today.minusDays(5));
        loan2.setWasLateReturn(true); // Late return

        // Loan 3: approved but not returned yet
        loanManager.createLoanRequest("ali", "ISBN-003", today.minusDays(10), today.plusDays(5));
        Loan loan3 = loanManager.getAllLoans().get(2);
        loan3.setApproved(true);
        loan3.setReturned(false); // Not returned yet
        loan3.setActualStartDate(today.minusDays(10));

        // Loan 4: returned, late
        loanManager.createLoanRequest("ali", "ISBN-001", today, today.plusDays(7));
        Loan loan4 = loanManager.getAllLoans().get(3);
        loan4.setApproved(true);
        loan4.setReturned(true);
        loan4.setActualStartDate(today);
        loan4.setActualEndDate(today.plusDays(3));
        loan4.setWasLateReturn(true); // Another late return

        // Act
        StudentReport report = reportingService.generateStudentReport("ali");

        // Assert
        assertNotNull(report, "StudentReport should not be null");
        assertEquals("ali", report.getStudentUsername(), 
                    "Report should be for correct student");
        assertEquals(4, report.getTotalLoans(), 
                    "Total loans should be 4");
        assertEquals(1, report.getUndeliveredBooks(), 
                    "Undelivered books should be 1");
        assertEquals(2, report.getLateLoans(), 
                    "Late loans should be 2");
    }

    /**
     * 4-2: محاسبه آمار کلی کتابخانه.
     * رفتار مورد انتظار: LibraryStats شامل میانگین روزهای امانت به درستی محاسبه شود.
     */
    @Test
    void testCalculateLibraryStatistics() {
        // Arrange
        Book book1 = new Book("Book 1", "Author 1", 2020, "ISBN-001", "emp1");
        Book book2 = new Book("Book 2", "Author 2", 2021, "ISBN-002", "emp1");
        bookManager.addBook(book1);
        bookManager.addBook(book2);

        LocalDate today = LocalDate.now();
        
        // Loan 1: 10 days
        loanManager.createLoanRequest("student1", "ISBN-001", today.minusDays(20), today.minusDays(10));
        Loan loan1 = loanManager.getAllLoans().get(0);
        loan1.setApproved(true);
        loan1.setReturned(true);
        loan1.setActualStartDate(today.minusDays(20));
        loan1.setActualEndDate(today.minusDays(10));

        // Loan 2: 15 days
        loanManager.createLoanRequest("student2", "ISBN-002", today.minusDays(25), today.minusDays(10));
        Loan loan2 = loanManager.getAllLoans().get(1);
        loan2.setApproved(true);
        loan2.setReturned(true);
        loan2.setActualStartDate(today.minusDays(25));
        loan2.setActualEndDate(today.minusDays(10));

        // Loan 3: 5 days (not returned yet, should not count in average)
        loanManager.createLoanRequest("student3", "ISBN-001", today.minusDays(10), today.plusDays(5));
        Loan loan3 = loanManager.getAllLoans().get(2);
        loan3.setApproved(true);
        loan3.setReturned(false);
        loan3.setActualStartDate(today.minusDays(10));

        // Act
        LibraryStats stats = reportingService.generateLibraryStats();

        // Assert
        assertNotNull(stats, "LibraryStats should not be null");
        assertEquals(2, stats.getTotalBooks(), "Total books should be 2");
        assertEquals(3, stats.getTotalLoans(), "Total loans should be 3");
        assertEquals(1, stats.getActiveLoans(), "Active loans should be 1");
        
        // Average should be (10 + 15) / 2 = 12.5
        assertEquals(12.5, stats.getAverageLoanDays(), 0.01,
                    "Average loan days should be 12.5");
    }
}

