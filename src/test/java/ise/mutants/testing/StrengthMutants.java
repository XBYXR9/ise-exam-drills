package ise.mutants.testing;

import ise.testing.s12_voidstate.ClubRegistry;
import ise.testing.s12_voidstate.ClubRegistryStateTest;
import ise.testing.s12_voidstate.Member;
import ise.testing.s13_calculations.InvoiceCalculator;
import ise.testing.s13_calculations.InvoiceCalculatorTest;
import ise.testing.s14_query.Book;
import ise.testing.s14_query.BookShelf;
import ise.testing.s14_query.BookShelfQueryTest;
import ise.testing.s15_observers.Etf;
import ise.testing.s15_observers.ObserverAndCallbackTest;
import ise.testing.s16_contracts.Isbn;
import ise.testing.s16_contracts.IsbnContractTest;
import ise.testing.s17_boundaries.Account;
import ise.testing.s17_boundaries.AccountBoundaryTest;
import org.junit.jupiter.api.Tag;

import java.util.List;

/** MUTANTS for testing scenarios 12 to 17 -- the strength-critical ones. */
final class StrengthMutants {

    private StrengthMutants() {
    }
}

// ---------------------------------------------------------------------------
// s12 -- MUTATION: registerMember does nothing.
// This is the literal exam feedback: "the registerMember() method was broken and
// did not register any members".
// ---------------------------------------------------------------------------
class ClubRegistryThatRegistersNobody extends ClubRegistry {

    @Override
    public void registerMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member must not be null");
        }
        // the add is gone
    }
}

@Tag("mutant")
class ClubRegistryStateTest_RegistersNobody extends ClubRegistryStateTest {

    @Override
    protected ClubRegistry newRegistry() {
        return new ClubRegistryThatRegistersNobody();
    }
    // CAUGHT BY: registerMemberActuallyRegisters and registeringTwoMembersCountsBoth.
    // NOT caught by thisIsTheTestThatScoredZero -- which is exactly why that method is
    // in the suite, labelled as the weak test to never submit.
}

// ---------------------------------------------------------------------------
// s12 -- MUTATION: the collection is overwritten instead of appended to
// ---------------------------------------------------------------------------
class ClubRegistryThatKeepsOnlyTheLast extends ClubRegistry {

    @Override
    public void registerMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member must not be null");
        }
        getMembers().clear();
        getMembers().add(member);
    }
}

@Tag("mutant")
class ClubRegistryStateTest_KeepsOnlyTheLast extends ClubRegistryStateTest {

    @Override
    protected ClubRegistry newRegistry() {
        return new ClubRegistryThatKeepsOnlyTheLast();
    }
    // CAUGHT BY: registeringTwoMembersCountsBoth -- assertEquals(2, ...). A test that
    // only asserted "the registry is not empty" would survive this mutation.
}

// ---------------------------------------------------------------------------
// s13 -- MUTATION: operator swapped, VAT is added instead of applied
// ---------------------------------------------------------------------------
class InvoiceCalculatorWithWrongVat extends InvoiceCalculator {

    @Override
    public double gross(double unitPrice, int quantity) {
        return net(unitPrice, quantity) + InvoiceCalculator.VAT_RATE;   // + instead of x
    }
}

@Tag("mutant")
class InvoiceCalculatorTest_WrongVat extends InvoiceCalculatorTest {

    @Override
    protected InvoiceCalculator newCalculator() {
        return new InvoiceCalculatorWithWrongVat();
    }
    // CAUGHT BY: normalGross. It is caught only because the test uses 12.50 x 3.
    // With a net of 0.00 both formulas return 0.19 vs 0.00... and with a net of 1.00
    // they are far closer. Pick inputs where wrong arithmetic gives a visibly wrong number.
}

// ---------------------------------------------------------------------------
// s13 -- MUTATION: inverted condition, the discount is applied backwards
// ---------------------------------------------------------------------------
class InvoiceCalculatorWithInvertedDiscount extends InvoiceCalculator {

    @Override
    public double discounted(double amount, double rate) {
        if (rate < 0.0 || rate > 1.0) {
            throw new IllegalArgumentException("Discount rate must be between 0 and 1");
        }
        return amount * rate;   // mutated from amount * (1 - rate)
    }
}

@Tag("mutant")
class InvoiceCalculatorTest_InvertedDiscount extends InvoiceCalculatorTest {

    @Override
    protected InvoiceCalculator newCalculator() {
        return new InvoiceCalculatorWithInvertedDiscount();
    }
    // CAUGHT BY: partialDiscount (33.75 vs 3.75) and zeroDiscount (37.50 vs 0.00).
    // fullDiscount does NOT catch it: 1.0 makes both formulas agree at 37.50... no,
    // it returns 37.50 instead of 0.00, so it does. But a suite testing ONLY rate 0.5
    // on an amount of 0.0 would catch nothing at all.
}

// ---------------------------------------------------------------------------
// s14 -- MUTATION: the filter is skipped, everything comes back
// ---------------------------------------------------------------------------
class BookShelfThatFiltersNothing extends BookShelf {

    private final List<Book> all = new java.util.ArrayList<>();

    @Override
    public void add(Book book) {
        super.add(book);
        all.add(book);
    }

    @Override
    public List<Book> getAvailableBooks() {
        return all;   // the isBorrowed check is gone
    }
}

@Tag("mutant")
class BookShelfQueryTest_FiltersNothing extends BookShelfQueryTest {

    @Override
    protected BookShelf newShelf() {
        return new BookShelfThatFiltersNothing();
    }
    // CAUGHT BY: filterKeepsOnlyAvailableBooks -- the exact size AND the NEGATIVE
    // assertFalse(available.contains(dune)). Checking only that Solaris is present
    // would pass against a filter that returns the entire shelf.
}

// ---------------------------------------------------------------------------
// s15 -- MUTATION: detach does nothing
// ---------------------------------------------------------------------------
class EtfThatNeverDetaches extends Etf {

    @Override
    public void detach(ise.testing.s15_observers.ChartView view) {
        // body deleted
    }
}

@Tag("mutant")
class ObserverAndCallbackTest_NeverDetaches extends ObserverAndCallbackTest {

    @Override
    protected Etf newEtf() {
        return new EtfThatNeverDetaches();
    }
    // CAUGHT BY: detachedViewIsNotNotified and detachRemovesOnlyThatView, both via
    // assertEquals(0, updateCount) -- the assertion that a call did NOT happen.
}

// ---------------------------------------------------------------------------
// s16 -- MUTATION: equals without a matching hashCode
// ---------------------------------------------------------------------------
class IsbnWithBrokenHashCode extends Isbn {

    IsbnWithBrokenHashCode(String code, String title) {
        super(code, title);
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);   // no longer derived from the code
    }
}

@Tag("mutant")
class IsbnContractTest_BrokenHashCode extends IsbnContractTest {

    @Override
    protected Isbn newIsbn(String code, String title) {
        return new IsbnWithBrokenHashCode(code, title);
    }
    // CAUGHT BY: equalObjectsShareAHashCode and hashSetDeduplicates. The equality
    // tests alone all pass -- a hash-based collection is what exposes the broken pair.
}

// ---------------------------------------------------------------------------
// s17 -- MUTATION: off-by-one at the overdraft limit (< becomes <=)
// ---------------------------------------------------------------------------
class AccountWithOffByOneLimit extends Account {

    private double balance;

    AccountWithOffByOneLimit(double openingBalance) {
        super(openingBalance);
        this.balance = openingBalance;
    }

    @Override
    public double withdraw(double amount) {
        if (amount <= 0.0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        double newBalance = balance - amount;
        if (newBalance <= Account.OVERDRAFT_LIMIT) {   // mutated from <
            throw new IllegalArgumentException("Overdraft limit exceeded");
        }
        balance = newBalance;
        return balance;
    }

    @Override
    public double getBalance() {
        return balance;
    }
}

@Tag("mutant")
class AccountBoundaryTest_OffByOneLimit extends AccountBoundaryTest {

    @Override
    protected Account newAccount(double openingBalance) {
        return new AccountWithOffByOneLimit(openingBalance);
    }
    // CAUGHT BY: exactlyAtTheOverdraftLimit, and by nothing else in the class. The
    // L-1 and L+1 cases both still pass. That is the entire argument for boundary testing.
}
