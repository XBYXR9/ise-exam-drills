package ise.testing.s12_voidstate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * THE LOST-POINTS SCENARIO.
 *
 * Exam feedback: "Your test for registering members passed, but the registerMember()
 * method was broken and did not register any members."
 *
 * A void method gives you nothing to assert on directly. The only evidence that it
 * did its job is the state of the object afterwards -- so every test here reads the
 * state back and checks an exact number, a membership flag and a negative case.
 */
public class ClubRegistryStateTest {

    private ClubRegistry registry;
    private Member ann;
    private Member bob;

    protected ClubRegistry newRegistry() {
        return new ClubRegistry();
    }

    @BeforeEach
    void setUp() {
        registry = newRegistry();
        ann = new Member("Ann");
        bob = new Member("Bob");
    }

    @Test
    @DisplayName("a new registry has exactly zero members, asserted as a number")
    void newRegistryHasNoMembers() {
        // "isEmpty() is true" alone would also hold for a getMembers() that returns a
        // fresh empty list every time. The explicit 0 is the stronger statement.
        assertEquals(0, registry.getMemberCount());
        assertEquals(0, registry.getMembers().size());
        assertTrue(registry.getMembers().isEmpty());
    }

    @Test
    @DisplayName("registerMember actually stores the member, checked by count, list and lookup")
    void registerMemberActuallyRegisters() {
        registry.registerMember(ann);

        // Three independent witnesses, because an implementation can break any one of
        // them alone: a counter that increments without storing, a list that stores
        // without counting, a contains() that always answers false.
        assertEquals(1, registry.getMemberCount());
        assertTrue(registry.getMembers().contains(ann));
        assertTrue(registry.isRegistered(ann));
    }

    @Test
    @DisplayName("registering two members gives a count of exactly two, not just more than zero")
    void registeringTwoMembersCountsBoth() {
        registry.registerMember(ann);
        registry.registerMember(bob);

        // An implementation that overwrites instead of appending passes a "not empty"
        // check and fails this one.
        assertEquals(2, registry.getMemberCount());
        assertTrue(registry.getMembers().contains(ann));
        assertTrue(registry.getMembers().contains(bob));
    }

    @Test
    @DisplayName("removeMember removes only that member")
    void removeMemberRemovesOnlyThatOne() {
        registry.registerMember(ann);
        registry.registerMember(bob);

        registry.removeMember(ann);

        assertEquals(1, registry.getMemberCount());
        assertFalse(registry.isRegistered(ann));   // negative check
        assertTrue(registry.isRegistered(bob));    // and the survivor
    }

    @Test
    @DisplayName("registering null throws AND leaves the registry untouched")
    void nullMemberIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> registry.registerMember(null));

        // The state check catches an implementation that adds first and validates after.
        assertEquals(0, registry.getMemberCount());
    }

    @Test
    @DisplayName("WEAK TEST, kept as a warning: this passes against an empty method body")
    void thisIsTheTestThatScoredZero() {
        // Exactly the shape that earned the feedback. It asserts that registerMember
        // did not throw, and nothing else. Delete the body of registerMember and this
        // test is still green. Never submit this.
        assertDoesNotThrow(() -> registry.registerMember(ann));
    }
}
