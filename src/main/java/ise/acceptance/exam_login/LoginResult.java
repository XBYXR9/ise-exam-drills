package ise.acceptance.exam_login;

/**
 * The outcome of a login attempt, shaped so that every Then step of the Gherkin
 * scenarios has something to assert on:
 *   "I should access my account"          -> isAccessGranted()
 *   "I should see an error message"        -> hasErrorMessage()
 *   "I should remain on the login page"    -> isOnLoginPage()
 */
public class LoginResult {

    private final boolean accessGranted;
    private final String errorMessage;

    private LoginResult(boolean accessGranted, String errorMessage) {
        this.accessGranted = accessGranted;
        this.errorMessage = errorMessage;
    }

    public static LoginResult granted() {
        return new LoginResult(true, null);
    }

    public static LoginResult denied(String errorMessage) {
        return new LoginResult(false, errorMessage);
    }

    public boolean isAccessGranted() {
        return accessGranted;
    }

    public boolean hasErrorMessage() {
        return errorMessage != null && !errorMessage.isBlank();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    /** A failed login leaves the user where they were. */
    public boolean isOnLoginPage() {
        return !accessGranted;
    }
}
