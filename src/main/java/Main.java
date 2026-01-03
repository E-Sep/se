import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import model.Book;
import model.Employee;
import model.Loan;
import model.Student;
import system.LibrarySystem;
import system.MenuHandler;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static LibrarySystem system = new LibrarySystem();
    private static Student currentStudent = null;
    private static Employee currentEmployee = null;

    public static void main(String[] args) {
        System.out.println("=== University Library Management System ===\n");
		seedSampleData();
        displayMainMenu();
    }

	private static void seedSampleData() {
		// Seed Employees
		system.getEmployeeManager().addEmployee("emp1", "1234");
		system.getEmployeeManager().addEmployee("emp2", "1234");
		system.getEmployeeManager().addEmployee("emp3", "1234");

		// Seed Students
        system.getStudentManager().registerStudent("st1", "1", "st1", "1");
		system.getStudentManager().registerStudent("Ali Ahmadi", "401001", "ali", "1111");
		system.getStudentManager().registerStudent("Sara Karimi", "401002", "sara", "2222");
		system.getStudentManager().registerStudent("Reza Hosseini", "401003", "reza", "3333");

		// Seed Books (registered by emp1)
		system.getBookManager().addBook(new Book("Discrete Mathematics", "Rosen", 2019, "ISBN-0001", "emp1"));
		system.getBookManager().addBook(new Book("Introduction to Algorithms", "CLRS", 2022, "ISBN-0002", "emp1"));
		system.getBookManager().addBook(new Book("Clean Code", "Robert C. Martin", 2008, "ISBN-0003", "emp2"));
		system.getBookManager().addBook(new Book("Design Patterns", "GoF", 1994, "ISBN-0004", "emp3"));
	}

    // ========== MAIN MENU ==========
    private static void displayMainMenu() {
        while (true) {
            System.out.println("\n========== Main Menu ==========");
            System.out.println("1. Student Options");
            System.out.println("2. Guest Options");
            System.out.println("3. Library Employee Login");
            System.out.println("4. Manager Login");
            System.out.println("5. Exit");
            System.out.print("Please enter your choice: ");

            int choice = getIntInput(1, 5);

            switch (choice) {
                case 1:
                    displayStudentMenu();
                    break;
                case 2:
                    displayGuestMenu();
                    break;
                case 3:
                    employeeLogin();
                    break;
                case 4:
                    managerLogin();
                    break;
                case 5:
                    System.out.println("Exiting system. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    // ========== STUDENT MENU ==========
    private static void displayStudentMenu() {
        while (true) {
            System.out.println("\n========== Student Menu ==========");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Back to Main Menu");
            System.out.print("Please enter your choice: ");

            int choice = getIntInput(1, 3);

            switch (choice) {
                case 1:
                    studentRegister();
                    break;
                case 2:
                    studentLogin();
                    break;
                case 3:
                    return;
            }
        }
    }

    private static void studentRegister() {
        System.out.println("\n--- Student Registration ---");
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Student ID: ");
        String studentId = scanner.nextLine();
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        system.getStudentManager().registerStudent(name, studentId, username, password);
    }

    private static void studentLogin() {
        System.out.println("\n--- Student Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        currentStudent = system.authenticateStudent(username, password);
        if (currentStudent != null) {
            if (currentStudent.isActive()) {
                System.out.println("Login successful! Welcome, " + currentStudent.getName());
                displayLoggedInStudentMenu();
            } else {
                System.out.println("Your account is inactive. Please contact the manager.");
                currentStudent = null;
            }
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private static void displayLoggedInStudentMenu() {
        while (currentStudent != null) {
            System.out.println("\n========== Student Dashboard ==========");
            System.out.println("1. Search Books");
            System.out.println("2. Request Book Loan");
            System.out.println("3. View My Loan History");
            System.out.println("4. Logout");
            System.out.print("Please enter your choice: ");

            int choice = getIntInput(1, 4);

            switch (choice) {
                case 1:
                    searchBooks();
                    break;
                case 2:
                    requestBookLoan();
                    break;
                case 3:
                    viewMyLoanHistory();
                    break;
                case 4:
                    currentStudent = null;
                    System.out.println("Logged out successfully.");
                    return;
            }
        }
    }

    private static void searchBooks() {
        System.out.println("\n--- Search Books ---");
        System.out.println("Leave fields empty to search by any value");
        System.out.print("Book Title (or empty): ");
        String title = scanner.nextLine();
        System.out.print("Author (or empty): ");
        String author = scanner.nextLine();
        System.out.print("Publish Year (or empty): ");
        String yearStr = scanner.nextLine();
        Integer year = yearStr.isEmpty() ? null : Integer.parseInt(yearStr);

        List<Book> results = system.getBookManager().searchBooks(title, author, year);
        
        if (results.isEmpty()) {
            System.out.println("No books found.");
        } else {
            System.out.println("\nSearch Results:");
            for (Book book : results) {
                System.out.println(book);
            }
        }
    }

    private static void requestBookLoan() {
        System.out.println("\n--- Request Book Loan ---");
        System.out.print("Enter book ISBN: ");
        String isbn = scanner.nextLine();
        
        Book book = system.getBookManager().findBookByIsbn(isbn);
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }

        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startStr = scanner.nextLine();
        System.out.print("Enter end date (YYYY-MM-DD): ");
        String endStr = scanner.nextLine();

        try {
            LocalDate startDate = LocalDate.parse(startStr);
            LocalDate endDate = LocalDate.parse(endStr);
            
            system.getLoanManager().createLoanRequest(currentStudent.getUsername(), isbn, startDate, endDate);
            System.out.println("Loan request submitted successfully. Please wait for employee approval.");
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD");
        }
    }

    private static void viewMyLoanHistory() {
        List<Loan> history = system.getLoanManager().getStudentLoanHistory(currentStudent.getUsername());
        
        if (history.isEmpty()) {
            System.out.println("No loan history found.");
        } else {
            System.out.println("\nMy Loan History:");
            for (Loan loan : history) {
                System.out.println(loan);
            }
        }
    }

    // ========== GUEST MENU ==========
    private static void displayGuestMenu() {
        while (true) {
            System.out.println("\n========== Guest Menu ==========");
			System.out.println("1. View Number of Registered Students");
			System.out.println("2. Search Books by Title");
			System.out.println("3. View Statistics");
			System.out.println("4. View All Books");
			System.out.println("5. Back to Main Menu");
            System.out.print("Please enter your choice: ");

			int choice = getIntInput(1, 5);

            switch (choice) {
                case 1:
                    viewStudentCount();
                    break;
                case 2:
                    searchBooksGuest();
                    break;
                case 3:
                    viewGuestStatistics();
                    break;
				case 4:
					viewAllBooks();
					break;
				case 5:
                    return;
            }
        }
    }

	private static void viewAllBooks() {
		List<Book> books = system.getBookManager().getAllBooks();
		System.out.println("\n--- All Books ---");
		if (books.isEmpty()) {
			System.out.println("No books available.");
			return;
		}
		for (Book book : books) {
			System.out.println(book);
		}
	}

    private static void viewStudentCount() {
        int count = system.getStudentCount();
        System.out.println("\nTotal registered students: " + count);
    }

    private static void searchBooksGuest() {
        System.out.println("\n--- Search Books by Title ---");
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();

        List<Book> results = system.getBookManager().searchBooksByTitle(title);
        
        if (results.isEmpty()) {
            System.out.println("No books found.");
        } else {
            System.out.println("\nSearch Results:");
            for (Book book : results) {
                System.out.println(book);
            }
        }
    }

    private static void viewGuestStatistics() {
        System.out.println("\n--- Statistics ---");
        System.out.println("Total Students: " + system.getStudentCount());
        System.out.println("Total Books: " + system.getBookManager().getTotalBooksCount());
        System.out.println("Total Loan Requests: " + system.getLoanManager().getTotalLoanRequests());
        System.out.println("Current Books on Loan: " + system.getLoanManager().getCurrentLoansCount());
    }

    // ========== EMPLOYEE MENU ==========
    private static void employeeLogin() {
        System.out.println("\n--- Library Employee Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        currentEmployee = system.getEmployeeManager().authenticateEmployee(username, password);
        if (currentEmployee != null) {
            System.out.println("Login successful! Welcome, " + currentEmployee.getUsername());
            displayEmployeeMenu();
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private static void displayEmployeeMenu() {
        while (currentEmployee != null) {
            System.out.println("\n========== Employee Dashboard ==========");
            System.out.println("1. Change Password");
            System.out.println("2. Register New Book");
            System.out.println("3. Search and Edit Book");
            System.out.println("4. Review and Approve Loan Requests");
            System.out.println("5. View Student Loan History");
            System.out.println("6. Activate/Deactivate Student");
            System.out.println("7. Return Book");
            System.out.println("8. Logout");
            System.out.print("Please enter your choice: ");

            int choice = getIntInput(1, 8);

            switch (choice) {
                case 1:
                    changePassword();
                    break;
                case 2:
                    registerNewBook();
                    break;
                case 3:
                    searchAndEditBook();
                    break;
                case 4:
                    reviewLoanRequests();
                    break;
                case 5:
                    viewStudentLoanHistory();
                    break;
                case 6:
                    toggleStudentStatus();
                    break;
                case 7:
                    returnBook();
                    break;
                case 8:
                    currentEmployee = null;
                    System.out.println("Logged out successfully.");
                    return;
            }
        }
    }

    private static void changePassword() {
        System.out.println("\n--- Change Password ---");
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();
        currentEmployee.setPassword(newPassword);
        System.out.println("Password changed successfully.");
    }

    private static void registerNewBook() {
        System.out.println("\n--- Register New Book ---");
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Author: ");
        String author = scanner.nextLine();
        System.out.print("Publish Year: ");
        int year = Integer.parseInt(scanner.nextLine());
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();

        Book book = new Book(title, author, year, isbn, currentEmployee.getUsername());
        system.getBookManager().addBook(book);
        currentEmployee.incrementBooksRegistered();
        System.out.println("Book registered successfully.");
    }

    private static void searchAndEditBook() {
        System.out.println("\n--- Search and Edit Book ---");
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();

        Book book = system.getBookManager().findBookByIsbn(isbn);
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }

        System.out.println("Current book: " + book);
        System.out.print("New title (or empty to keep): ");
        String newTitle = scanner.nextLine();
        System.out.print("New author (or empty to keep): ");
        String newAuthor = scanner.nextLine();
        System.out.print("New year (or empty to keep): ");
        String yearStr = scanner.nextLine();
        Integer newYear = yearStr.isEmpty() ? null : Integer.parseInt(yearStr);

        system.getBookManager().editBook(isbn, newTitle, newAuthor, newYear);
        System.out.println("Book updated successfully.");
    }

    private static void reviewLoanRequests() {
        System.out.println("\n--- Review Loan Requests ---");
        List<Loan> pendingRequests = system.getLoanManager().getPendingLoanRequests();
        
        if (pendingRequests.isEmpty()) {
            System.out.println("No pending loan requests.");
            return;
        }

        System.out.println("Pending loan requests:");
        for (int i = 0; i < pendingRequests.size(); i++) {
            Loan loan = pendingRequests.get(i);
            System.out.println(i + 1 + ". " + loan);
        }

        System.out.print("Enter request number to approve (or 0 to skip): ");
        int choice = Integer.parseInt(scanner.nextLine());
        
        if (choice > 0 && choice <= pendingRequests.size()) {
            Loan loan = pendingRequests.get(choice - 1);
            system.getLoanManager().approveLoanRequest(
                loan.getStudentUsername() + "_" + loan.getBookIsbn(), 
                currentEmployee.getUsername()
            );
            currentEmployee.incrementBooksLent();
            System.out.println("Loan request approved.");
        }
    }

    private static void viewStudentLoanHistory() {
        System.out.println("\n--- Student Loan History ---");
        System.out.print("Enter student username: ");
        String username = scanner.nextLine();

        List<Loan> history = system.getLoanManager().getStudentLoanHistory(username);
        
        if (history.isEmpty()) {
            System.out.println("No loan history found.");
        } else {
            System.out.println("\nLoan History:");
            for (Loan loan : history) {
                System.out.println(loan);
            }
            
            // Statistics
            long totalLoans = history.size();
            long undelivered = history.stream().filter(l -> l.isApproved() && !l.isReturned()).count();
            long lateReturns = history.stream().filter(l -> l.wasLateReturn()).count();
            
            System.out.println("\nStatistics:");
            System.out.println("Total loans: " + totalLoans);
            System.out.println("Undelivered books: " + undelivered);
            System.out.println("Late returns: " + lateReturns);
        }
    }

    private static void toggleStudentStatus() {
        System.out.println("\n--- Toggle Student Status ---");
        System.out.print("Enter student username: ");
        String username = scanner.nextLine();

        Student student = system.getStudentManager().findStudentByUsername(username);

        if (student != null) {
            student.setActive(!student.isActive());
            System.out.println("Student status updated to: " + (student.isActive() ? "Active" : "Inactive"));
        } else {
            System.out.println("Student not found.");
        }
    }

    private static void returnBook() {
        System.out.println("\n--- Return Book ---");
        System.out.print("Enter student username: ");
        String studentUsername = scanner.nextLine();
        System.out.print("Enter book ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Was the return late? (yes/no): ");
        String lateStr = scanner.nextLine();
        
        boolean wasLate = lateStr.toLowerCase().equals("yes");
        
        system.getLoanManager().returnBook(studentUsername, isbn, currentEmployee.getUsername(), wasLate);
        currentEmployee.incrementBooksReturned();
        System.out.println("Book returned successfully.");
    }

    // ========== MANAGER MENU ==========
    private static void managerLogin() {
        System.out.println("\n--- Manager Login ---");
        System.out.print("Enter manager password (admin): ");
        String password = scanner.nextLine();

        if (password.equals("admin")) {
            System.out.println("Login successful! Welcome, Manager");
            displayManagerMenu();
        } else {
            System.out.println("Invalid password.");
        }
    }

    private static void displayManagerMenu() {
        while (true) {
            System.out.println("\n========== Manager Dashboard ==========");
            System.out.println("1. Define Library Employee");
            System.out.println("2. View Employee Performance");
            System.out.println("3. View Loan Statistics");
            System.out.println("4. View Student Statistics");
            System.out.println("5. Logout");
            System.out.print("Please enter your choice: ");

            int choice = getIntInput(1, 5);

            switch (choice) {
                case 1:
                    defineEmployee();
                    break;
                case 2:
                    viewEmployeePerformance();
                    break;
                case 3:
                    viewLoanStatistics();
                    break;
                case 4:
                    viewStudentStatistics();
                    break;
                case 5:
                    System.out.println("Logged out successfully.");
                    return;
            }
        }
    }

    private static void defineEmployee() {
        System.out.println("\n--- Define Library Employee ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        system.getEmployeeManager().addEmployee(username, password);
    }

    private static void viewEmployeePerformance() {
        System.out.println("\n--- Employee Performance ---");
        List<Employee> employees = system.getEmployeeManager().getAllEmployees();
        
        if (employees.isEmpty()) {
            System.out.println("No employees registered.");
        } else {
            for (Employee employee : employees) {
                System.out.println(employee);
            }
        }
    }

    private static void viewLoanStatistics() {
        System.out.println("\n--- Loan Statistics ---");
        System.out.println("Total Loan Requests: " + system.getLoanManager().getTotalLoanRequests());
        System.out.println("Total Books Lent: " + system.getLoanManager().getTotalApprovedLoans());
        System.out.println("Average Loan Days: " + String.format("%.2f", system.getLoanManager().getAverageLoanDays()));
    }

    private static void viewStudentStatistics() {
        System.out.println("\n--- Student Statistics ---");
        List<Student> students = system.getStudentManager().getAllStudents();
        
        System.out.println("Total Students: " + students.size());
        System.out.println("Active Students: " + students.stream().filter(s -> s.isActive()).count());
        System.out.println("Inactive Students: " + students.stream().filter(s -> !s.isActive()).count());
        
        System.out.println("\nTop 10 Students with Most Delays:");
        List<String> topDelays = system.getLoanManager().getStudentsWithMostDelays(10);
        for (int i = 0; i < topDelays.size(); i++) {
            System.out.println((i + 1) + ". " + topDelays.get(i));
        }
    }

    // ========== HELPER METHODS ==========
    private static int getIntInput(int min, int max) {
        while (true) {
            try {
                int input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.printf("Please enter a number between %d and %d: ", min, max);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
}

/*
راهنمای ورود و استفاده از سیستم (نمونه‌های از پیش ثبت‌شده):

1) مدیر سیستم (Manager)
- ورود: از منوی اصلی گزینه 4 را انتخاب کنید > رمز: admin
- گزینه‌ها:
  1. تعریف کارمند جدید (Username/Password)
  2. مشاهده عملکرد کارمندان
  3. آمار امانت‌ها (تعداد درخواست‌ها، تعداد امانت‌های تایید شده، میانگین روزها)
  4. آمار دانشجویان (فعال/غیرفعال، 10 دانشجوی با بیشترین تاخیر)

2) کارمند کتابخانه (Employee)
- ورود: از منوی اصلی گزینه 3 را انتخاب کنید
- نمونه کاربرها: emp1/1234 ، emp2/1234 ، emp3/1234
- گزینه‌ها پس از ورود:
  1. تغییر رمز عبور
  2. ثبت کتاب جدید
  3. جستجو و ویرایش کتاب (با ISBN)
  4. بررسی و تایید درخواست‌های امانت (درخواست‌های امروز/دیروز)
  5. مشاهده تاریخچه امانات یک دانشجو + آمار (کل، تحویل‌نشده، با تاخیر)
  6. فعال/غیرفعال کردن دانشجو (با username)
  7. ثبت بازگشت کتاب (student username + ISBN + دیرکرد؟)

3) دانشجو (Student)
- ثبت‌نام/ورود: از منوی اصلی گزینه 1
- نمونه دانشجوها:
  ali/1111 (Ali Ahmadi) ، sara/2222 (Sara Karimi) ، reza/3333 (Reza Hosseini)
- پس از ورود (Student Dashboard):
  1. جستجوی کتاب (ترکیبی از عنوان/نویسنده/سال)
  2. ثبت درخواست امانت (ISBN + تاریخ شروع/پایان به فرمت YYYY-MM-DD)
  3. مشاهده تاریخچه امانات خود

4) کاربر مهمان (Guest)
- از منوی اصلی گزینه 2
- گزینه‌ها:
  1. مشاهده تعداد دانشجویان ثبت‌نام‌شده
  2. جستجوی کتاب صرفاً با عنوان
  3. مشاهده آمار ساده (تعداد دانشجو، کل کتاب‌ها، کل درخواست‌ها، تعداد در امانت)

کتاب‌های نمونه (ISBN):
- ISBN-0001: Discrete Mathematics (Rosen, 2019)
- ISBN-0002: Introduction to Algorithms (CLRS, 2022)
- ISBN-0003: Clean Code (Robert C. Martin, 2008)
- ISBN-0004: Design Patterns (GoF, 1994)

مسیرها در منوها:
- مشاهده تعداد دانشجویان: Main > Guest Options > 1
- جستجوی کتاب (مهمان): Main > Guest Options > 2
- آمار ساده (مهمان): Main > Guest Options > 3
- مشاهده همه کتاب‌ها (مهمان): Main > Guest Options > 4
- جستجوی کتاب (دانشجو): Main > Student > Login > Student Dashboard > 1
- درخواست امانت: Main > Student > Login > Student Dashboard > 2
- تاریخچه امانات من: Main > Student > Login > Student Dashboard > 3
- ثبت کتاب جدید (کارمند): Main > Employee Login > 2
- جستجو/ویرایش کتاب (کارمند): Main > Employee Login > 3
- تایید درخواست‌ها (کارمند): Main > Employee Login > 4
- تاریخچه امانات دانشجو (کارمند): Main > Employee Login > 5
- فعال/غیرفعال دانشجو (کارمند): Main > Employee Login > 6
- ثبت برگشت کتاب (کارمند): Main > Employee Login > 7
- تعریف کارمند (مدیر): Main > Manager Login > 1
- عملکرد کارمندان (مدیر): Main > Manager Login > 2
- آمار امانت‌ها (مدیر): Main > Manager Login > 3
- آمار دانشجویان (مدیر): Main > Manager Login > 4
*/

