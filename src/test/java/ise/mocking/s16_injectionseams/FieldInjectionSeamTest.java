package ise.mocking.s16_injectionseams;

import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SEAM C -- a private field with neither constructor argument nor setter.
 * Reflection is the only way in, and EasyMockExtension does it for you: it matches
 * each @Mock to a SUT field by TYPE first, then by field NAME when the type is
 * ambiguous. That is why naming the mock after the SUT field is worth the keystrokes.
 */
@ExtendWith(EasyMockExtension.class)
class FieldInjectionSeamTest {

    @TestSubject
    private FieldInjectedCoffeeMachine coffeeMachine = new FieldInjectedCoffeeMachine();

    @Mock
    private WaterPump pump;

    @Test
    @DisplayName("SEAM C: the extension writes the mock into the private field for you")
    void fieldSeam() {
        expect(pump.pump(250)).andReturn(true);
        replay(pump);

        assertTrue(coffeeMachine.brew(250));
        verify(pump);
    }
}
