# Exam templates — copy, paste, rename

Every `.java` file is self-contained: it has a **working dummy class at the bottom** so you can run it as
is and see it green, then replace the dummy with your real class and rename. All of them were compiled and
run in this project (JUnit 5.10, EasyMock 5.2, Spring 3.2). Package line says `de.tum.ise` — change it to the
exam project's package.

| The task says… | Use | What is inside |
|---|---|---|
| "implement the class / enum / method, then test it" | [`ImplementTemplate.java`](ImplementTemplate.java) | enum (plain and with a value), result class, guards, compute→check→mutate, `Math.min` cap |
| "write unit tests" (normal, other branch, failure, cap, exact boundary, invalid, exception) | [`TestTemplate.java`](TestTemplate.java) | the seven tests every class needs, `@BeforeEach`, delta |
| exceptions, void methods, lists, object equality, `assertAll`, `@Nested`, skip | [`JUnitExtrasTemplate.java`](JUnitExtrasTemplate.java) | `assertThrows` + message, state assertions, collections, `assertSame`, `assumeTrue` |
| a table of inputs/expected results, boundaries, "many cases" | [`ParameterizedTemplate.java`](ParameterizedTemplate.java) | `@CsvSource`, `@ValueSource`, `@NullAndEmptySource`, `@MethodSource`, `@EnumSource` |
| "use a mock", manual style (`createMock`) | [`MockTemplate.java`](MockTemplate.java) | create → record → replay → verify, `times`, void, throw, `anyString` |
| "annotate the test class", `@TestSubject` / `@Mock` | [`EasyMockAnnotationTemplate.java`](EasyMockAnnotationTemplate.java) | the 3 injection rules, nice mock, capture, `andAnswer`, matchers, strict order, never-called |
| "implement the REST endpoints" | [`RestResourceTemplate.java`](RestResourceTemplate.java) | service over a list + resource with all 5 verbs, status codes, query param |
| "test the endpoints" | [`RestTestTemplate.java`](RestTestTemplate.java) | `MockMvc` standalone, one test per status-code row, read-back after update/delete |
| "equivalence classes / boundary values" | [`BlackBoxTemplate.md`](BlackBoxTemplate.md) | the three tables with `<fill>` cells, class-count check, L−1 / L / L+1 recipe |
| "write acceptance tests / Gherkin" | [`GherkinTemplate.md`](GherkinTemplate.md) | Feature, Background, happy + failure scenario, Scenario Outline |

## Rename checklist (60 seconds)

1. **Copy names from the task character-for-character** — class, method, enum constants, `@PathVariable` names.
2. Replace the dummy classes at the bottom of the file; delete the dummy `// ----- DELETE these -----` block.
3. Fix the numbers: write the expected values **by hand from the task's formulas**, never by calling the SUT.
4. Run it. A test must call the method under test, assert the result **and** the state, and use a delta on doubles.
5. `git add . && git commit -m "..." && git push`.

## The one question to ask of every test

> If I deleted the body of the method I am testing, would this test fail?

If the answer is no, it scores zero (the grader runs your tests against a broken implementation).
