package ise.mocking.s09_matchers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.anyDouble;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The single most common EasyMock compile-clean-but-broken mistake: one matcher and
 * one raw value in the same call. It looks perfectly reasonable and it is illegal.
 *
 * Both halves are here: the mistake (proved to throw) and the eq() fix (proved to work).
 */
class MixedMatcherErrorTest {

    @Test
    @DisplayName("WRONG: a raw value beside a matcher throws IllegalStateException at record time")
    void mixingRawValueAndMatcherIsIllegal() {
        TransferGateway gateway = createMock(TransferGateway.class);

        IllegalStateException error = assertThrows(IllegalStateException.class, () -> {
            // transfer takes two arguments. anyDouble() registers ONE matcher, so
            // EasyMock expects two and finds one. The raw "DE89" is not a matcher.
            expect(gateway.transfer("DE89", anyDouble())).andReturn(true);
            replay(gateway);
        });

        // The message is worth memorising -- it names the exact counts.
        assertTrue(error.getMessage().contains("matchers expected"),
                "unexpected message: " + error.getMessage());
    }

    @Test
    @DisplayName("RIGHT: wrap the fixed argument in eq() so every argument is a matcher")
    void eqFixesTheMixedCall() {
        TransferGateway gateway = createMock(TransferGateway.class);

        expect(gateway.transfer(eq("DE89"), anyDouble())).andReturn(true);
        replay(gateway);

        assertTrue(new TransferService(gateway).send("DE89", 50.0));
        verify(gateway);
    }
}
