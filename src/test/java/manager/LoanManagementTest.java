package manager;

import exception.BookNotAvailableException;
import exception.InvalidRequestStatusException;
import exception.InvalidStudentStatusException;
import model.Book;
import model.BorrowRequest;
import model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * سناریو ۳: مدیریت امانت
 */
public class LoanManagementTest {
    private LoanManager loanManager;
    private StudentManager studentManager;
    private BookManager bookManager;
    private Student activeStudent;
    private Student inactiveStudent;
    private Book availableBook;
    private Book borrowedBook;

    @BeforeEach
    void setUp() {
        loanManager = new LoanManager();
        studentManager = new StudentManager();
        bookManager = new BookManager();

        // Create active student
        studentManager.register("Ali Ahmadi", "401001", "ali", "1111");
        activeStudent = studentManager.findStudentByUsername("ali");
        assertNotNull(activeStudent, "Active student should be created");

        // Create inactive student
        studentManager.register("Sara Karimi", "401002", "sara", "2222");
        inactiveStudent = studentManager.findStudentByUsername("sara");
        inactiveStudent.setActive(false);

        // Create available book
        availableBook = new Book("Discrete Mathematics", "Rosen", 2019, "ISBN-0001", "emp1");
        bookManager.addBook(availableBook);

        // Create borrowed book
        borrowedBook = new Book("Introduction to Algorithms", "CLRS", 2022, "ISBN-0002", "emp1");
        borrowedBook.setAvailable(false);
        bookManager.addBook(borrowedBook);
    }

    /**
     * 3-1: یک دانشجوی فعال برای یک کتاب موجود درخواست امانت می‌دهد.
     * رفتار مورد انتظار: یک شیء BorrowRequest با وضعیت PENDING ایجاد و برگردانده می‌شود.
     */
    @Test
    void testActiveStudentRequestsLoanForAvailableBook() throws Exception {
        // Act
        BorrowRequest request = loanManager.requestLoan(
            activeStudent, availableBook, 
            LocalDate.now(), LocalDate.now().plusDays(7),
            studentManager, bookManager
        );

        // Assert
        assertNotNull(request, "BorrowRequest should not be null");
        assertEquals(BorrowRequest.RequestStatus.PENDING, request.getStatus(), 
                    "Request status should be PENDING");
        assertEquals(activeStudent.getUsername(), request.getStudentUsername(),
                    "Request should belong to active student");
        assertEquals(availableBook.getIsbn(), request.getBookIsbn(),
                    "Request should be for available book");
    }

    /**
     * 3-2: یک دانشجوی غیرفعال سعی می‌کند درخواست امانت بدهد.
     * رفتار مورد انتظار: یک Exception (مثلاً InvalidStudentStatusException) پرتاب می‌شود.
     */
    @Test
    void testInactiveStudentTriesToRequestLoan() {
        // Act & Assert
        assertThrows(InvalidStudentStatusException.class, () -> {
            loanManager.requestLoan(
                inactiveStudent, availableBook,
                LocalDate.now(), LocalDate.now().plusDays(7),
                studentManager, bookManager
            );
        }, "Should throw InvalidStudentStatusException for inactive student");
    }

    /**
     * 3-3: درخواست امانت برای کتابی که وضعیت آن BORROWED است.
     * رفتار مورد انتظار: یک Exception (مثلاً BookNotAvailableException) پرتاب می‌شود.
     */
    @Test
    void testRequestLoanForBorrowedBook() {
        // Act & Assert
        assertThrows(BookNotAvailableException.class, () -> {
            loanManager.requestLoan(
                activeStudent, borrowedBook,
                LocalDate.now(), LocalDate.now().plusDays(7),
                studentManager, bookManager
            );
        }, "Should throw BookNotAvailableException for borrowed book");
    }

    /**
     * 3-4: تایید یک درخواست امانت معتبر.
     * رفتار مورد انتظار: وضعیت درخواست به APPROVED تغییر می‌کند و وضعیت کتاب به BORROWED تغییر می‌کند.
     */
    @Test
    void testApproveValidLoanRequest() throws Exception {
        // Arrange
        BorrowRequest request = loanManager.requestLoan(
            activeStudent, availableBook,
            LocalDate.now(), LocalDate.now().plusDays(7),
            studentManager, bookManager
        );
        assertTrue(availableBook.isAvailable(), "Book should be available before approval");

        // Act
        loanManager.approveRequest(request, "emp1", bookManager);

        // Assert
        assertEquals(BorrowRequest.RequestStatus.APPROVED, request.getStatus(),
                    "Request status should be APPROVED");
        assertFalse(availableBook.isAvailable(), "Book should not be available after approval");
    }

    /**
     * 3-5: تلاش برای تایید یک درخواست که قبلاً تایید شده است.
     * رفتار مورد انتظار: یک Exception (مثلاً InvalidRequestStatusException) پرتاب می‌شود.
     */
    @Test
    void testApproveAlreadyApprovedRequest() throws Exception {
        // Arrange
        BorrowRequest request = loanManager.requestLoan(
            activeStudent, availableBook,
            LocalDate.now(), LocalDate.now().plusDays(7),
            studentManager, bookManager
        );
        loanManager.approveRequest(request, "emp1", bookManager);
        assertEquals(BorrowRequest.RequestStatus.APPROVED, request.getStatus());

        // Act & Assert
        assertThrows(InvalidRequestStatusException.class, () -> {
            loanManager.approveRequest(request, "emp2", bookManager);
        }, "Should throw InvalidRequestStatusException when approving already approved request");
    }
}

