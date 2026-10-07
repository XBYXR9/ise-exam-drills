package de.tum.ise;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/*
 * PARAMETERIZED TEST TEMPLATE  (many rows, one test body -- ideal for black-box tables)
 *
 * One row of the exam's table = one line in @CsvSource. Columns map to the method parameters in order.
 * JUnit converts "35" -> int, "ACCEPTED" -> enum, "true" -> boolean, "2.5" -> double automatically.
 * Needs junit-jupiter-params (already in the project). Rename Gate / Decision / decide to your SUT.
 */
public class ParameterizedTemplate {

    private final Gate gate = new Gate();

    // ---------- 1. @CsvSource: inputs AND expected result per row (most common) ----------
    @ParameterizedTest(name = "{0} | {1} | {2} -> {3}")                 // {0}.. = the columns, shows in the report
    @CsvSource({
            // id,   type,       level, expected
            "TC1,   standard,    10,    REJECTED",                      // below the bracket
            "TC2,   standard,    35,    ACCEPTED",                      // inside
            "TC3,   standard,    75,    REJECTED",                      // above
            "TC4,   vip,         10,    ACCEPTED",                      // ON the lower limit  (the mark!)
            "TC5,   admin,       35,    INVALID",                       // invalid type
            "TC6,   standard,    0,     INVALID"                        // invalid level
    })
    void table(String id, String type, int level, Decision expected) {
        assertEquals(expected, gate.decide(type, level), id);           // id as message: failure names the row
    }

    // ---------- 2. Three-point boundary: L-1, L, L+1 ----------
    @ParameterizedTest(name = "vip lower limit: {0} -> {1}")
    @CsvSource({"9, REJECTED", "10, ACCEPTED", "11, ACCEPTED"})
    void boundaryVipLower(int level, Decision expected) {
        assertEquals(expected, gate.decide("vip", level));
    }

    // ---------- 3. @ValueSource: one list of inputs, SAME expected result ----------
    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100, 101, 500})
    void invalidLevels(int level) {
        assertEquals(Decision.INVALID, gate.decide("vip", level));
    }

    // ---------- 4. null / empty / blank strings (case matters: "VIP" is not "vip") ----------
    @ParameterizedTest
    @NullAndEmptySource                                                  // null and ""
    @ValueSource(strings = {"  ", "admin", "VIP", "Standard"})
    void invalidTypes(String type) {
        assertEquals(Decision.INVALID, gate.decide(type, 35));
    }

    // ---------- 5. @MethodSource: when rows are objects or need computing ----------
    static Stream<Arguments> rows() {
        return Stream.of(
                Arguments.of("standard", 20, Decision.ACCEPTED),
                Arguments.of("standard", 19, Decision.REJECTED),
                Arguments.of("vip", 80, Decision.ACCEPTED),
                Arguments.of("vip", 81, Decision.REJECTED));
    }

    @ParameterizedTest
    @MethodSource("rows")                                                // name of the static method
    void fromMethod(String type, int level, Decision expected) {
        assertEquals(expected, gate.decide(type, level));
    }

    // ---------- 6. @EnumSource: run once per enum constant ----------
    @ParameterizedTest
    @EnumSource(Decision.class)
    void everyDecisionHasAName(Decision d) {
        assertNotNull(d.name());
    }

    // ---------- Quick reference ----------
    // @CsvSource separator is ","  | quote text containing a comma: "'a,b', 5"  | 'NULL' needs nullValues = "NULL"
    // @ParameterizedTest REPLACES @Test (do not put both).  Parameter order = column order.
    // Case "expected exception": @ValueSource(ints=...) + assertThrows(Ex.class, () -> sut.m(x)).

    // ---------- Dummy SUT so this file compiles. DELETE these. ----------
    enum Decision { ACCEPTED, REJECTED, INVALID }

    static class Gate {
        Decision decide(String type, int level) {
            if (level < 1 || level > 100) return Decision.INVALID;
            if ("standard".equals(type)) return level >= 20 && level <= 50 ? Decision.ACCEPTED : Decision.REJECTED;
            if ("vip".equals(type)) return level >= 10 && level <= 80 ? Decision.ACCEPTED : Decision.REJECTED;
            return Decision.INVALID;
        }
    }
}
