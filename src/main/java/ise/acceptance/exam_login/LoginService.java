package ise.acceptance.exam_login;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock exam (Jun 2026), exercise 1 -- the SUT behind the Gherkin scenarios.
 *
 * US1: "As a registered user, I want to log in with my username and password so that
 *       I can access my account."
 *
 * The exercise itself only asks for the two scenarios in text. Implementing the
 * service turns each Given/When/Then line into an executable assertion, which is
 * exactly what an acceptance test is supposed to become.
 */
public class LoginService {

    public static final String INVALID_CREDENTIALS = "Invalid username or password";

    private final Map<String, String> registeredUsers = new HashMap<>();

    public void register(String username, String password) {
        registeredUsers.put(username, password);
    }

    public boolean isRegistered(String username) {
        return registeredUsers.containsKey(username);
    }

    public LoginResult login(String username, String password) {
        String stored = registeredUsers.get(username);
        // One message for both failure modes, on purpose: telling the user WHICH half
        // was wrong hands an attacker a list of valid usernames.
        if (stored == null || !stored.equals(password)) {
            return LoginResult.denied(INVALID_CREDENTIALS);
        }
        return LoginResult.granted();
    }
}
