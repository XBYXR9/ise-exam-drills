package ise.acceptance.exam_login;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MOCK EXAM (Jun 2026), EXERCISE 1 -- Acceptance tests, 5 points.
 *
 * The exam answer is TEXT: two Gherkin scenarios. The finished text is in
 * docs/MOCK_EXAM.md. This class executes those scenarios so you can see that each
 * Then step is actually true, and it maps Gherkin onto the AAA skeleton you already
 * know:
 *
 *     Given  =  Arrange
 *     When   =  Act
 *     Then   =  Assert
 *
 * THE TWO REQUIREMENTS THE TASK GRADES:
 *   at least two Given steps per scenario
 *   at least two Then steps per scenario
 * Both are marked in the comments below so you can count them at a glance.
 */
class LoginAcceptanceTest {

    private LoginService loginService;
    private boolean onLoginPage;
    private String enteredUsername;
    private String enteredPassword;

    @BeforeEach
    void setUp() {
        loginService = new LoginService();
        onLoginPage = false;
    }

    @Test
    @DisplayName("Scenario: successful login with valid credentials")
    void happyPath() {
        // GIVEN I am on the login page                                    (Given 1)
        onLoginPage = true;
        // GIVEN I am registered with username "student" and password "password123"  (Given 2)
        loginService.register("student", "password123");

        // WHEN I enter the username "student"
        enteredUsername = "student";
        // AND I enter the password "password123"
        enteredPassword = "password123";
        // AND I log in on the website
        LoginResult result = loginService.login(enteredUsername, enteredPassword);

        // THEN I should access my account                                 (Then 1)
        assertTrue(result.isAccessGranted());
        // AND I should not see any error message                          (Then 2)
        assertFalse(result.hasErrorMessage());
        // AND I should no longer be on the login page
        assertFalse(result.isOnLoginPage());
        assertTrue(onLoginPage, "the scenario starts on the login page");
    }

    @Test
    @DisplayName("Scenario: login is refused when the password is wrong")
    void invalidCredentials() {
        // GIVEN I am on the login page                                    (Given 1)
        onLoginPage = true;
        // GIVEN I am registered with username "student" and password "password123"  (Given 2)
        loginService.register("student", "password123");

        // WHEN I enter the username "student"
        enteredUsername = "student";
        // AND I enter the password "wrongpassword"
        enteredPassword = "wrongpassword";
        // AND I log in on the website
        LoginResult result = loginService.login(enteredUsername, enteredPassword);

        // THEN I should not access my account                             (Then 1)
        assertFalse(result.isAccessGranted());
        // AND I should see an error message                               (Then 2)
        assertTrue(result.hasErrorMessage());
        assertEquals(LoginService.INVALID_CREDENTIALS, result.getErrorMessage());
        // AND I should remain on the login page                           (Then 3)
        assertTrue(result.isOnLoginPage());
    }

    @Test
    @DisplayName("an unknown username is refused with the same message, so no account is revealed")
    void unknownUsername() {
        loginService.register("student", "password123");

        LoginResult result = loginService.login("chud", "password123");

        assertFalse(result.isAccessGranted());
        // Identical message for "wrong user" and "wrong password". Different messages
        // would let an attacker enumerate valid usernames -- a security requirement
        // hiding inside an acceptance test.
        assertEquals(LoginService.INVALID_CREDENTIALS, result.getErrorMessage());
        assertFalse(loginService.isRegistered("chud"));
    }
}
