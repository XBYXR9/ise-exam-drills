package ise.drill;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.ArrayList;
import java.util.List;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PART 4 -- THE MUTATION DRILL.
 *
 * Everything in this project exists to answer one question: would my test go RED if
 * the implementation were broken? Artemis answers it by running your tests against a
 * deliberately mutated implementation. This class answers it here, before you submit.
 *
 * HOW IT WORKS
 *   Each mutant is a real JUnit test class carrying the ORIGINAL assertions and a
 *   BROKEN SUT. Those classes are tagged "mutant" and excluded from the normal suite
 *   (build.gradle: useJUnitPlatform { excludeTags 'mutant' }), because they are
 *   supposed to fail.
 *
 *   This class uses the JUnit Platform Launcher to execute each of them
 *   programmatically and asserts that the failure count is greater than zero. So:
 *
 *     gradlew.bat test             -> green, and the drill is part of that green
 *     gradlew.bat mutationReport   -> the same run, with the summary table printed
 *
 *   A mutant that PASSES fails this class. That is the alarm you want: it means the
 *   corresponding test cannot detect that mutation and is worth zero points.
 */
class MutationDrillTest {

    /** One row of the study table: which test class, fed which broken implementation. */
    private record Mutant(String area, String scenario, String mutation, String mutantTestClass) {
    }

    private static final List<Mutant> MUTANTS = List.of(
            // ---- TESTING ------------------------------------------------
            new Mutant("testing", "s02_aaa", "method body deleted (deposit does nothing)",
                    "ise.mutants.testing.BankAccountAaaTest_NoOpDeposit"),
            new Mutant("testing", "s02_aaa", "guard moved after the state change",
                    "ise.mutants.testing.BankAccountAaaTest_ValidatesTooLate"),
            new Mutant("testing", "s03_assertions", "returns a constant / admit does nothing",
                    "ise.mutants.testing.AllAssertionsTest_ConstantOccupancy"),
            new Mutant("testing", "s04_exceptions", "validation guard removed",
                    "ise.mutants.testing.EnrolmentExceptionTest_NeverValidates"),
            new Mutant("testing", "s05_assertall", "half the operation skipped (total not updated)",
                    "ise.mutants.testing.GroupedAssertionTest_ForgetsTheTotal"),
            new Mutant("testing", "s06_parameterized", "off-by-one at a boundary (>= becomes >)",
                    "ise.mutants.testing.GradeConverterParameterizedTest_OffByOne"),
            new Mutant("testing", "s07_extras", "returns a constant zero",
                    "ise.mutants.testing.TestAnnotationExtrasTest_ReturnsZero"),
            new Mutant("testing", "s08_nested_ordered", "remove wipes the whole collection",
                    "ise.mutants.testing.CatalogueStructureTest_RemovesEverything"),
            new Mutant("testing", "s10_collections", "operation swapped (prepend instead of append)",
                    "ise.mutants.testing.PlaylistCollectionTest_Prepends"),
            new Mutant("testing", "s10_collections", "remove deletes every element",
                    "ise.mutants.testing.PlaylistCollectionTest_RemovesEverything"),
            new Mutant("testing", "s11_validation", "inverted boundary (zero wrongly rejected)",
                    "ise.mutants.testing.CatalogueItemValidationTest_RejectsZero"),
            new Mutant("testing", "s12_voidstate", "registerMember does nothing -- THE exam defect",
                    "ise.mutants.testing.ClubRegistryStateTest_RegistersNobody"),
            new Mutant("testing", "s12_voidstate", "collection overwritten instead of appended",
                    "ise.mutants.testing.ClubRegistryStateTest_KeepsOnlyTheLast"),
            new Mutant("testing", "s13_calculations", "operator swapped (VAT added, not applied)",
                    "ise.mutants.testing.InvoiceCalculatorTest_WrongVat"),
            new Mutant("testing", "s13_calculations", "discount formula inverted",
                    "ise.mutants.testing.InvoiceCalculatorTest_InvertedDiscount"),
            new Mutant("testing", "s14_query", "filter skipped, everything returned",
                    "ise.mutants.testing.BookShelfQueryTest_FiltersNothing"),
            new Mutant("testing", "s15_observers", "detach does nothing",
                    "ise.mutants.testing.ObserverAndCallbackTest_NeverDetaches"),
            new Mutant("testing", "s16_contracts", "hashCode no longer agrees with equals",
                    "ise.mutants.testing.IsbnContractTest_BrokenHashCode"),
            new Mutant("testing", "s17_boundaries", "off-by-one at the overdraft limit (< becomes <=)",
                    "ise.mutants.testing.AccountBoundaryTest_OffByOneLimit"),

            // ---- TESTING, exam exercises --------------------------------
            new Mutant("testing", "exam_library", "registerMember does nothing",
                    "ise.mutants.testing.LibraryTest_RegistersNobody"),
            new Mutant("testing", "exam_library", "the pre-registered member is missing",
                    "ise.mutants.testing.LibraryTest_NoHouseMember"),
            new Mutant("testing", "exam_library", "already-borrowed guard removed",
                    "ise.mutants.testing.LibraryTest_LendsTwice"),
            new Mutant("testing", "exam_library", "removeBook removes nothing",
                    "ise.mutants.testing.LibraryTest_NeverRemoves"),
            new Mutant("testing", "exam_cart", "addProduct does nothing",
                    "ise.mutants.testing.ShoppingCartTest_AddsNothing"),
            new Mutant("testing", "exam_cart", "total returns a constant zero",
                    "ise.mutants.testing.ShoppingCartTest_ZeroTotal"),
            new Mutant("testing", "exam_cart", "discount rate ignored",
                    "ise.mutants.testing.ShoppingCartTest_IgnoresDiscount"),
            new Mutant("testing", "exam_cart", "removeProduct clears the whole cart",
                    "ise.mutants.testing.ShoppingCartTest_ClearsOnRemove"),

            // ---- MOCKING ------------------------------------------------
            new Mutant("mocking", "s01_setup_annotations", "collaborator answer ignored",
                    "ise.mutants.mocking.TurnstileControllerTest_AlwaysOpens"),
            new Mutant("mocking", "s02_setup_manual", "collaborator answer ignored",
                    "ise.mutants.mocking.FineCollectorTest_AlwaysSucceeds"),
            new Mutant("mocking", "s03_setup_support", "collaborator called when it should be skipped",
                    "ise.mutants.mocking.OrderFulfilmentTest_PrintsAnyway"),
            new Mutant("mocking", "s05_stubbing", "collaborator consulted once, value reused",
                    "ise.mutants.mocking.BoxOfficeTest_ReusesOneNumber"),
            new Mutant("mocking", "s06_voidmethods", "loop body deleted, collaborator calls skipped",
                    "ise.mutants.mocking.SampleFreezerTest_LogsNothing"),
            new Mutant("mocking", "s07_callcounts", "off-by-one in the retry loop",
                    "ise.mutants.mocking.AlertDispatcherTest_OneRetryTooFew"),
            new Mutant("mocking", "s08_exceptions", "catch block emptied",
                    "ise.mutants.mocking.DonationServiceTest_EmptyCatch"),
            new Mutant("mocking", "s10_capture", "wrong object handed to the collaborator",
                    "ise.mutants.mocking.BillingServiceCaptureTest_WrongTotal"),
            new Mutant("mocking", "s13_multiplemocks", "collaborator called without the guard",
                    "ise.mutants.mocking.DispatchServiceTest_SkipsTheCheck"),

            // ---- MOCKING, exam exercises --------------------------------
            new Mutant("mocking", "exam_vehicle", "assign() called despite a failed engine check",
                    "ise.mutants.mocking.VehicleManagementSystemTest_IgnoresTheEngine"),
            new Mutant("mocking", "exam_vehicle", "assign() skipped entirely",
                    "ise.mutants.mocking.VehicleManagementSystemTest_NeverAssigns"),
            new Mutant("mocking", "exam_vehicle", "driver never told about the vehicle",
                    "ise.mutants.mocking.VehicleManagementSystemTest_ForgetsTheDriver"),
            new Mutant("mocking", "exam_docking", "dock() never executed",
                    "ise.mutants.mocking.DockingControlSystemTest_NeverDocks"),

            // ---- MOCK EXAM (Jun 2026) -----------------------------------
            new Mutant("mocking", "exam_smarthome", "register() never called on the device",
                    "ise.mutants.mockexam.SmartHomeTest_NeverRegisters"),
            new Mutant("mocking", "exam_smarthome", "duplicate check dropped",
                    "ise.mutants.mockexam.SmartHomeTest_AllowsDuplicates"),
            new Mutant("mocking", "exam_smarthome", "removeDevice reports success without removing",
                    "ise.mutants.mockexam.SmartHomeTest_NeverRemoves"),
            new Mutant("mocking", "exam_smarthome", "sendCommand does nothing",
                    "ise.mutants.mockexam.SmartHomeTest_SwallowsCommands"),
            new Mutant("mocking", "exam_smarthome", "unregistered device is commanded anyway",
                    "ise.mutants.mockexam.SmartHomeTest_CommandsAnything"),
            new Mutant("solid", "exam_enrollment", "PrerequisiteRule blocks courses with no prerequisite",
                    "ise.mutants.mockexam.PrerequisiteRuleTest_BlocksEverything"),
            new Mutant("solid", "exam_enrollment", "only the student is updated, not the course",
                    "ise.mutants.mockexam.EnrollmentServiceTest_ForgetsTheCourse"),
            new Mutant("solid", "exam_enrollment", "state mutated before the rules are evaluated",
                    "ise.mutants.mockexam.EnrollmentServiceTest_MutatesTooEarly"),
            new Mutant("solid", "exam_enrollment", "duplicate enrollment check missing",
                    "ise.mutants.mockexam.EnrollmentServiceTest_NoDuplicateCheck"),
            new Mutant("blackbox", "exam_gamelauncher", "off-by-one at the VIP lower limit (>= becomes >)",
                    "ise.mutants.mockexam.GameLauncherTest_OffByOneVipLimit"),
            new Mutant("acceptance", "exam_login", "invalid credentials are accepted",
                    "ise.mutants.mockexam.LoginServiceTest_TrustsEveryone"));

    /** Runs one mutant test class and reports how many of its tests failed. */
    private static TestExecutionSummary run(String testClassName) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClassName))
                // The launcher must NOT inherit the build's excludeTags filter, or it
                // would discover zero tests and report a vacuous success.
                .build();

        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.execute(request, listener);
        return listener.getSummary();
    }

    @Test
    @DisplayName("every broken implementation is caught by the test that is supposed to catch it")
    void everyMutantIsKilled() {
        List<String> survivors = new ArrayList<>();
        int killed = 0;

        System.out.println();
        System.out.println("================ MUTATION DRILL ================");
        System.out.printf("%-8s %-24s %-52s %s%n", "AREA", "SCENARIO", "MUTATION", "RESULT");
        System.out.println("-".repeat(110));

        for (Mutant mutant : MUTANTS) {
            TestExecutionSummary summary = run(mutant.mutantTestClass());

            long started = summary.getTestsStartedCount();
            long failed = summary.getTestsFailedCount();

            String result;
            if (started == 0) {
                result = "NO TESTS RAN";
                survivors.add(mutant.scenario() + " / " + mutant.mutation() + " (no tests discovered)");
            } else if (failed > 0) {
                result = "killed (" + failed + "/" + started + " red)";
                killed++;
            } else {
                // The alarm: the test suite is blind to this mutation.
                result = "SURVIVED -- test is worth 0 points";
                survivors.add(mutant.scenario() + " / " + mutant.mutation());
            }

            System.out.printf("%-8s %-24s %-52s %s%n",
                    mutant.area(), mutant.scenario(), mutant.mutation(), result);
        }

        System.out.println("-".repeat(110));
        System.out.printf("%d of %d mutations caught.%n", killed, MUTANTS.size());
        if (!survivors.isEmpty()) {
            System.out.println("SURVIVORS (fix the test, not the mutant):");
            survivors.forEach(s -> System.out.println("  - " + s));
        }
        System.out.println("===============================================");
        System.out.println();

        assertTrue(survivors.isEmpty(),
                "these mutations were NOT caught, so the corresponding tests cannot go red: " + survivors);
        assertEquals(MUTANTS.size(), killed);
    }

    @Test
    @DisplayName("the drill covers every scenario that has a mutable implementation")
    void coverageIsComplete() {
        long testingScenarios = MUTANTS.stream()
                .filter(m -> "testing".equals(m.area()))
                .map(Mutant::scenario)
                .distinct()
                .count();
        long mockingScenarios = MUTANTS.stream()
                .filter(m -> "mocking".equals(m.area()))
                .map(Mutant::scenario)
                .distinct()
                .count();

        // Scenarios without a mutable SUT are listed in docs/EXAM_DRILLS.md with the
        // reason: lifecycle demos, the assumption probe, the EasyMock error catalogue,
        // and the mock-type/call-order comparisons, which already assert their own
        // failures inline with assertThrows(AssertionError.class, ...).
        assertEquals(17, testingScenarios, "a testing scenario lost its mutant");
        assertEquals(12, mockingScenarios, "a mocking scenario lost its mutant");

        // The June 2026 mock exam added three areas that are not mocking or testing.
        long mockExamAreas = MUTANTS.stream()
                .map(Mutant::area)
                .filter(a -> "solid".equals(a) || "blackbox".equals(a) || "acceptance".equals(a))
                .distinct()
                .count();
        assertEquals(3, mockExamAreas, "a mock-exam area lost its mutant");
    }
}
