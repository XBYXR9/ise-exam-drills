# GHERKIN / ACCEPTANCE TEMPLATE — copy, rename, done

**Given = Arrange · When = Act · Then = Assert.** Graded: usually **≥ 2 `Given` and ≥ 2 `Then` per scenario**,
and **at least one happy path AND one failure path**. Use the words of the task for pages, buttons, messages.

```gherkin
Feature: <what the user can do, one line>
  As a <role>
  I want <goal>
  So that <benefit>

  Background:
    Given the system is running
    And <the shared setup every scenario needs, e.g. "I am on the login page">

  Scenario: <happy path, one sentence>
    Given <precondition 1>
    And <precondition 2>
    When <the ONE action the user takes>
    Then <visible result 1>
    And <visible result 2 or system-side effect>

  Scenario: <failure path>
    Given <precondition 1>
    And <precondition 2>
    When <the same action with bad data>
    Then <error message is shown: "...">
    And <nothing changed / user stays on the page / access denied>

  Scenario Outline: <same steps, many data rows>
    Given I am registered with username "<username>" and password "<password>"
    When I log in with username "<usernameInput>" and password "<passwordInput>"
    Then I see "<result>"

    Examples:
      | username | password    | usernameInput | passwordInput | result          |
      | student  | password123 | student       | password123   | the dashboard   |
      | student  | password123 | student       | wrong         | an error message |
```

## Worked example (login)

```gherkin
Feature: Login
  Scenario: Successful login with valid credentials
    Given I am on the login page
    And I am registered with username "student" and password "password123"
    When I enter the username "student"
    And I enter the password "password123"
    And I click the login button
    Then I am redirected to the dashboard
    And I see the message "Welcome, student"

  Scenario: Login fails with invalid credentials
    Given I am on the login page
    And I am registered with username "student" and password "password123"
    When I enter the username "student"
    And I enter the password "wrong"
    And I click the login button
    Then I see the error message "Invalid username or password"
    And I remain on the login page
```

## Rules
- One `When` per scenario (one trigger). Several `And`s after a `Given`/`Then` are fine.
- `Then` describes **observable** results (message, page, state), never "the database row is inserted".
- Use `And` instead of repeating `Given`/`Then`. Keep scenarios independent of each other.
- Repeated setup in many scenarios → `Background:` (or "Common Includes"); same logic with many data rows → `Scenario Outline` + `Examples` (or "Parameterized Includes").
- Three Amigos (domain expert, developer, tester) write scenarios **together** — developers alone is the anti-pattern.
