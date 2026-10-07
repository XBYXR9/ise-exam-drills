package de.tum.ise;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * JUNIT TEST TEMPLATE
 *
 * How to use:
 * 1. Replace MyClass, MyEnum, and the numbers with the ones from your task.
 * 2. Keep the pattern: ARRANGE (set up), ACT (call method), ASSERT (check).
 * 3. For every method, write at least: normal case, edge case, failure case.
 *
 * Remember: assertEquals(EXPECTED, ACTUAL, delta). Expected value goes first!
 */
public class TestTemplate {

    // Use a delta for every double comparison (decimals are never exact)
    private static final double DELTA = 0.0001;

    private MyClass obj;

    // Runs before every test, so each test starts with a fresh object
    @BeforeEach
    void setUp() {
        obj = new MyClass(50.0, MyEnum.A, 2);
    }

    // ---------- 1. NORMAL CASE (happy path) ----------
    @Test
    void testNormalCase() {
        // ARRANGE: write the numbers out by hand from the task text
        double input = 5.0;
        double expectedCost = input * 0.6;                 // e.g. 3.0
        double expectedRemaining = 50.0 - expectedCost;    // e.g. 47.0

        // ACT
        Result result = obj.doSomething(MyEnum.A, input);

        // ASSERT: check EVERYTHING the task says to check
        assertTrue(result.isSuccess());
        assertEquals(expectedCost, result.getCost(), DELTA);
        assertEquals(expectedRemaining, obj.getValue(), DELTA);
    }

    // ---------- 2. OTHER BRANCH (the "else" of your if) ----------
    @Test
    void testOtherBranch() {
        double input = 5.0;
        double expectedCost = input * 1.5;                 // e.g. 7.5

        Result result = obj.doSomething(MyEnum.B, input);

        assertTrue(result.isSuccess());
        assertEquals(expectedCost, result.getCost(), DELTA);
        assertEquals(50.0 - expectedCost, obj.getValue(), DELTA);
    }

    // ---------- 3. FAILURE CASE (not enough resources) ----------
    @Test
    void testFailureCase() {
        MyClass empty = new MyClass(0.0, MyEnum.A, 2);

        Result result = empty.doSomething(MyEnum.B, 5.0);

        assertFalse(result.isSuccess());
        assertEquals(0.0, empty.getValue(), DELTA);        // value must be unchanged
        assertEquals(0.0, result.getCost(), DELTA);
    }

    // ---------- 4. CAP / LIMIT CASE (use min or max) ----------
    @Test
    void testCap() {
        double cap = 600.0 + 2 * 40.0;                     // 680.0

        double result = obj.addValue(60000.0);

        assertEquals(cap, result, DELTA);                  // exactly the cap
        assertEquals(cap, obj.getValue(), DELTA);          // and stored in the object
    }

    // ---------- 5. EXACT BOUNDARY (value exactly at the limit) ----------
    @Test
    void testExactBoundary() {
        // Enough RAM: ram == cost. The check is "ram < cost", so this must succeed.
        MyClass exact = new MyClass(3.0, MyEnum.A, 2);

        Result result = exact.doSomething(MyEnum.A, 5.0);  // cost = 3.0

        assertTrue(result.isSuccess());
        assertEquals(0.0, exact.getValue(), DELTA);
    }

    // ---------- 6. INVALID INPUT (zero or negative) ----------
    @Test
    void testInvalidInput() {
        double before = obj.getValue();

        double result = obj.addValue(0.0);

        assertEquals(before, result, DELTA);               // nothing changed
        assertEquals(before, obj.getValue(), DELTA);
    }

    // ---------- 7. EXCEPTION (only if the task says one is thrown) ----------
    @Test
    void testException() {
        assertThrows(IllegalArgumentException.class, () -> obj.doSomething(null, 5.0));
    }

    // ---------- Quick reference ----------
    // assertTrue(cond)                     check something is true
    // assertFalse(cond)                    check something is false
    // assertEquals(exp, act)               ints, enums, strings
    // assertEquals(exp, act, DELTA)        doubles (always with delta!)
    // assertNull(x) / assertNotNull(x)     null checks
    // assertSame(exp, act)                 same object (==)
    // assertThrows(Ex.class, () -> code)   expect an exception

    // ---------- Dummy classes so this file compiles. DELETE these. ----------
    enum MyEnum { A, B }

    static class Result {
        boolean isSuccess() { return true; }
        double getCost() { return 0; }
    }

    static class MyClass {
        MyClass(double v, MyEnum e, int level) { }
        Result doSomething(MyEnum e, double x) { return new Result(); }
        double addValue(double x) { return 0; }
        double getValue() { return 0; }
    }
}
