package ise.mutants.testing;

import ise.testing.s02_aaa.BankAccount;
import ise.testing.s02_aaa.BankAccountAaaTest;
import ise.testing.s03_assertions.AllAssertionsTest;
import ise.testing.s03_assertions.SeminarRoom;
import ise.testing.s04_exceptions.Enrolment;
import ise.testing.s04_exceptions.EnrolmentExceptionTest;
import ise.testing.s05_assertall.GroupedAssertionTest;
import ise.testing.s05_assertall.ShoppingBasket;
import ise.testing.s06_parameterized.GradeConverterParameterizedTest;
import ise.testing.s07_extras.TestAnnotationExtrasTest;
import ise.testing.s07_extras.TokenGenerator;
import org.junit.jupiter.api.Tag;

/**
 * MUTANTS for testing scenarios 02 to 07.
 *
 * Each pair is: one broken SUT, plus a test class that inherits the ORIGINAL
 * assertions unchanged and only swaps in the broken SUT. Nothing about the test is
 * weakened -- if it still passes here, the test was never worth anything.
 *
 * Every test class is @Tag("mutant"), so `gradlew test` skips them (build.gradle
 * excludes that tag) and only MutationDrillTest runs them, asserting they FAIL.
 */
final class CoreMutants {

    private CoreMutants() {
    }
}

// ---------------------------------------------------------------------------
// s02 -- MUTATION: method body deleted
// ---------------------------------------------------------------------------
class BankAccountWithNoOpDeposit extends BankAccount {

    BankAccountWithNoOpDeposit(double openingBalance) {
        super(openingBalance);
    }

    @Override
    public void deposit(double amount) {
        // body deleted
    }
}

@Tag("mutant")
class BankAccountAaaTest_NoOpDeposit extends BankAccountAaaTest {

    @Override
    protected BankAccount newAccount(double openingBalance) {
        return new BankAccountWithNoOpDeposit(openingBalance);
    }
    // CAUGHT BY: depositIncreasesTheBalance -- assertEquals(150.0, getBalance())
}

// ---------------------------------------------------------------------------
// s02 -- MUTATION: validation runs AFTER the state change
// ---------------------------------------------------------------------------
class BankAccountThatValidatesTooLate extends BankAccount {

    private double balance;

    BankAccountThatValidatesTooLate(double openingBalance) {
        super(openingBalance);
        this.balance = openingBalance;
    }

    @Override
    public void deposit(double amount) {
        balance += amount;                     // mutated: state first ...
        if (amount <= 0.0) {                   // ... guard second
            throw new IllegalArgumentException("Deposit must be positive");
        }
    }

    @Override
    public double getBalance() {
        return balance;
    }
}

@Tag("mutant")
class BankAccountAaaTest_ValidatesTooLate extends BankAccountAaaTest {

    @Override
    protected BankAccount newAccount(double openingBalance) {
        return new BankAccountThatValidatesTooLate(openingBalance);
    }
    // CAUGHT BY: negativeDepositIsRejected -- the assertEquals(100.0) AFTER the
    // assertThrows. Without that second line this mutant would survive.
}

// ---------------------------------------------------------------------------
// s03 -- MUTATION: returns a constant
// ---------------------------------------------------------------------------
class SeminarRoomWithConstantOccupancy extends SeminarRoom {

    SeminarRoomWithConstantOccupancy() {
        super("MI-01", 4);
    }

    @Override
    public double occupancyRate() {
        return 0.25;   // right answer for one attendee, wrong for every other count
    }

    @Override
    public void admit(String attendee) {
        // body deleted
    }
}

@Tag("mutant")
class AllAssertionsTest_ConstantOccupancy extends AllAssertionsTest {

    @Override
    protected SeminarRoom newRoom() {
        return new SeminarRoomWithConstantOccupancy();
    }
    // CAUGHT BY: booleans (assertFalse on the non-empty list) and arraysAndIterables
}

// ---------------------------------------------------------------------------
// s04 -- MUTATION: the guard never fires
// ---------------------------------------------------------------------------
class EnrolmentThatNeverValidates extends Enrolment {

    private int semester;

    EnrolmentThatNeverValidates() {
        super("Yahya", 3);
        this.semester = 3;
    }

    @Override
    public void setSemester(int semester) {
        this.semester = semester;   // guard removed
    }

    @Override
    public int getSemester() {
        return semester;
    }
}

@Tag("mutant")
class EnrolmentExceptionTest_NeverValidates extends EnrolmentExceptionTest {

    @Override
    protected Enrolment newEnrolment() {
        return new EnrolmentThatNeverValidates();
    }
    // CAUGHT BY: setterThrows, messageIsAsserted, and tryCatchMakesATestUseless --
    // the last one only because of the trailing assertEquals, which is the point.
}

// ---------------------------------------------------------------------------
// s05 -- MUTATION: half the operation is skipped
// ---------------------------------------------------------------------------
class ShoppingBasketThatForgetsTheTotal extends ShoppingBasket {

    @Override
    public void add(String item, double price) {
        getItems().add(item);   // the item is stored, the total is not updated
    }
}

@Tag("mutant")
class GroupedAssertionTest_ForgetsTheTotal extends GroupedAssertionTest {

    @Override
    protected ShoppingBasket newBasket() {
        return new ShoppingBasketThatForgetsTheTotal();
    }
    // CAUGHT BY: filledBasketReportsBothProperties -- and assertAll reports it
    // alongside the size check rather than instead of it.
}

// ---------------------------------------------------------------------------
// s06 -- MUTATION: off-by-one at a boundary (>= 90 becomes > 90)
// ---------------------------------------------------------------------------
@Tag("mutant")
class GradeConverterParameterizedTest_OffByOne extends GradeConverterParameterizedTest {

    @Override
    protected double toGrade(int points) {
        if (points < 0 || points > 100) {
            throw new IllegalArgumentException("Points must be between 0 and 100");
        }
        if (points > 90) return 1.0;    // mutated from >=
        if (points >= 80) return 2.0;
        if (points >= 70) return 3.0;
        if (points >= 50) return 4.0;
        return 5.0;
    }
    // CAUGHT BY: gradeTable, on the row "90 points -> grade 1.0" only. A table
    // without the exact boundary value would let this through.
}

// ---------------------------------------------------------------------------
// s07 -- MUTATION: returns a constant zero
// ---------------------------------------------------------------------------
class TokenGeneratorReturningZero extends TokenGenerator {

    @Override
    public int nextToken() {
        return 0;
    }

    @Override
    public long factorial(int n) {
        return 0L;
    }
}

@Tag("mutant")
class TestAnnotationExtrasTest_ReturnsZero extends TestAnnotationExtrasTest {

    @Override
    protected TokenGenerator newGenerator() {
        return new TokenGeneratorReturningZero();
    }
    // CAUGHT BY: tokenIsAlwaysPositive, finishesQuickly, assertTimeoutOnOneCall --
    // note that the timeout assertions only catch it because they also assert the RESULT.
}
