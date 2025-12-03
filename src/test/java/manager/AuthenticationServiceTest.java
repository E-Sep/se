package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * سناریو ۱: سرویس احراز هویت
 */
public class AuthenticationServiceTest {
    private StudentManager studentManager;

    @BeforeEach
    void setUp() {
        studentManager = new StudentManager();
    }

    /**
     * 1-1: ثبت‌نام یک کاربر جدید با نام کاربری منحصربه‌فرد.
     * رفتار مورد انتظار: متد register مقدار true برمی‌گرداند.
     */
    @Test
    void testRegisterNewUserWithUniqueUsername() {
        // Arrange & Act
        boolean result = studentManager.register("Ali Ahmadi", "401001", "ali", "1111");

        // Assert
        assertTrue(result, "Registration should return true for unique username");
        assertEquals(1, studentManager.getStudentCount(), "Student count should be 1");
    }

    /**
     * 1-2: ثبت‌نام با نام کاربری تکراری.
     * رفتار مورد انتظار: متد register مقدار false برمی‌گرداند.
     */
    @Test
    void testRegisterWithDuplicateUsername() {
        // Arrange
        studentManager.register("Ali Ahmadi", "401001", "ali", "1111");

        // Act
        boolean result = studentManager.register("Sara Karimi", "401002", "ali", "2222");

        // Assert
        assertFalse(result, "Registration should return false for duplicate username");
        assertEquals(1, studentManager.getStudentCount(), "Student count should remain 1");
    }

    /**
     * 1-3: ورود با نام کاربری و رمز عبور صحیح.
     * رفتار مورد انتظار: متد login مقدار true برمی‌گرداند.
     */
    @Test
    void testLoginWithCorrectUsernameAndPassword() {
        // Arrange
        studentManager.register("Ali Ahmadi", "401001", "ali", "1111");

        // Act
        boolean result = studentManager.login("ali", "1111");

        // Assert
        assertTrue(result, "Login should return true for correct credentials");
    }

    /**
     * 1-4: ورود با نام کاربری صحیح اما رمز عبور نادرست.
     * رفتار مورد انتظار: متد login مقدار false برمی‌گرداند.
     */
    @Test
    void testLoginWithCorrectUsernameButWrongPassword() {
        // Arrange
        studentManager.register("Ali Ahmadi", "401001", "ali", "1111");

        // Act
        boolean result = studentManager.login("ali", "wrongpassword");

        // Assert
        assertFalse(result, "Login should return false for wrong password");
    }

    /**
     * 1-5: ورود با نام کاربری که وجود ندارد.
     * رفتار مورد انتظار: متد login مقدار false برمی‌گرداند.
     */
    @Test
    void testLoginWithNonExistentUsername() {
        // Arrange - no users registered

        // Act
        boolean result = studentManager.login("nonexistent", "password");

        // Assert
        assertFalse(result, "Login should return false for non-existent username");
        assertNull(studentManager.authenticateStudent("nonexistent", "password"), 
                  "Authenticate should return null for non-existent user");
    }
}

