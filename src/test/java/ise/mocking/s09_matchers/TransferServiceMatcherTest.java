package ise.mocking.s09_matchers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.and;
import static org.easymock.EasyMock.anyDouble;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.contains;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.gt;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.isNull;
import static org.easymock.EasyMock.lt;
import static org.easymock.EasyMock.matches;
import static org.easymock.EasyMock.not;
import static org.easymock.EasyMock.notNull;
import static org.easymock.EasyMock.or;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.same;
import static org.easymock.EasyMock.startsWith;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The matcher catalogue.
 *
 * THE ONE RULE: the moment you use a matcher for one argument of a call, every
 * other argument of that same call must be a matcher too. Wrap the fixed ones in
 * eq(). Breaking this rule is the "N matchers expected, M recorded" error, and it
 * is demonstrated and then fixed at the bottom of this class.
 */
class TransferServiceMatcherTest {

    @Test
    @DisplayName("eq() matches an exact value, and is what you wrap a raw argument in")
    void exactValue() {
        TransferGateway gateway = createMock(TransferGateway.class);
        expect(gateway.transfer(eq("DE89"), eq(50.0))).andReturn(true);
        replay(gateway);

        assertTrue(new TransferService(gateway).send("DE89", 50.0));
        verify(gateway);
    }

    @Test
    @DisplayName("anyString()/anyDouble() accept any value of that type")
    void anyValueOfAType() {
        TransferGateway gateway = createMock(TransferGateway.class);
        expect(gateway.transfer(anyString(), anyDouble())).andReturn(true);
        replay(gateway);

        assertTrue(new TransferService(gateway).send("DE89", 50.0));
        // Careful: anyString() no longer proves WHICH account was credited. Only relax
        // an argument you genuinely do not care about.
        verify(gateway);
    }

    @Test
    @DisplayName("isA() checks the runtime type of the argument")
    void typeMatcher() {
        TransferGateway gateway = createMock(TransferGateway.class);
        expect(gateway.lookup("ann")).andReturn(new Customer("ann"));
        expect(gateway.archive(isA(Customer.class))).andReturn(true);
        replay(gateway);

        assertTrue(new TransferService(gateway).archiveByName("ann"));
        verify(gateway);
    }

    @Test
    @DisplayName("isNull() matches a null argument, and an unknown name never reaches archive()")
    void nullMatcher() {
        TransferGateway gateway = createMock(TransferGateway.class);
        expect(gateway.lookup(isNull())).andReturn(null);
        // archive() is deliberately NOT recorded: calling it with a null customer
        // would be the bug, and on a default mock that call fails the test at once.
        replay(gateway);

        assertFalse(new TransferService(gateway).archiveByName(null));
        verify(gateway);
    }

    @Test
    @DisplayName("notNull() matches any non-null argument")
    void notNullMatcher() {
        TransferGateway gateway = createMock(TransferGateway.class);
        expect(gateway.lookup(notNull())).andReturn(new Customer("bob"));
        expect(gateway.archive(notNull())).andReturn(true);
        replay(gateway);

        assertTrue(new TransferService(gateway).archiveByName("bob"));
        verify(gateway);
    }

    @Test
    @DisplayName("same() demands the very same instance, not merely an equal one")
    void identityMatcher() {
        TransferGateway gateway = createMock(TransferGateway.class);
        Customer ann = new Customer("ann");
        expect(gateway.lookup("ann")).andReturn(ann);
        // same(ann) proves the SUT forwarded the object it got back, instead of
        // constructing a fresh, look-alike Customer of its own.
        expect(gateway.archive(same(ann))).andReturn(true);
        replay(gateway);

        assertTrue(new TransferService(gateway).archiveByName("ann"));
        verify(gateway);
    }

    @Test
    @DisplayName("matches()/contains()/startsWith() match a String argument by shape")
    void stringMatchers() {
        TransferGateway regex = createMock(TransferGateway.class);
        expect(regex.lookup(matches("[a-z]+"))).andReturn(new Customer("ann"));
        expect(regex.archive(notNull())).andReturn(true);
        replay(regex);
        assertTrue(new TransferService(regex).archiveByName("ann"));
        verify(regex);

        TransferGateway substring = createMock(TransferGateway.class);
        expect(substring.lookup(contains("nn"))).andReturn(null);
        replay(substring);
        assertFalse(new TransferService(substring).archiveByName("ann"));
        verify(substring);

        TransferGateway prefix = createMock(TransferGateway.class);
        expect(prefix.lookup(startsWith("a"))).andReturn(null);
        replay(prefix);
        assertFalse(new TransferService(prefix).archiveByName("ann"));
        verify(prefix);
    }

    @Test
    @DisplayName("and(gt, lt) expresses a numeric range")
    void combinedRangeMatcher() {
        TransferGateway gateway = createMock(TransferGateway.class);
        expect(gateway.transfer(anyString(), and(gt(0.0), lt(100.0)))).andReturn(true);
        replay(gateway);

        assertTrue(new TransferService(gateway).send("DE89", 50.0));
        verify(gateway);
    }

    @Test
    @DisplayName("not() and or() combine matchers into a single condition")
    void notAndOrMatchers() {
        TransferGateway negated = createMock(TransferGateway.class);
        expect(negated.transfer(anyString(), not(eq(0.0)))).andReturn(true);
        replay(negated);
        assertTrue(new TransferService(negated).send("DE89", 50.0));
        verify(negated);

        TransferGateway alternatives = createMock(TransferGateway.class);
        expect(alternatives.lookup(or(eq("ann"), eq("bob")))).andReturn(null);
        replay(alternatives);
        assertFalse(new TransferService(alternatives).archiveByName("bob"));
        verify(alternatives);
    }
}
