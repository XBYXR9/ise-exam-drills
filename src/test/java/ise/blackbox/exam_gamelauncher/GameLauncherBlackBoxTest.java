package ise.blackbox.exam_gamelauncher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * MOCK EXAM (Jun 2026), EXERCISE 4 -- Black-box testing, 10 points.
 *
 * The exam wants three tables in plain text. This class IS those tables, executable:
 * every row below is one row of the answer you would type into Artemis, and running
 * it tells you whether the expected result you wrote down is actually right.
 *
 * The filled-in tables in exam-submission format are in docs/MOCK_EXAM.md.
 *
 * THE RECIPE, in the order the task asks for it:
 *   Part 1  partition each PARAMETER into equivalence classes (valid and invalid)
 *   Part 2  pick ONE representative per class, combining parameters -> 9 test cases
 *   Part 3  for each target boundary L, test L-1, L, L+1
 */
class GameLauncherBlackBoxTest {

    private GameLauncher launcher;

    @BeforeEach
    void setUp() {
        launcher = new GameLauncher();
    }

    @Nested
    @DisplayName("Part 2 -- the 9 representative test cases, one per equivalence class")
    class RepresentativeTestCases {

        @ParameterizedTest(name = "{0} | {1} | {2} | expect {3}  ({4})")
        @CsvSource({
                // TC   accountType  level  expected    class covered
                "TC1,   standard,     10,   REJECTED,   ATC1 + CLC2 standard below bracket",
                "TC2,   standard,     35,   ACCEPTED,   ATC1 + CLC3 standard inside bracket",
                "TC3,   standard,     75,   REJECTED,   ATC1 + CLC4 standard above bracket",
                "TC4,   vip,           5,   REJECTED,   ATC2 + CLC5 vip below bracket",
                "TC5,   vip,          45,   ACCEPTED,   ATC2 + CLC6 vip inside bracket",
                "TC6,   vip,          90,   REJECTED,   ATC2 + CLC7 vip above bracket",
                "TC7,   standard,      0,   INVALID,    CLC1 level at or below zero",
                "TC8,   vip,         101,   INVALID,    CLC8 level above the maximum",
                "TC9,   admin,        35,   INVALID,    ATC3 unknown account type"
        })
        void representativeCases(String testCase, String accountType, int level,
                                 AccessDecision expected, String classCovered) {
            assertEquals(expected, launcher.checkBetaAccess(accountType, level),
                    testCase + " (" + classCovered + ")");
        }
    }

    @Nested
    @DisplayName("Part 3 -- three-point boundary analysis on the two target limits")
    class BoundaryValues {

        @ParameterizedTest(name = "{0} | vip | {1} | expect {2}")
        @CsvSource({
                "TC10, 9,  REJECTED",    // L-1: one below the VIP lower limit
                "TC11, 10, ACCEPTED",    // L:   exactly on it -- inclusive
                "TC12, 11, ACCEPTED"     // L+1: one above
        })
        @DisplayName("VIP lower bracket limit (10)")
        void vipLowerLimit(String testCase, int level, AccessDecision expected) {
            // TC11 is the one that fails if the implementation used > instead of >=.
            // TC10 and TC12 both pass against that mutation.
            assertEquals(expected, launcher.checkBetaAccess("vip", level), testCase);
        }

        @ParameterizedTest(name = "{0} | standard | {1} | expect {2}")
        @CsvSource({
                "TC13, 49, ACCEPTED",    // L-1
                "TC14, 50, ACCEPTED",    // L:   inclusive upper bound
                "TC15, 51, REJECTED"     // L+1
        })
        @DisplayName("Standard upper bracket limit (50)")
        void standardUpperLimit(String testCase, int level, AccessDecision expected) {
            assertEquals(expected, launcher.checkBetaAccess("standard", level), testCase);
        }
    }

    @Nested
    @DisplayName("The remaining boundaries the task did not ask for, but that exist")
    class OtherBoundaries {

        @ParameterizedTest(name = "level {0} -> {1}")
        @CsvSource({
                "-1,  INVALID",     // below the system minimum
                "0,   INVALID",     // the stated invalid boundary
                "1,   REJECTED",    // first VALID level: in range, outside every bracket
                "100, REJECTED",    // last valid level
                "101, INVALID"      // the stated invalid boundary
        })
        @DisplayName("system limits 1 and 100, tested from both sides")
        void systemLimits(int level, AccessDecision expected) {
            assertEquals(expected, launcher.checkBetaAccess("vip", level));
        }

        @ParameterizedTest(name = "standard bracket boundary {0} -> {1}")
        @CsvSource({"19, REJECTED", "20, ACCEPTED"})
        @DisplayName("standard lower bracket limit (20)")
        void standardLowerLimit(int level, AccessDecision expected) {
            assertEquals(expected, launcher.checkBetaAccess("standard", level));
        }

        @ParameterizedTest(name = "vip bracket boundary {0} -> {1}")
        @CsvSource({"80, ACCEPTED", "81, REJECTED"})
        @DisplayName("vip upper bracket limit (80)")
        void vipUpperLimit(int level, AccessDecision expected) {
            assertEquals(expected, launcher.checkBetaAccess("vip", level));
        }
    }

    @Nested
    @DisplayName("ATC3 -- what counts as an invalid account type")
    class InvalidAccountTypes {

        @ParameterizedTest(name = "accountType {0} -> INVALID")
        @CsvSource(value = {"admin", "STANDARD", "Vip", "'  '", "''", "NULL"}, nullValues = "NULL")
        void invalidAccountTypes(String accountType) {
            // Case matters: "VIP" is not "vip". Worth stating explicitly in your answer,
            // because a grader reading "vip" as case-insensitive would mark it differently.
            assertEquals(AccessDecision.INVALID, launcher.checkBetaAccess(accountType, 35));
        }
    }
}
