package ise.mocking.s12_callorder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * "A must happen before B" is the one requirement a default mock cannot express.
 * Both controllers below call the same two methods exactly once each; only the
 * sequence differs, so only createStrictMock can tell them apart.
 */
class AirlockControllerOrderTest {

    @Test
    @DisplayName("a strict mock accepts sealing the inner door before opening the outer one")
    void correctOrderPasses() {
        Airlock airlock = createStrictMock(Airlock.class);
        expect(airlock.sealInnerDoor()).andReturn(true);   // must happen FIRST
        expect(airlock.openOuterDoor()).andReturn(true);   // must happen SECOND
        replay(airlock);

        assertTrue(new AirlockController(airlock).cycle());

        verify(airlock);
    }

    @Test
    @DisplayName("a strict mock rejects the controller that opens the outer door first")
    void wrongOrderFailsOnAStrictMock() {
        Airlock airlock = createStrictMock(Airlock.class);
        expect(airlock.sealInnerDoor()).andReturn(true);
        expect(airlock.openOuterDoor()).andReturn(true);
        replay(airlock);

        // The broken controller is a real class in src/main -- this is the proof that
        // the test goes red on a wrong implementation instead of merely claiming to.
        assertThrows(AssertionError.class, () -> new UnsafeAirlockController(airlock).cycle());
    }

    @Test
    @DisplayName("a default mock lets the unsafe order through, which is why order needs strict")
    void wrongOrderSlipsPastADefaultMock() {
        Airlock airlock = createMock(Airlock.class);
        expect(airlock.sealInnerDoor()).andReturn(true);
        expect(airlock.openOuterDoor()).andReturn(true);
        replay(airlock);

        assertDoesNotThrow(() -> new UnsafeAirlockController(airlock).cycle());
        assertDoesNotThrow(() -> verify(airlock));
    }
}
